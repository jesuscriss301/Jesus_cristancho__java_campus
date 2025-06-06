package com.prueba.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.prueba.demo.entity.Cliente}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto implements Serializable {
    Integer id;
    String nombre;
    String apellido;
    String email;
    String telefono;
    String direccion;
    Instant fechaRegistro;
}