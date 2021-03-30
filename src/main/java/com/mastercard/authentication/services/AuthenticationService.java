package com.mastercard.authentication.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.mastercard.authentication.models.UserVoiceDistance;
import com.mastercard.authentication.repository.AuthRepository;
import com.mastercard.authentication.repository.CustomerRepository;
import com.mastercard.authentication.repository.CustomerVoiceDataRepository;
import com.mastercard.authentication.repository.UserVoiceDistanceRepository;

@Service
public class AuthenticationService implements IAuthenticateService {

	@Autowired
	private AuthRepository authRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerVoiceDataRepository customerVoiceDataRepository;

	@Autowired
	UserVoiceDistanceRepository distanceRepository;

	@Value("${recongnito.voice.distance.offset:.05}")
	private Double offset;

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
	public boolean authenticateUser(MultipartFile file, @Valid int id) throws Exception {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		byte[] data = file.getBytes();
		boolean isMatched = false;

		String inputPath = System.getProperty(Constants.USER_HOME) + File.separatorChar
				+ Constants.VOICE_MASTER_AUTHENTICATION + File.separatorChar + Constants.INPUT + File.separatorChar;

		String inputFilePathMatch = new StringBuilder(inputPath).toString();
		File inputUserDir = new File(inputFilePathMatch);
		if (!inputUserDir.exists()) {
			boolean bool = inputUserDir.mkdirs();
			if (bool) {
				LOGGER.info("Input Directory created successfully");
			} else {
				LOGGER.error("Sorry couldn’t create specified Input directory");
			}
		}

		File inputFile = createFile(inputPath + fileName, data);
		LOGGER.info("inputPath is:" + inputPath);
		LOGGER.info("inputfileName is:" + fileName);
		// Fetch samples from database
		List<CustomerVoiceData> dataList = customerVoiceDataRepository.findCustomerVoiceDataByUserID(id);
		UserVoiceDistance voiceDistance = distanceRepository.findDistanceByUserId(id);
		if (!dataList.isEmpty()) {
			for (CustomerVoiceData storedSample : dataList) {
				List<MatchResult<String>> matches = null;
				File storedFile = null;
				String tempPath = System.getProperty(Constants.USER_HOME) + File.separatorChar
						+ Constants.VOICE_MASTER_AUTHENTICATION + File.separatorChar + Constants.TEMP
						+ File.separatorChar;

				String filePathMatch = new StringBuilder(tempPath + id + File.separatorChar).toString();
				File userDir = new File(filePathMatch);
				if (!userDir.exists()) {
					boolean bool = userDir.mkdirs();
					if (bool) {
						LOGGER.info("Directory created successfully");
					} else {
						LOGGER.error("Sorry couldn’t create specified directory");
					}
				}

				String newFileName = id + "_" + storedSample.getName();
				try {
					storedFile = createFile(filePathMatch + newFileName, storedSample.getData());
				} catch (IOException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}

				// Match input voice sample with database sample
				VoiceMatchConfig voiceMatch = new VoiceMatchConfig(filePathMatch);
				LOGGER.info("filePathMatch is:" + filePathMatch);
				LOGGER.info("storedSample.getName() is:" + newFileName);
				LOGGER.info("storedSample.getData() is:" + storedSample.getData());
				try {
					matches = voiceMatch.recognito.identify(inputFile);
				} catch (UnsupportedAudioFileException | IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
				if (null != matches) {
					matches.stream().forEach(match -> {
						LOGGER.debug("match.getKey() " + match.getKey());
						LOGGER.debug("match.getDistance() " + match.getDistance());
						LOGGER.debug("match.getLikelihoodRatio() " + match.getLikelihoodRatio());
					});
					isMatched = matches.stream().filter(f -> f.getDistance() <= voiceDistance.getMaxDistance()
							&& f.getDistance() >= voiceDistance.getMinDistance()).findAny().isPresent();
					if (isMatched) {
						break;
					}
				}
				boolean isRemoved = inputFile.delete();
				if (isRemoved) {
					LOGGER.info("input file has been removed");
				}
				if (storedFile != null) {
					storedFile.delete();
				}
				if (userDir.exists()) {
					userDir.delete();
				}
			}
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

	@Override
	public void calculateDistance(int id) {
		List<CustomerVoiceData> dataList = customerVoiceDataRepository.findCustomerVoiceDataByUserID(id);
		String tempPath = System.getProperty(Constants.USER_HOME) + File.separatorChar
				+ Constants.VOICE_MASTER_AUTHENTICATION + File.separatorChar + Constants.VOICE_SAMPLES
				+ File.separatorChar;
		String filePathMatch = new StringBuilder(tempPath + id + File.separatorChar).toString();

		if (!dataList.isEmpty()) {
			dataList.forEach(storedSample -> {
				File userDir = new File(filePathMatch);
				if (!userDir.exists()) {
					boolean bool = userDir.mkdirs();
					if (bool) {
						LOGGER.info("Directory created successfully");
					} else {
						LOGGER.error("Sorry couldn’t create specified directory");
					}
				}
				String newFileName = id + "_" + storedSample.getName();
				try {
					File storedFile = createFile(filePathMatch + newFileName, storedSample.getData());
					LOGGER.info("Voice print has been created for file:" + storedFile.getAbsolutePath());
				} catch (IOException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}
			});
			VoiceMatchConfig voiceMatch = new VoiceMatchConfig(filePathMatch);
			File[] files = new File(filePathMatch).listFiles();
			List<MatchResult<String>> matches = null;
			List<Double> allDistances = new ArrayList<Double>();
			try {
				for (File file : files) {
					if (file.isFile()) {
						matches = voiceMatch.recognito.identify(file);
						if (null != matches) {
							matches.stream().filter(data -> data.getDistance() != 0.0).forEach(f -> {
								allDistances.add(f.getDistance());
							});
						}
					}
				}
				Double maxNumber = allDistances.stream().max(Comparator.comparing(Double::valueOf)).get();
				Double minNumber = allDistances.stream().min(Comparator.comparing(Double::valueOf)).get();

				System.out.println(maxNumber + " : " + minNumber);
				UserVoiceDistance voiceDistance = distanceRepository.findDistanceByUserId(id);
				if (voiceDistance == null) {
					voiceDistance = new UserVoiceDistance();
				}
				voiceDistance.setCustomer(customerRepository.findById(id).get());
				voiceDistance.setMaxDistance(maxNumber + offset);
				voiceDistance.setMinDistance(minNumber - offset);
				distanceRepository.save(voiceDistance);

			} catch (UnsupportedAudioFileException | IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
