package com.clover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clover.model.Vet;

import jakarta.transaction.Transactional;

@Repository
public interface VetRepository extends JpaRepository<Vet, String> {

	Optional<Vet> findByVetIdAndIsActiveTrue(String vetId);

	@Modifying
	@Query("UPDATE Vet v SET v.isActive = false WHERE v.vetId = :vetId")
	@Transactional
	int softDeleteVetByVetId(@Param("vetId") String vetId);
}
