package com.mastercard.authentication.controller;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mastercard.authentication.dto.ResponseMessage;
import com.mastercard.authentication.services.IAuthenticateService;


@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {
	
	@Autowired
	IAuthenticateService authService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@PostMapping("/user/uploadnewvoice")
	public ResponseEntity<ResponseMessage> uploadVoiceFile(
			@RequestParam("file") MultipartFile file , @Valid @RequestParam("email") String email ) throws UnsupportedAudioFileException {
		String message = "";
//		int id = 1;
		ResponseMessage response = new ResponseMessage();
		try {
			// validate file format and file size
			authService.store(file, email);
			message = "Uploaded the voice sample successfully for user, " + email;
			LOGGER.info(message);
			response.setStatus("200");
            response.setDescription(message);
            return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			response.setStatus("404");
            response.setDescription(message);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/user/authenticatevoice")
	public ResponseEntity<ResponseMessage> authenticateUserVoice(@RequestParam("file") MultipartFile file, @Valid @RequestParam("userId") int id) throws UnsupportedAudioFileException {
		String message = "";
		ResponseMessage response = new ResponseMessage();
		try {
			// validate file format and file size
			boolean result = authService.authenticateUser(file, id);
			if(!result) {
				throw new Exception("User is not valid");
			}
			message = "User is authenticated successfully for user, " + id;
			LOGGER.info(message);
			response.setData(result);
			response.setStatus("200");
            response.setDescription(message);
            return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			message = "User is not authenticated successfully!";
			response.setStatus("401");
            response.setDescription(message);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}
}
