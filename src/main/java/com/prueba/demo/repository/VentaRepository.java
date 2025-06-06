package com.prueba.demo.repository;

import com.prueba.demo.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByClienteId(Integer clienteId);
}