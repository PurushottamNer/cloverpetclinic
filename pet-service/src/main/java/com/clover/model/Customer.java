package com.clover.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

	@Id
	private String customerId;
	private String customerName;
	private String customerEmail;
	private String customerPhone;
	private String address;

	@JsonManagedReference
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude // Exclude to prevent circular reference
	private List<Pet> pets = new ArrayList<>();

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	private Boolean isActive;

	public void addPet(Pet pet) {
		if (pets == null) {
			pets = new ArrayList<>();
		}
		pets.add(pet);
		pet.setCustomer(this);
	}
}