package com.auth.Auth_system.repositories;

import com.auth.Auth_system.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}