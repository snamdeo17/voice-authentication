package com.mastercard.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.sun.istack.NotNull;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class CustomerVoiceData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(required = false, hidden = true)
	private long voiceId;

	@ManyToOne
    @JoinColumn(name = "user_id")
	private Customer customer;

	@Column
	@NotNull
	private String name;

	@Lob
	@Column(unique = true)
	private byte[] data;

	public CustomerVoiceData() {
	}

	public CustomerVoiceData(Customer customer, String name, byte[] data) {
		super();
		this.customer = customer;
		this.name = name;
		this.data = data;
	}

	public long getVoiceId() {
		return voiceId;
	}

	public void setVoiceId(long voiceId) {
		this.voiceId = voiceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	

}