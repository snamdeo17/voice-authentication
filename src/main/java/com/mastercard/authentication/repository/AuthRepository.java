package com.mastercard.authentication.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mastercard.authentication.models.CustomerVoiceData;


@Repository
public interface AuthRepository extends CrudRepository<CustomerVoiceData, Long> {

	@Query("Select count(c.voiceId) from CustomerVoiceData c where c.customer.email=:email")
	Long findRecordsByUserEmailId(@Param("email") String email);

	

}
