package com.prueba.demo.Controller;

import com.prueba.demo.dto.VentaDto;
import com.prueba.demo.service.ServicioVenta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final ServicioVenta servicioVenta;

    public VentaController(ServicioVenta servicioVenta) {
        this.servicioVenta = servicioVenta;
    }

    /**
     * POST /api/ventas – Registrar una venta.
     * @param VentaDto DTO con los datos para registrar la venta (clienteId, items).
     * @return ResponseEntity con la VentaDto de la venta registrada y estado 201.
     */
    @PostMapping
    public ResponseEntity<VentaDto> registrarVenta( @RequestBody VentaDto VentaDto) {
        VentaDto nuevaVenta = servicioVenta.registrarVenta(VentaDto);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    /**
     * GET /api/ventas – Obtener todas las ventas.
     * @return ResponseEntity con una lista de VentaDto y estado 200.
     */
    @GetMapping
    public ResponseEntity<List<VentaDto>> obtenerTodasLasVentas() {
        List<VentaDto> ventas = servicioVenta.obtenerTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }

    /**
     * GET /api/ventas/cliente/{identificadorCliente} – Obtener ventas por identificador del cliente.
     * @param clienteId ID del cliente.
     * @return ResponseEntity con una lista de VentaDto y estado 200.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaDto>> obtenerVentasPorClienteId(@PathVariable("clienteId") Integer clienteId) {
        List<VentaDto> ventas = servicioVenta.obtenerVentasPorClienteId(clienteId);
        return ResponseEntity.ok(ventas);
    }

    /**
     * GET /api/ventas/{id} – Obtener una venta por su ID.
     * @param id ID de la venta.
     * @return ResponseEntity con la VentaDto si se encuentra (200), o 404 si no.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaDto> obtenerVentaPorId(@PathVariable Integer id) {
        return servicioVenta.obtenerVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}