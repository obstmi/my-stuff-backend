package de.telekom.sea.mystuff.backend.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@JsonProperty("Bezeichnung")
	private String name;
	
	private int amount;
	
	private String location;
	
	private String description;
	
	private LocalDate lastUsed;
	
}
