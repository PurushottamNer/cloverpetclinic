package com.clover.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clover.service.CustomerService;

@RestController
@RequestMapping("/customerService")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public String register(@RequestBody Map<String, Object> request) {
		return customerService.registerCustomerWithPets(request);
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
		try {
			Map<String, String> tokenResponse = customerService.loginCustomer(loginRequest);
			return ResponseEntity.ok(tokenResponse);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/getCustomerDetailsWithPets")
	public ResponseEntity<Map<String, Object>> getCustomerDetailsWithPets(@RequestParam String customerId,
			@RequestHeader("Authorization") String token) {
		try {
			if (!token.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", "access denied"));
			}

			Map<String, Object> customerDetails = customerService.getCustomerDetailsWithPets(customerId);
			return ResponseEntity.ok(customerDetails);
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
		}
	}

}
