package com.prueba.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.prueba.demo.entity.DetalleVenta}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaDto implements Serializable {
    Integer id;
    VentaDto venta;
    ProductoDto producto;
    Integer cantidadVendida;
    BigDecimal precioUnitarioVenta;


}