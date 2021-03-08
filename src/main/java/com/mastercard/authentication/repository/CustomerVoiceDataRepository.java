package com.mastercard.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mastercard.authentication.models.CustomerVoiceData;

@Repository
public interface CustomerVoiceDataRepository extends CrudRepository<CustomerVoiceData, Integer>{

	 @Query("SELECT d FROM CustomerVoiceData d WHERE d.customer.userId=:userID")
	 List<CustomerVoiceData> findCustomerVoiceDataByUserID(@Param("userID") Integer userID);
}
