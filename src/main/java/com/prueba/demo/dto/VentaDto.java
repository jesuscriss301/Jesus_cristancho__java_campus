package com.prueba.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link com.prueba.demo.entity.Venta}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaDto implements Serializable {
    Integer id;
    ClienteDto cliente;
    Instant fechaVenta;
    BigDecimal totalGlobalVenta;
    List<ItemVentaDTO> items;
    List<DetalleVentaDto> detalles;
}