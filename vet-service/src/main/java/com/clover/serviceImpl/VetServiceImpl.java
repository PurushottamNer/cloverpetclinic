package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clover.converter.VetConverter;
import com.clover.dto.VetDto;
import com.clover.model.Vet;
import com.clover.repository.VetRepository;
import com.clover.service.VetService;

@Service
public class VetServiceImpl implements VetService {

	@Autowired
	private VetRepository vetRepository;

	@Autowired
	private VetConverter vetConverter;

	private static final Logger logger = LoggerFactory.getLogger(VetServiceImpl.class);

	private String idGenerator() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

	@Override
	public boolean addVets(List<VetDto> vetDtos) {
		try {
			if (vetDtos == null || vetDtos.isEmpty()) {
				throw new IllegalArgumentException("No vet data provided.");
			}

			List<Vet> vets = new ArrayList<>();
			for (VetDto vetDto : vetDtos) {
				Vet vet = vetConverter.dtoToEntity(vetDto);

				String uniqueId;
				do {
					uniqueId = "VET" + idGenerator();
				} while (vetRepository.existsById(uniqueId));

				vet.setVetId(uniqueId);
				vet.setVetName(vetDto.getVetName());
				vet.setSpecialization(vetDto.getSpecialization());
				vet.setEmail(vetDto.getEmail());
				vet.setPhone(vetDto.getPhone());
				vet.setAddress(vetDto.getAddress());
				vet.setCreatedAt(new Date());
				vet.setIsActive(true);

				vets.add(vet);
			}

			List<Vet> savedVets = vetRepository.saveAll(vets);

			if (savedVets.isEmpty()) {
				logger.error("No vets were saved.");
				return false;
			}

			logger.info("Vets registered successfully.");
			return true;

		} catch (Exception e) {
			logger.error("Error occurred while registering vets: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to register vets. Please try again later.");
		}
	}

	@Override
	public VetDto getVetDetails(String vetId) {
		logger.info("Starting to fetch vet details for vetId: {}", vetId);

		try {
			Vet vet = vetRepository.findByVetIdAndIsActiveTrue(vetId).orElse(null);

			if (vet == null) {
				logger.warn("Active vet with vetId: {} not found.", vetId);
				return null;
			}

			VetDto vetDto = vetConverter.entityToDto(vet);

			return vetDto;

		} catch (Exception e) {
			logger.error("Error occurred while fetching vet details for vetId: {}. Error: {}", vetId, e.getMessage(),
					e);
			throw new RuntimeException("Failed to fetch vet details. Please try again later.");
		}
	}

	@Override
	public boolean updateVet(String vetId, VetDto vetDto) {
		try {
			logger.info("VetServiceImpl:updateVet execution started");

			if (vetDto == null || vetId == null || vetId.isEmpty()) {
				logger.error("VetServiceImpl:updateVet: Invalid input data. Please provide valid vetId and vetDto.");
				return false;
			}

			Vet vet = vetRepository.findById(vetId).orElse(null);

			if (vet == null) {
				logger.warn("VetServiceImpl:updateVet: Vet with id {} not found", vetId);
				return false;
			}

			vet.setVetName(vetDto.getVetName());
			vet.setSpecialization(vetDto.getSpecialization());
			vet.setPhone(vetDto.getPhone());
			vet.setEmail(vetDto.getEmail());
			vet.setAddress(vetDto.getAddress());
			vet.setIsActive(vetDto.getIsActive());
			vet.setUpdatedAt(new Date());

			vetRepository.save(vet);

			logger.info("VetServiceImpl:updateVet execution completed successfully for vetId: {}", vetId);
			return true;

		} catch (Exception e) {
			logger.error("VetServiceImpl:updateVet: An error occurred while updating vet with id {}: {}", vetId,
					e.getMessage(), e);
			throw new RuntimeException("Failed to update vet details. Please try again later.");
		}
	}

	@Override
	public boolean softDeleteVet(String vetId) {
		try {
			logger.info("VetServiceImpl:softDeleteVet execution started");

			if (vetId == null || vetId.isEmpty()) {
				logger.error("VetServiceImpl:softDeleteVet: Invalid vetId provided.");
				return false;
			}

			int rowsUpdated = vetRepository.softDeleteVetByVetId(vetId);

			if (rowsUpdated > 0) {
				logger.info("VetServiceImpl:softDeleteVet: Vet with vetId {} has been successfully soft deleted.",
						vetId);
				return true;
			} else {
				logger.warn("VetServiceImpl:softDeleteVet: Vet with vetId {} not found", vetId);
				return false;
			}

		} catch (Exception e) {
			logger.error("VetServiceImpl:softDeleteVet: An error occurred while updating vet with id {}: {}", vetId,
					e.getMessage(), e);
			throw new RuntimeException("Failed to soft delete vet. Please try again later.");
		}
	}

}
