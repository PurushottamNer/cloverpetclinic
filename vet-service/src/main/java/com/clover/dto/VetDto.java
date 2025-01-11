package com.clover.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VetDto {

	private String vetId;
	private String vetName;
	private String specialization;
	private String email;
	private String phone;
	private String address;
	private Date createdAt;
	private Date updatedAt;
	private Boolean isActive;
}
