package com.prueba.demo.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.prueba.demo.entity.Producto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDto implements Serializable {
    Integer id;
    String nombre;
    String sku;
    BigDecimal precioUnitario;
    Integer cantidadEnStock;
    CategoriaDto categoria;
}