package com.clover.service;

import java.util.List;
import java.util.Map;

import com.clover.dto.PetDTO;
import com.clover.dto.PetResponseDTO;
import com.clover.dto.PetVetAssignmentDto;
import com.clover.dto.PetVetResponse;
import com.clover.model.Pet;

public interface PetService {

	public boolean addNewPetToExistingCustomer(String customerId, List<PetDTO> petDTOs);

	public boolean updatePetDetailsByCustomerId(String customerId, String petId, Pet pet);

	public PetResponseDTO fetchPetDetailsByPetId(String petId);

	public void addPets(List<PetDTO> pets);

	public boolean assignVetToPet(PetVetAssignmentDto assignmentDto) throws Exception;

	public List<Map<String, Object>> getVetDetailsForPet(String petId);

	public PetVetResponse getVetDetailsForPet1(String petId);

}
