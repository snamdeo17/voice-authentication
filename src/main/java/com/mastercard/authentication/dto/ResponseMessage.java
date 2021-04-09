package com.mastercard.authentication.dto;

public class ResponseMessage {
	private String status;
	private String description;
	private Object data;
	private String userName;	

	public ResponseMessage() {
		super();
	}

	public ResponseMessage(String description) {
		super();
		this.description = description;
	}

	public ResponseMessage(String status, String description) {
		super();
		this.status = status;
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}