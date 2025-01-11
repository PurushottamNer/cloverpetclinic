package com.clover.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clover.dto.PetDTO;
import com.clover.dto.PetResponseDTO;
import com.clover.dto.PetVetAssignmentDto;
import com.clover.dto.PetVetResponse;
import com.clover.model.Pet;
import com.clover.service.PetService;

@RestController
@RequestMapping("/petService")
public class PetController {

	@Autowired
	private PetService petService;

	private static final Logger logger = LoggerFactory.getLogger(PetController.class);

	@PostMapping("/addPetsByCustomerService")
	public ResponseEntity<String> addPets(@RequestBody List<PetDTO> pets) {
		petService.addPets(pets);
		return ResponseEntity.status(HttpStatus.CREATED).body("Pets added successfully.");
	}

	@PostMapping("/addNewPetToExistingCustomer")
	public ResponseEntity<?> addNewPetToExistingCustomer(@RequestParam String customerId,
			@RequestBody List<PetDTO> pets) {
		try {
			boolean result = petService.addNewPetToExistingCustomer(customerId, pets);

			if (result) {
				logger.info("PetController: Pet(s) added for customer with ID: " + customerId);
				return ResponseEntity.ok("{\"status\":\"success\"}");
			} else {
				logger.error("PetController: Failed to add pet(s) for customer with ID: " + customerId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"status\":\"failed\"}");
			}
		} catch (Exception e) {
			logger.error("PetController: Exception occurred - " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
		}
	}

	@PutMapping("/updatePetDetailsByCustomerId")
	public ResponseEntity<?> updatePetDetails(@RequestParam String customerId, @RequestParam String petId,
			@RequestBody Pet pet) {

		boolean result = petService.updatePetDetailsByCustomerId(customerId, petId, pet);

		if (result) {
			logger.info("PetController: Pet details updated successfully for petId " + petId + " under customerId "
					+ customerId);
			return new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
		}

		logger.error("PetController: Failed to update pet details. CustomerId and PetId mismatch or not found.");
		return new ResponseEntity<>(
				"{\"status\":\"failed\", \"message\": \"CustomerId and PetId mismatch or not found.\"}",
				HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/fetchPetDetailsByPetId")
	public ResponseEntity<?> fetchPetDetailsByPetId(@RequestParam String petId) {
		try {
			PetResponseDTO pet = petService.fetchPetDetailsByPetId(petId);
			logger.info("PetController: Fetched pet details for petId " + petId);
			return new ResponseEntity<>(pet, HttpStatus.OK);
		} catch (RuntimeException e) {
			logger.error("PetController: " + e.getMessage());
			return new ResponseEntity<>("{\"status\":\"failed\", \"message\":\"" + e.getMessage() + "\"}",
					HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/assignVetToPet")
	public ResponseEntity<String> assignVetToPet(@RequestBody PetVetAssignmentDto assignVetToPetDto) {
		try {
			boolean isAssigned = petService.assignVetToPet(assignVetToPetDto);

			if (isAssigned) {
				return ResponseEntity.ok("Vet assigned successfully to pet.");
			} else {
				return ResponseEntity.status(400).body("Failed to assign vet to pet. Pet or Vet not found.");
			}
		} catch (Exception ex) {
			return ResponseEntity.status(500).body("Vet Service is unavailable. Please try again later.");
		}
	}

	@GetMapping("/getVetForPet")
	public ResponseEntity<Map<String, Object>> getVetForPet(@RequestParam String petId) {
		List<Map<String, Object>> vetDetails = petService.getVetDetailsForPet(petId);
		if (vetDetails.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "No vet details found for pet with ID " + petId));
		}
		return ResponseEntity.ok(vetDetails.get(0));
	}

	@GetMapping("/getVetForPetByFeign")
	public PetVetResponse getVetDetailsForPet(@RequestParam String petId) {
		return petService.getVetDetailsForPet1(petId);
	}

}
