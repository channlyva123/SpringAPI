package com.setec.entities;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tbl_product")
public class ProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String product_name;
	private double price;
	private int qty;
	@JsonIgnore
	private String imageUrl;
	
	public double getTotalPrice() {
		return price * qty;
	}
	
	public String getFullImageUrl() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + imageUrl;
	}
}
