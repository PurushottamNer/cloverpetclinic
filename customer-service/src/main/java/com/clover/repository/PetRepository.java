package com.clover.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clover.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {

	@Query(value = "SELECT * FROM pet WHERE customer_id = :customerId", nativeQuery = true)
	List<Pet> findPetsByCustomerId(@Param("customerId") String customerId);

}
