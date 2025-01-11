package com.clover.feignCleint;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.clover.dto.PetDTO;
import com.clover.model.Pet;

@FeignClient(name = "pet-service", url = "${pet-service.url}")
public interface PetServiceClient {

	@PostMapping("/addPetsByCustomerService")
	void addPets(@RequestBody List<Map<String, String>> petsData);
}
