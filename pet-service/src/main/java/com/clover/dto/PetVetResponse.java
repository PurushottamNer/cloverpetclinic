package com.clover.dto;

import com.clover.model.Pet;
import com.clover.model.Vet;

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
public class PetVetResponse {
	private Pet pet;
	private Vet vet;
}
