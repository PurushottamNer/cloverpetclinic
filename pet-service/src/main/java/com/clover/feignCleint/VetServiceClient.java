package com.clover.feignCleint;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clover.model.Vet;

@FeignClient(name = "vet-service", url = "${vet-service.url}")
public interface VetServiceClient {
	@GetMapping("/getVetDetailsByVetId")
	Vet getVetDetails(@RequestParam("vetId") String vetId);

}
