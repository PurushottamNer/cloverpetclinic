package com.clover.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.clover.dto.VetDto;
import com.clover.model.Vet;

@Component
public class VetConverter {

	public Vet dtoToEntity(VetDto vetDto) {
		Vet vet = new Vet();
		BeanUtils.copyProperties(vetDto, vet);
		return vet;
	}

	public VetDto entityToDto(Vet vet) {
		VetDto vetDto = new VetDto();
		BeanUtils.copyProperties(vet, vetDto);
		return vetDto;
	}

}
