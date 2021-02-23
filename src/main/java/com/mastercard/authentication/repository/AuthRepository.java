package com.mastercard.authentication.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mastercard.authentication.models.CustomerVoiceData;


@Repository
public interface AuthRepository extends CrudRepository<CustomerVoiceData, Long> {

	

}
