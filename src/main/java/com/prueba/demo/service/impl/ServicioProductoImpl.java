package com.prueba.demo.service.impl;

import com.prueba.demo.dto.CategoriaDto;
import com.prueba.demo.service.ServicioProducto;
import com.prueba.demo.service.ServicioCategoria;
import com.prueba.demo.entity.Producto;
import com.prueba.demo.dto.ProductoDto;
import com.prueba.demo.repository.ProductoRepository;
import com.prueba.demo.repository.CategoriaRepository; // Suponiendo que tienes un CategoriaRepository
import com.prueba.demo.entity.Categoria; // Modelo de Categoría
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class ServicioProductoImpl implements ServicioProducto {

    // Inyección de dependencias (en un proyecto Spring, esto sería con @Autowired)
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ServicioCategoria servicioCategoria;

    public ServicioProductoImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                                ServicioCategoria servicioCategoria) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.servicioCategoria = servicioCategoria;
    }

    @Override
    public ProductoDto crearProducto(ProductoDto productoDTO) {
        // Validación de negocio: SKU debe ser único
        if (productoRepository.findBySku(productoDTO.getSku()).isPresent()) {
            System.out.println("Ya existe un producto con el SKU: " + productoDTO.getSku());
            throw new IllegalArgumentException("Ya existe un producto con el SKU: " + productoDTO.getSku());
        }

        // Obtener o crear la categoría
        Categoria categoria = categoriaRepository.findByNombreContains(productoDTO.getCategoria().getNombre())
                .orElseGet(() -> {
                    Categoria nuevaCategoria = new Categoria(null, productoDTO.getCategoria().getNombre(), "Descripción por defecto");
                    return categoriaRepository.save(nuevaCategoria); // Guardar la nueva categoría
                });

        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setSku(productoDTO.getSku());
        producto.setPrecioUnitario(productoDTO.getPrecioUnitario());
        producto.setCantidadEnStock(productoDTO.getCantidadEnStock());
        producto.setCategoria(categoria); // Asigna el ID de la categoría

        Producto productoGuardado = productoRepository.save(producto);
        return mapToRespuestaDTO(productoGuardado, categoria);
    }

    @Override
    public Optional<ProductoDto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    Optional<Categoria> categoriaOpt = categoriaRepository.findById(producto.getCategoria().getId());
                    return mapToRespuestaDTO(producto, categoriaOpt.get());
                });
    }

    @Override
    public List<ProductoDto> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
                .map(producto -> {
                    Optional<Categoria> categoriaOpt = categoriaRepository.findById(producto.getCategoria().getId());
                    Categoria categoria = categoriaOpt.get();
                    return mapToRespuestaDTO(producto, categoria);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDto actualizarProducto(Integer id, ProductoDto productoDTO) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        // Obtener o crear la categoría
        Categoria categoria = categoriaRepository.findByNombreContains(productoDTO.getCategoria().getNombre())
                .orElseGet(() -> {
                    Categoria nuevaCategoria = new Categoria(null, productoDTO.getCategoria().getNombre(), "Descripción por defecto");
                    return categoriaRepository.save(nuevaCategoria); // Guardar la nueva categoría
                });

        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setPrecioUnitario(productoDTO.getPrecioUnitario());
        productoExistente.setCategoria(categoria); // Actualiza el ID de la categoría

        Producto productoActualizado = productoRepository.save(productoExistente);
        return mapToRespuestaDTO(productoActualizado, categoria);
    }

    @Override
    public boolean eliminarProducto(Integer id) {
        // En una aplicación real, aquí deberías verificar si el producto está asociado a ventas
        // antes de eliminarlo o implementar una eliminación lógica (marcar como inactivo).
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductoDto> obtenerProductosPorCategoria(String categoriaNombre) {
        // Primero, encontrar la categoría por nombre
        Optional<Categoria> categoriaOpt = categoriaRepository.findByNombreContains(categoriaNombre);
        if (categoriaOpt.isEmpty()) {
            return List.of(); // Si la categoría no existe, no hay productos para ella
        }
        Categoria categoria = categoriaOpt.get();

        return productoRepository.findByCategoriaId(categoria.getId()).stream()
                .map(producto -> mapToRespuestaDTO(producto,categoria ))
                .collect(Collectors.toList());
    }

    @Override
    public void disminuirStock(Integer productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

        if (producto.getCantidadEnStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre() + ". Disponible: " + producto.getCantidadEnStock() + ", Solicitado: " + cantidad);
        }

        producto.setCantidadEnStock(producto.getCantidadEnStock() - cantidad);
        productoRepository.save(producto); // Guarda el producto con el stock actualizado
    }

    @Override
    public Optional<Producto> obtenerProductoDominioPorId(Integer id) {
        return productoRepository.findById(id);
    }

    // Método de mapeo de entidad a DTO de respuesta
    private ProductoDto mapToRespuestaDTO(Producto producto, Categoria categoria) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setSku(producto.getSku());
        dto.setPrecioUnitario(producto.getPrecioUnitario());
        dto.setCantidadEnStock(producto.getCantidadEnStock());
        CategoriaDto categoriaDto= servicioCategoria.obtenerCategoriaPorId(categoria.getId()).get();
        dto.setCategoria(categoriaDto);
        return dto;
    }
}

