package com.clover.controller;

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

import com.clover.dto.VetDto;
import com.clover.service.VetService;

import java.util.List;

@RestController
@RequestMapping("/vetService")
public class VetController {

	@Autowired
	private VetService vetService;

	private static final Logger logger = LoggerFactory.getLogger(VetController.class);

	@PostMapping("/addVets")
	public ResponseEntity<?> addVets(@RequestBody List<VetDto> vets) {
		try {
			for (VetDto vetDto : vets) {
				logger.info("Received VetDto: {}", vetDto);
			}
			vetService.addVets(vets);
			return ResponseEntity.ok("Vets registered successfully.");
		} catch (Exception e) {
			logger.error("Error occurred while registering vets: {}", e.getMessage());
			return ResponseEntity.status(500).body("Failed to register vets. Please try again.");
		}
	}

	@GetMapping("/getVetDetailsByVetId")
	public ResponseEntity<?> getVetDetails(@RequestParam String vetId) {
		try {
			VetDto vetDto = vetService.getVetDetails(vetId);
			if (vetDto == null) {
				return ResponseEntity.status(404).body("Vet not found.");
			}
			return ResponseEntity.ok(vetDto);
		} catch (Exception e) {
			logger.error("Error occurred while fetching vet details: {}", e.getMessage());
			return ResponseEntity.status(500).body("Failed to fetch vet details. Please try again.");
		}
	}

	@PutMapping("/updateVetByVetId")
	public ResponseEntity<?> updateVet(@RequestParam String vetId, @RequestBody VetDto vetDto) {
		try {
			logger.info("Received request to update vet with vetId: {}", vetId);
			boolean updated = vetService.updateVet(vetId, vetDto);
			if (updated) {
				return ResponseEntity.ok("Vet updated successfully.");
			} else {
				return ResponseEntity.status(404).body("Vet not found.");
			}
		} catch (Exception e) {
			logger.error("Error occurred while updating vet details: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Failed to update vet details. Please try again.");
		}
	}

	@PutMapping("/softDeleteByVetId")
	public ResponseEntity<String> softDeleteVet(@RequestParam String vetId) {
		try {
			boolean result = vetService.softDeleteVet(vetId);
			if (result) {
				return ResponseEntity.ok("Vet with ID " + vetId + " has been successfully soft deleted.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vet with ID " + vetId + " not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting vet.");
		}
	}

}
