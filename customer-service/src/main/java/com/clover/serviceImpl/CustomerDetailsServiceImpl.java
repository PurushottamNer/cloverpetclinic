package com.clover.serviceImpl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clover.model.Customer;
import com.clover.repository.CustomerRepository;

@Service
public class CustomerDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer = customerRepository.findByCustomerEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Customer not found: " + username));
		return new org.springframework.security.core.userdetails.User(customer.getCustomerEmail(),
				customer.getPassword(), new ArrayList<>());
	}
}
