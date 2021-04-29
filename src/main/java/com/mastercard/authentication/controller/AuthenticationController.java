package com.mastercard.authentication.controller;

import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mastercard.authentication.dto.ResponseMessage;
import com.mastercard.authentication.models.AuthHistory;
import com.mastercard.authentication.models.Customer;
import com.mastercard.authentication.services.IAuthenticateService;

@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

	@Autowired
	IAuthenticateService authService;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@PostMapping("/user/uploadnewvoice")
	public ResponseEntity<ResponseMessage> uploadVoiceFile(@RequestParam("file") MultipartFile file,
			@Valid @RequestParam("email") String email) throws UnsupportedAudioFileException {
		String message = "";
		// int id = 1;
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
	public ResponseEntity<ResponseMessage> authenticateUserVoice(@RequestParam("file") MultipartFile file,
			@Valid @RequestParam("userId") int id) throws UnsupportedAudioFileException {
		String message = "";
		ResponseMessage response = new ResponseMessage();
		try {
			// validate file format and file size
			boolean result = authService.authenticateUser(file, id);
			if (!result) {
				throw new Exception("User is not valid");
			}
			Customer customer = authService.findById(id);
			message = "User's voice is authenticated successfully";
			LOGGER.info(message);
			response.setData(result);
			response.setStatus("200");
			response.setDescription(message);
			response.setUserName(customer.getFname() + " " + customer.getLname());
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			message = "User's voice is not authenticated";
			response.setStatus("401");
			response.setDescription(message);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/user/calculate/distance/{userId}")
	public ResponseEntity<ResponseMessage> calculateDistance(@PathVariable("userId") int id) {
		ResponseMessage response = new ResponseMessage();
		String message = "";
		try {
			authService.calculateDistance(id);
			message = "Distance calculated for user, " + id;
			LOGGER.info(message);
			response.setStatus("200");
			response.setDescription(message);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			message = "Error in calculating voice distance!";
			response.setStatus("500");
			response.setDescription(message);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/user/authhistory/{userId}")
	public ResponseEntity<ResponseMessage> getAuthHistory(@PathVariable("userId") int id) {
		ResponseMessage response = new ResponseMessage();
		List<AuthHistory> userAuthHistory = authService.getUserAuthHistory(id);
		response.setData(userAuthHistory);
		return new ResponseEntity<ResponseMessage>(response, HttpStatus.OK);
	}
}
