package com.mastercard.authentication.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bitsinharmony.recognito.MatchResult;
import com.mastercard.authentication.config.VoiceMatchConfig;
import com.mastercard.authentication.constant.Constants;
import com.mastercard.authentication.models.Customer;
import com.mastercard.authentication.models.CustomerVoiceData;
import com.mastercard.authentication.repository.AuthRepository;
import com.mastercard.authentication.repository.CustomerRepository;
import com.mastercard.authentication.repository.CustomerVoiceDataRepository;

@Service
public class AuthenticationService implements IAuthenticateService {

	@Autowired
	private AuthRepository authRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerVoiceDataRepository customerVoiceDataRepository;

	private boolean isMatched = true;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public CustomerVoiceData store(MultipartFile file, @Valid String email) throws Exception {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		CustomerVoiceData voiceSample = null;
		Customer customer = findCustomerByEmail(email);
		if (null == customer) {
			LOGGER.error("User not found");
			throw new Exception("User not found");
		}
		byte[] data = file.getBytes();
		voiceSample = new CustomerVoiceData(customer, fileName, data);
		voiceSample = authRepository.save(voiceSample);
		return voiceSample;
	}

	public CustomerVoiceData getFile(Long id) {
		return authRepository.findById(id).get();
	}

	public Customer findCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	@Override
	public boolean authenticateUser(MultipartFile file, @Valid int id)
			throws Exception {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		byte[] data = file.getBytes();

		String inputPath = System.getProperty(Constants.USER_HOME) + File.separatorChar + 
				Constants.VOICE_MASTER_AUTHENTICATION +  File.separatorChar + 
                Constants.INPUT+  File.separatorChar;
		
		String inputFilePathMatch = new StringBuilder(inputPath).toString();
		  File inputUserDir = new File(inputFilePathMatch);
		  if(!inputUserDir.exists()) {
		      boolean bool = inputUserDir.mkdirs();
		      if(bool){
		    	  LOGGER.info("Input Directory created successfully");
		      }else{
		    	  LOGGER.error("Sorry couldn’t create specified Input directory");
		      }
		  }

		File inputFile = createFile(inputPath + fileName, data);
		LOGGER.info("inputPath is:"+inputPath);
		LOGGER.info("inputfileName is:"+fileName);
		// Fetch samples from database
		List<CustomerVoiceData> dataList = customerVoiceDataRepository.findCustomerVoiceDataByUserID(id);

		if (!dataList.isEmpty()) {
			dataList.forEach(storedSample -> {

				List<MatchResult<String>> matches = null;
				File storedFile = null;
				String tempPath = System.getProperty(Constants.USER_HOME) + File.separatorChar + 
						Constants.VOICE_MASTER_AUTHENTICATION +  File.separatorChar + 
		                Constants.TEMP+  File.separatorChar;

				String filePathMatch = new StringBuilder(tempPath + id + File.separatorChar).toString();
				  File userDir = new File(filePathMatch);
				  if(!userDir.exists()) {
				      boolean bool = userDir.mkdirs();
				      if(bool){
				    	  LOGGER.info("Directory created successfully");
				      }else{
				    	  LOGGER.error("Sorry couldn’t create specified directory");
				      }
				  }

				try {
					storedFile = createFile(filePathMatch + storedSample.getName(), storedSample.getData());
				} catch (IOException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}

				// Match input voice sample with database sample
				VoiceMatchConfig voiceMatch = new VoiceMatchConfig(filePathMatch);
				LOGGER.info("filePathMatch is:"+filePathMatch);
				LOGGER.info("storedSample.getName() is:"+storedSample.getName());
				LOGGER.info("storedSample.getData() is:"+storedSample.getData());
				try {
					matches = voiceMatch.recognito.identify(inputFile);
				} catch (UnsupportedAudioFileException | IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
				if (null != matches) {
					matches.stream().forEach(f -> {
						isMatched = true;
						StringBuilder sb = new StringBuilder();

						if (f.getDistance() < 1.5) {
							LOGGER.info("Distance is:"+f.getDistance());
							sb.append("Input sample matched!");
							isMatched = isMatched && true;
						} else {
							sb.append("Input sample does not match!");
							isMatched = isMatched && false;
						}

						String matchesResult = new String("Identified: " + f.getKey() + " distance of "
								+ f.getDistance() + " with " + f.getLikelihoodRatio() + "% positive about it...");
						LOGGER.info(matchesResult);
						System.out.println(matchesResult);
					});
				}
				boolean isRemoved = inputFile.delete();
				if(isRemoved) {
					LOGGER.info("input file has been removed");
				}
				if(storedFile != null) {
					storedFile.delete();
				}
				if(userDir.exists()) {
					userDir.delete();
				}
			});
		}
		else {
			isMatched = false;
		}
		return isMatched;
		
	}


	private File createFile(String path, byte[] data) throws IOException {
		File inputFile = new File(path);
		FileOutputStream os1 = new FileOutputStream(inputFile);
		os1.write(data);
		os1.close();
		return inputFile;
	}

	@Override
	public CustomerVoiceData store(MultipartFile file, @Valid int id) throws IOException, Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
