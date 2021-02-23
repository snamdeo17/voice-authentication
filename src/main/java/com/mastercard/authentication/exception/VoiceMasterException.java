package com.mastercard.authentication.exception;

import org.springframework.http.HttpStatus;

public class VoiceMasterException extends Exception {
	private static final long serialVersionUID = 1L;
	private HttpStatus statusCode;

	public VoiceMasterException(String message) {
		super(message);
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public VoiceMasterException(HttpStatus statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}

}
