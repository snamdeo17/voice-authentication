package com.mastercard.authentication.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mastercard.authentication.models.UserVoiceDistance;

@Repository
public interface UserVoiceDistanceRepository extends CrudRepository<UserVoiceDistance, Long> {

	@Query("SELECT d FROM UserVoiceDistance d WHERE d.customer.userId= :userID")
	UserVoiceDistance findDistanceByUserId(@Param("userID") Integer userID);

}
