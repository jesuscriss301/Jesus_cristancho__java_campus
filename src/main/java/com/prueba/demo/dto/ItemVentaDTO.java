package com.prueba.demo.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) para representar un ítem individual dentro de una venta.
 * Utilizado para transferir la información de qué producto y qué cantidad se desea comprar.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVentaDTO {
    private Integer productoId; // Identificador único del producto que se está vendiendo
    private Integer cantidad; // Cantidad de unidades de ese producto que se desea comprar

}