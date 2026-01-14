package com.setec.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setec.entities.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

}
