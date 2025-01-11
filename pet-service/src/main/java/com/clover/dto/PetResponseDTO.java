package com.clover.dto;

import java.util.List;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PetResponseDTO {
	@Id
	private String petId;
	private String petName;
	private String petSpecies;
	private String petBreed;
	private String petBirthDate;
}
