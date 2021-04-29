package com.mastercard.authentication.services;

import java.io.IOException;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.mastercard.authentication.models.AuthHistory;
import com.mastercard.authentication.models.Customer;
import com.mastercard.authentication.models.CustomerVoiceData;


public interface IAuthenticateService {

	public CustomerVoiceData store(MultipartFile file, @Valid int id) throws IOException, Exception;
	public CustomerVoiceData store(MultipartFile file, @Valid String email) throws IOException, Exception;

	public boolean authenticateUser(MultipartFile file, @Valid int id) throws IOException, UnsupportedAudioFileException, Exception;
	public void calculateDistance(int id);
	public Customer findById(int id);
	public List<AuthHistory> getUserAuthHistory(int id);
}
