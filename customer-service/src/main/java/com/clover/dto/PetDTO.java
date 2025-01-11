package com.clover.dto;

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
public class PetDTO {

	private String petName;
	private String petSpecies;
	private String petBreed;
	private String petBirthDate;
}
