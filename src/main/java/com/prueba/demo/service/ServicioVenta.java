package com.prueba.demo.service;

import com.prueba.demo.dto.VentaDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface ServicioVenta {

    /**
     * Registra una nueva venta, verificando y disminuyendo el stock.
     * Esta operación es transaccional.
     * @param ventaDTO Datos de la venta a registrar (cliente y productos con cantidades).
     * @return El DTO de la venta registrada con su ID y total.
     * @throws IllegalArgumentException Si no hay stock suficiente, el producto/cliente no existe, o los datos son inválidos.
     */
    VentaDto registrarVenta(VentaDto ventaDTO);

    /**
     * Obtiene todas las ventas registradas.
     * @return Una lista de DTOs de ventas.
     */
    List<VentaDto> obtenerTodasLasVentas();

    /**
     * Obtiene ventas por el identificador del cliente.
     * @param clienteId El ID del cliente.
     * @return Una lista de DTOs de ventas realizadas por ese cliente.
     */
    List<VentaDto> obtenerVentasPorClienteId(Integer clienteId);

    /**
     * Obtiene una venta por su ID.
     * @param id El ID de la venta.
     * @return Un Optional que contiene el DTO de la venta si existe, o un Optional vacío.
     */
    Optional<VentaDto> obtenerVentaPorId(Integer id);
}
