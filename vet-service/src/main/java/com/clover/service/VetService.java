package com.clover.service;

import java.util.List;

import com.clover.dto.VetDto;

public interface VetService {

	public boolean addVets(List<VetDto> vetDtos);

	public VetDto getVetDetails(String vetId);

	public boolean updateVet(String vetId, VetDto vetDto);

	public boolean softDeleteVet(String vetId);

}
