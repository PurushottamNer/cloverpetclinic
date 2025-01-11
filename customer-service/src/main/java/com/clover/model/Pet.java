package com.clover.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(exclude = "customer") // Exclude customer to prevent cyclic reference
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pet {

	@Id
	private String petId;
	private String petName;
	private String petSpecies;
	private String petBreed;
	private String petBirthDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false) // Define the foreign key column
	private Customer customer;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	private Boolean isActive;
}
