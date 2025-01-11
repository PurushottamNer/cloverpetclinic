package com.clover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clover.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

	Optional<Customer> findByCustomerEmail(String email);

}
