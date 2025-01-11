package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.clover.dto.PetDTO;
import com.clover.dto.PetResponseDTO;
import com.clover.dto.PetVetAssignmentDto;
import com.clover.dto.PetVetResponse;
import com.clover.feignCleint.VetServiceClient;
import com.clover.model.Customer;
import com.clover.model.Pet;
import com.clover.model.Vet;
import com.clover.repository.CustomerRepository;
import com.clover.repository.PetRepository;
import com.clover.repository.VetRepository;
import com.clover.service.PetService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;

@Service
public class PetServiceImpl implements PetService {

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private VetServiceClient vetServiceClient;

	@Autowired
	private VetRepository vetRepository;

	private static final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);

	private String idGenerator() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

	@Override
	public void addPets(List<PetDTO> petDTOs) {
		List<Pet> pets = petDTOs.stream().map(dto -> {
			Pet pet = new Pet();
			pet.setPetName(dto.getPetName());
			pet.setPetSpecies(dto.getPetSpecies());
			pet.setPetBreed(dto.getPetBreed());
			pet.setPetBirthDate(dto.getPetBirthDate());

			boolean isUniqueIdFound = false;
			String uniqueId = "";

			while (!isUniqueIdFound) {
				uniqueId = "PET" + idGenerator();
				if (petRepository.findById(uniqueId).isEmpty()) {
					isUniqueIdFound = true;
				}
			}

			pet.setPetId(uniqueId);
			pet.setCreatedAt(new Date());
			pet.setIsActive(true);
			return pet;
		}).collect(Collectors.toList());

		petRepository.saveAll(pets);
	}

	@Transactional
	public boolean addNewPetToExistingCustomer(String customerId, List<PetDTO> petDTOs) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		List<Pet> pets = petDTOs.stream().map(this::convertDtoToEntity).peek(pet -> pet.setCustomer(customer))
				.collect(Collectors.toList());

		if (customer.getPets() == null) {
			customer.setPets(new ArrayList<>());
		}
		customer.getPets().addAll(pets);

		customerRepository.save(customer);
		return true;
	}

	private Pet convertDtoToEntity(PetDTO petDTO) {
		Pet pet = new Pet();
		pet.setPetId(generateUniquePetId());
		pet.setPetName(petDTO.getPetName());
		pet.setPetSpecies(petDTO.getPetSpecies());
		pet.setPetBreed(petDTO.getPetBreed());
		pet.setPetBirthDate(petDTO.getPetBirthDate());
		pet.setCreatedAt(new Date());
		pet.setIsActive(true);
		return pet;
	}

	private String generateUniquePetId() {
		boolean isUniqueIdFound = false;
		String uniquePetId = "";

		while (!isUniqueIdFound) {
			uniquePetId = "PET" + idGenerator();
			if (petRepository.findById(uniquePetId).isEmpty()) {
				isUniqueIdFound = true;
			}
		}
		return uniquePetId;
	}

	@Override
	public boolean updatePetDetailsByCustomerId(String customerId, String petId, Pet pet) {
		Optional<Customer> customerOptional = customerRepository.findById(customerId);
		if (customerOptional.isEmpty()) {
			return false;
		}

		Optional<Pet> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty()) {
			return false;
		}

		Pet existingPet = petOptional.get();

		if (!existingPet.getCustomer().getCustomerId().equals(customerId)) {
			return false;
		}

		existingPet.setPetName(pet.getPetName());
		existingPet.setPetSpecies(pet.getPetSpecies());
		existingPet.setPetBreed(pet.getPetBreed());
		existingPet.setPetBirthDate(pet.getPetBirthDate());
		existingPet.setUpdatedAt(new Date());
		petRepository.save(existingPet);

		return true;
	}

	@Override
	public PetResponseDTO fetchPetDetailsByPetId(String petId) {
		Optional<Pet> petOptional = petRepository.findById(petId);

		if (petOptional.isPresent()) {
			Pet pet = petOptional.get();
			return new PetResponseDTO(pet.getPetId(), pet.getPetName(), pet.getPetSpecies(), pet.getPetBreed(),
					pet.getPetBirthDate());
		}
		throw new RuntimeException("Pet not found");
	}

	@Transactional
	@Override
	public boolean assignVetToPet(PetVetAssignmentDto assignmentDto) {
		Pet pet = petRepository.findById(assignmentDto.getPetId())
				.orElseThrow(() -> new RuntimeException("Pet not found with ID: " + assignmentDto.getPetId()));

		Vet vet = vetRepository.findById(assignmentDto.getVetId())
				.orElseThrow(() -> new RuntimeException("Vet not found with ID: " + assignmentDto.getVetId()));

		pet.setVet(vet);

		petRepository.save(pet);

		return true;
	}

	@Override
	public List<Map<String, Object>> getVetDetailsForPet(String petId) {
		return petRepository.getVetForPet(petId);
	}

	@Transactional
	@CircuitBreaker(name = "vetServiceBreaker", fallbackMethod = "getVetDetailsForPetFallback")
	public PetVetResponse getVetDetailsForPet1(String petId) {
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

		Vet vet = vetServiceClient.getVetDetails(pet.getVet().getVetId());

		return new PetVetResponse(pet, vet);
	}

	public PetVetResponse getVetDetailsForPetFallback(String petId, Throwable throwable) {
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

		Vet dummyVet = new Vet();
		dummyVet.setVetId("DUMMY_VET_ID");
		dummyVet.setVetName("Unavailable Vet");
		dummyVet.setEmail("dummy.vet@clover.com");
		dummyVet.setPhone("000-000-0000");
		dummyVet.setSpecialization("N/A");
		dummyVet.setAddress("N/A");

		return new PetVetResponse(pet, dummyVet);
	}

}
