package com.clover.service;

import java.util.Map;

public interface CustomerService {

	public String registerCustomerWithPets(Map<String, Object> request);

	public Map<String, String> loginCustomer(Map<String, String> loginRequest);

	Map<String, Object> getCustomerDetailsWithPets(String customerId);

}
