package com.mastercard.authentication.services;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.mastercard.authentication.models.CustomerVoiceData;


public interface IAuthenticateService {

	public CustomerVoiceData store(MultipartFile file, @Valid int id) throws IOException, Exception;

	public boolean authenticateUser(MultipartFile file, @Valid int id) throws IOException, UnsupportedAudioFileException, Exception;
}
