package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clover.feignCleint.PetServiceClient;
import com.clover.model.Customer;
import com.clover.model.Pet;
import com.clover.repository.CustomerRepository;
import com.clover.repository.PetRepository;
import com.clover.security.JWTUtil;
import com.clover.service.CustomerService;

import jakarta.transaction.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PetServiceClient petServiceClient;

	@Autowired
	private JWTUtil jwtUtil;

	private static int counter = 0;

	private String idGenerator() {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		counter = (counter + 1) % 1000;
		return timestamp + String.format("%03d", counter);
	}

	private void setUniqueCustomerId(Customer customer) {
		String uniqueId;
		do {
			uniqueId = "CST" + idGenerator();
		} while (customerRepository.existsById(uniqueId));
		customer.setCustomerId(uniqueId);
	}

	@Transactional
	@Override
	public String registerCustomerWithPets(Map<String, Object> request) {
		Map<String, String> customerData = (Map<String, String>) request.get("customer");
		Customer customer = new Customer();
		customer.setCustomerName(customerData.get("customerName"));
		customer.setCustomerEmail(customerData.get("customerEmail"));
		customer.setPassword(passwordEncoder.encode(customerData.get("password")));
		customer.setCustomerPhone(customerData.get("customerPhone"));
		customer.setAddress(customerData.get("address"));
		customer.setCreatedAt(new Date());
		customer.setIsActive(true);
		setUniqueCustomerId(customer);

		customerRepository.saveAndFlush(customer);

		List<Map<String, String>> petsData = (List<Map<String, String>>) request.get("pets");
		petsData.forEach(petData -> {
			Pet pet = new Pet();
			pet.setPetName(petData.get("petName"));
			pet.setPetSpecies(petData.get("petSpecies"));
			pet.setPetBreed(petData.get("petBreed"));
			pet.setPetBirthDate(petData.get("petBirthDate"));
			pet.setPetId("PET" + idGenerator());
			pet.setCreatedAt(new Date());
			pet.setIsActive(true);

			pet.setCustomer(customer);
			customer.addPet(pet);
		});

		customerRepository.save(customer);

		return "Customer and pets registered successfully!";
	}

	@Override
	public Map<String, String> loginCustomer(Map<String, String> loginRequest) {
		String email = loginRequest.get("customerEmail");
		String password = loginRequest.get("password");

		Customer customer = customerRepository.findByCustomerEmail(email)
				.orElseThrow(() -> new RuntimeException("Incorrect username"));

		if (!passwordEncoder.matches(password, customer.getPassword())) {
			throw new RuntimeException("Incorrect password");
		}

		String token = jwtUtil.generateToken(email);

		customer.setToken(token);
		customerRepository.save(customer);

		return Map.of("token", token);
	}

	@Override
	public Map<String, Object> getCustomerDetailsWithPets(String customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		List<Pet> pets = petRepository.findPetsByCustomerId(customerId);

		Map<String, Object> response = Map.of("customer",
				Map.of("customerId", customer.getCustomerId(), "customerName", customer.getCustomerName(),
						"customerEmail", customer.getCustomerEmail(), "customerPhone", customer.getCustomerPhone(),
						"address", customer.getAddress()),
				"pets",
				pets.stream()
						.map(pet -> Map.of("petId", pet.getPetId(), "petName", pet.getPetName(), "petSpecies",
								pet.getPetSpecies(), "petBreed", pet.getPetBreed(), "petBirthDate",
								pet.getPetBirthDate()))
						.toList());

		return response;
	}
}
