package com.clover.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clover.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {

	@Query(value = """
			SELECT
			    p.pet_id AS petId,
			    p.pet_name AS petName,
			    p.pet_species AS petSpecies,
			    p.pet_breed AS petBreed,
			    v.vet_id AS vetId,
			    v.vet_name AS vetName,
			    v.specialization AS specialization,
			    v.email AS email,
			    v.phone AS phone,
			    v.address AS address
			FROM
			    pet p
			LEFT JOIN
			    vet v
			ON
			    p.vet_id = v.vet_id
			WHERE
			    p.pet_id = :petId
			""", nativeQuery = true)
	List<Map<String, Object>> getVetForPet(@Param("petId") String petId);
}
