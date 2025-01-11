package com.clover.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vet {

	@Id
	private String vetId;

	private String vetName;
	private String specialization;
	private String email;
	private String phone;
	private String address;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	private Boolean isActive;

	@OneToMany(mappedBy = "vet", fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<Pet> pets;
}
