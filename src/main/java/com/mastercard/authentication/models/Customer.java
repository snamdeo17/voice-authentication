package com.mastercard.authentication.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

import com.sun.istack.NotNull;

@Entity
public class Customer implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	private String fname;

	private String lname;

	@Column(unique = true)
	@NotNull
	@Email(message = "Email should be in valid format")
	private String email;

	@Column(unique = true)
	private String secretCode;

	// @OneToOne(mappedBy = "walletOfCustomer")
	// @JsonIgnore
	// private Wallet wallet;
	//
	// @OneToMany(fetch = FetchType.EAGER, mappedBy = "accountHolder", cascade =
	// CascadeType.ALL)
	// @JsonIgnore
	// private List<Account> customerAccounts;

	private static final long serialVersionUID = 1L;

	public Customer() {
		super();
	}

	public Customer(String fname, String lname, String email) {
		super();
		this.fname = fname;
		this.lname = lname;
		this.email = email;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
}
