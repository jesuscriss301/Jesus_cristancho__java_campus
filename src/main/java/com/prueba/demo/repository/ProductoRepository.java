package com.prueba.demo.repository;

import com.prueba.demo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Object> findBySku(String sku);

    Collection<Producto> findByCategoriaId(Integer categoriaId);
}