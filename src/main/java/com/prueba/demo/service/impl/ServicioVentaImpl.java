package com.prueba.demo.service.impl;

import com.prueba.demo.dto.*;
import com.prueba.demo.service.ServicioVenta;
import com.prueba.demo.service.ServicioProducto;
import com.prueba.demo.entity.Venta;
import com.prueba.demo.entity.DetalleVenta;
import com.prueba.demo.entity.Producto;
import com.prueba.demo.entity.Cliente;

import com.prueba.demo.repository.VentaRepository;
import com.prueba.demo.repository.DetalleVentaRepository;
import com.prueba.demo.repository.ClienteRepository;
import com.prueba.demo.repository.ProductoRepository; // Necesario para el nombre del producto en DetalleVentaDto

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioVentaImpl implements ServicioVenta {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ServicioProducto servicioProducto;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository; // Inyecta el ProductoRepository

    // Constructor para inyección de dependencias
    public ServicioVentaImpl(VentaRepository ventaRepository,
                             DetalleVentaRepository detalleVentaRepository,
                             ServicioProducto servicioProducto,
                             ClienteRepository clienteRepository,
                             ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.servicioProducto = servicioProducto;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Método auxiliar para mapear una entidad DetalleVenta a DetalleVentaDto.
     * Incluye la búsqueda del nombre del producto.
     */
    private DetalleVentaDto mapDetalleToDto(DetalleVenta detalle) {
        if (detalle == null) {
            return null;
        }

        DetalleVentaDto dto = new DetalleVentaDto();
        ProductoDto prductodto= servicioProducto.obtenerProductoPorId(detalle.getProducto().getId()).get();
        dto.setProducto(prductodto);
        dto.setCantidadVendida(detalle.getCantidadVendida());
        dto.setPrecioUnitarioVenta(detalle.getPrecioUnitarioVenta());
        //dto.setSubtotal(detalle.getPrecioUnitarioVenta().multiply(new BigDecimal(detalle.getCantidadVendida())));

        // Obtener el nombre del producto usando el repositorio de productos
        productoRepository.findById(detalle.getProducto().getId())
                .map(Producto::getNombre);

        return dto;
    }

    /**
     * Método auxiliar para mapear una entidad Venta, su Cliente y sus Detalles a un VentaDto.
     * Este es el core del mapeo para la salida.
     */
    private VentaDto mapVentaToDto(Venta venta, Cliente cliente, List<DetalleVenta> detalles) {
        if (venta == null) {
            return null;
        }

        VentaDto ventaDto = new VentaDto();
        ventaDto.setId(venta.getId());
        // Mapear Cliente a ClienteDto
        if (cliente != null) {
            ventaDto.setCliente(clienteToDto(cliente));
        } else {
            ventaDto.setCliente(null);
        }

        // Convertir LocalDateTime a Instant
        if (venta.getFechaVenta() != null) {
            ventaDto.setFechaVenta(venta.getFechaVenta());
        }

        ventaDto.setTotalGlobalVenta(venta.getTotalGlobalVenta());

        // Mapear Detalles de Venta a DetalleVentaRespuestaDTO
        if (detalles != null && !detalles.isEmpty()) {
            List<DetalleVentaDto> detallesDto = detalles.stream()
                    .map(this::mapDetalleToDto) // Llama al método auxiliar de mapeo de detalles
                    .collect(Collectors.toList());
            ventaDto.setDetalles(detallesDto);
        } else {
            ventaDto.setDetalles(new ArrayList<>());
        }

        return ventaDto;
    }

    @Override
    @Transactional // Asegura que toda la operación sea atómica (todo o nada)
    public VentaDto registrarVenta(VentaDto ventaDTO) {
        // 1. Validar la existencia del cliente
        Integer clienteId = ventaDTO.getCliente().getId(); // Asumiendo que VentaDto.getCliente() no es nulo y tiene ID
        if (clienteId == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio para registrar una venta.");
        }
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        // 2. Validar que la venta contenga productos
        if (ventaDTO.getItems() == null || ventaDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        // Crear la entidad Venta (encabezado)
        Venta nuevaVenta = new Venta();
        nuevaVenta.setCliente(cliente);
        nuevaVenta.setFechaVenta(Instant.now());

        BigDecimal totalCalculado = BigDecimal.ZERO;

        // 3. Procesar cada ítem de la venta: verificar stock, disminuirlo y crear DetalleVenta
        for (ItemVentaDTO itemDTO : ventaDTO.getItems()) {
            Integer productoId = itemDTO.getProductoId();

            if (productoId == null) {
                throw new IllegalArgumentException("El ID del producto no puede ser nulo en un ítem de venta.");
            }
            if (itemDTO.getCantidad() == null || itemDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad comprada debe ser mayor que cero para el producto ID: " + productoId);
            }

            Producto producto = servicioProducto.obtenerProductoDominioPorId(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

            // Disminuir stock
            servicioProducto.disminuirStock(producto.getId(), itemDTO.getCantidad());

            // Crear el DetalleVenta
            DetalleVenta detalle = new DetalleVenta(
                    nuevaVenta, // Pasamos la entidad Venta para establecer la relación bidireccional
                    producto,
                    itemDTO.getCantidad(),
                    producto.getPrecioUnitario()
            );

            // --- ¡CLAVE!: Agregar el detalle a la colección de la entidad Venta ---
            nuevaVenta.getDetallesVenta().add(detalle);

            totalCalculado = totalCalculado.add(
                    producto.getPrecioUnitario().multiply(new BigDecimal(itemDTO.getCantidad()))
            );
        }

        // 4. Establecer el total global de la venta
        nuevaVenta.setTotalGlobalVenta(totalCalculado);

        // 5. Guardar la entidad Venta.
        // Debido a 'CascadeType.ALL' en Venta.java, todos los 'DetalleVenta'
        // que se agregaron a 'nuevaVenta.getDetallesVenta()' se guardarán automáticamente.
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        // 6. Mapear la Venta guardada a VentaDto para la respuesta
        return convertirAVentaDto(ventaGuardada); // Pasamos la Venta guardada
    }

    @Override
    public List<VentaDto> obtenerTodasLasVentas() {
        return ventaRepository.findAll().stream()
                .map(venta -> {
                    // Para cada venta, necesitamos cargar el cliente asociado
                    // Convertimos Long a Integer para el ID del cliente si tu DTO lo espera
                    Integer clienteId = venta.getCliente().getId();
                    Cliente cliente = clienteRepository.findById(clienteId)
                            .orElse(null); // Manejar si el cliente no se encuentra

                    // Mapear la entidad Venta y su Cliente a VentaDto
                    return mapToRespuestaDTO(venta, cliente);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VentaDto> obtenerVentasPorClienteId(Integer clienteId) {
        // 1. Validar que el ID del cliente no sea nulo y exista
        if (clienteId == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
        }
        // Cliente es necesario para la validación, no para la consulta directa de ventas por ID
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        // 2. Buscar ventas por cliente y mapear
        // Esta llamada ahora devolverá una List<Venta>
        List<Venta> ventas = ventaRepository.findByClienteId(clienteId);

        // Mapea la lista de entidades a una lista de DTOs


        return ventas.stream()
                .map(this::convertirAVentaDto)
                .collect(Collectors.toList());


    }
    private VentaDto convertirAVentaDto(Venta venta) {
        if (venta == null) {
            return null;
        }

        VentaDto dto = new VentaDto();
        dto.setId(venta.getId());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setTotalGlobalVenta(venta.getTotalGlobalVenta());

        if (venta.getCliente() != null) {
            // Asumiendo que tienes un ClienteDto y un método para mapear la entidad Cliente a ClienteDto
            Cliente cliente = clienteRepository.findById(venta.getCliente().getId()).get();
            dto.setCliente(clienteToDto(cliente)); // <-- Si tienes un método clienteToDto
        }

        // --- Crucial: Mapea las entidades DetalleVenta a DetalleVentaDto ---
        // Si usas FetchType.LAZY en la relación @OneToMany en tu entidad Venta,
        // podrías necesitar que el método obtenerVentasPorClienteId sea @Transactional
        // o que tu consulta en el repositorio use JOIN FETCH para cargar los detalles.
        if (venta.getDetallesVenta() != null && !venta.getDetallesVenta().isEmpty()) {
            List<DetalleVentaDto> detallesDto = venta.getDetallesVenta().stream()
                    .map(detalle -> {
                        DetalleVentaDto detalleDto = new DetalleVentaDto();
                        detalleDto.setId(detalle.getId());
                        detalleDto.setCantidadVendida(detalle.getCantidadVendida()); // Asegúrate de que los nombres de los getters coincidan con tu entidad DetalleVenta
                        detalleDto.setPrecioUnitarioVenta(detalle.getPrecioUnitarioVenta()); // Asegúrate de que los nombres de los getters coincidan
                        //detalleDto.setSubtotal(detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad())));

                        if (detalle.getProducto() != null) {
                            ProductoDto productoDto = servicioProducto.obtenerProductoPorId(detalle.getProducto().getId()).orElse(null);
                            detalleDto.setProducto(productoDto);

                        }
                        return detalleDto;
                    })
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDto); // Asegúrate de que VentaDto tenga setDetalles(List<DetalleVentaDto>)
        }

        // La lista 'items' en VentaDto (que es para la entrada de la solicitud) NO se mapea aquí.
        // Típicamente, solo se muestra en la respuesta al registrar una venta, no al consultarla.
        dto.setItems(null); // O explícitamente no la asignes si no es necesaria para DTOs de recuperación.

        return dto;
    }
    @Override
    public Optional<VentaDto> obtenerVentaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la venta no puede ser nulo.");
        }

        return ventaRepository.findById(id)
                .map(venta -> {
                    Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                            .orElse(null); // O lanzar una excepción si el cliente es obligatorio

                    // Mapear la entidad Venta y su Cliente a VentaDto
                    return mapToRespuestaDTO(venta, cliente);
                });
    }

    // --- Métodos de Mapeo de Entidad a DTO de Respuesta ---
    private VentaDto mapToRespuestaDTO(Venta venta, Cliente cliente) {
        if (venta == null) {
            return null;
        }

        VentaDto ventaDto = new VentaDto();
        ventaDto.setId(venta.getId());

        // Mapear Cliente a ClienteDto
        if (cliente != null) {
            ventaDto.setCliente(clienteToDto(cliente)); // Usar el mapper de cliente
        } else {
            // Manejar el caso donde el cliente podría ser null (ej., cliente eliminado o no cargado)
            ventaDto.setCliente(null); // O un ClienteDto por defecto si lo prefieres
        }

        // Convertir LocalDateTime a Instant si tu entidad Venta usa LocalDateTime
        if (venta.getFechaVenta() != null) {
            ventaDto.setFechaVenta(venta.getFechaVenta());
            // Asegúrate de la zona horaria correcta. ZoneOffset.UTC es un buen punto de partida.
        }

        ventaDto.setTotalGlobalVenta(venta.getTotalGlobalVenta());

        return ventaDto;
    }
    public ClienteDto clienteToDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        return dto;
    }

    private DetalleVentaDto mapDetalleToRespuestaDTO(DetalleVenta detalle) {
        DetalleVentaDto dto = new DetalleVentaDto();
        Optional<ProductoDto> productoOpt = servicioProducto.obtenerProductoPorId(detalle.getProducto().getId());
        dto.setProducto(productoOpt.get());
        dto.setCantidadVendida(detalle.getCantidadVendida());
        dto.setPrecioUnitarioVenta(detalle.getPrecioUnitarioVenta());
        //dto.setSubtotal(detalle.getPrecioUnitarioVenta().multiply(new BigDecimal(detalle.getCantidadVendida())));

        return dto;
    }
}
