package com.mastercard.authentication.config;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.bitsinharmony.recognito.Recognito;

public class VoiceMatchConfig {

	public Recognito<String> recognito = new Recognito<>(48000.0f);

	protected String pathVoiceRecorded;

	public VoiceMatchConfig() {
	
	}

	public VoiceMatchConfig(String pathVoiceRecorded) throws IllegalArgumentException {

		this.pathVoiceRecorded = pathVoiceRecorded;

		try {
			File[] files = new File(pathVoiceRecorded).listFiles();
			// If this pathname does not denote a directory, then listFiles()
			// returns null.
			for (File file : files) {
				if (file.isFile()) {
					recognito.createVoicePrint(file.getName(), new File(pathVoiceRecorded + file.getName()));
				}
			}
		} catch (UnsupportedAudioFileException | IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
