package com.mastercard.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mastercard.authentication.models.AuthHistory;

@Repository
public interface AuthHistoryRepository extends JpaRepository<AuthHistory, Long> {
	@Query("SELECT ah FROM AuthHistory ah WHERE ah.user.userId = :userId")
	List<AuthHistory> findByUserId(@Param("userId") int id);
}
