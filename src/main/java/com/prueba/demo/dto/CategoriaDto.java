package com.prueba.demo.dto;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.prueba.demo.entity.Categoria}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDto implements Serializable {
    Integer id;
    String nombre;
    String descripcion;

}