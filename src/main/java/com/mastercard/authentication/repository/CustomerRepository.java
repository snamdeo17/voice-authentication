package com.mastercard.authentication.repository;

import org.springframework.data.repository.CrudRepository;

import com.mastercard.authentication.models.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

}
