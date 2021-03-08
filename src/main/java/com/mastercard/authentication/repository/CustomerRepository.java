package com.mastercard.authentication.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mastercard.authentication.models.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {
	public Customer findByEmail(String email);
}
