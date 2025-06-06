package com.prueba.demo.Controller;


import com.prueba.demo.dto.ProductoDto;
import com.prueba.demo.service.ServicioProducto;

//import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ServicioProducto servicioProducto;

    public ProductoController(ServicioProducto servicioProducto) {
        this.servicioProducto = servicioProducto;
    }

    /**
     * POST /api/productos – Crear un producto.
     * @param productoCreacionDTO DTO con los datos del nuevo producto.
     * @return ResponseEntity con el ProductoDto del producto creado y estado 201.
     */
    @PostMapping
    public ResponseEntity<ProductoDto> crearProducto( @RequestBody ProductoDto productoCreacionDTO) {
        ProductoDto nuevoProducto = servicioProducto.crearProducto(productoCreacionDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    /**
     * GET /api/productos – Obtener todos los productos.
     * @return ResponseEntity con una lista de ProductoDto y estado 200.
     */
    @GetMapping
    public ResponseEntity<List<ProductoDto>> obtenerTodosLosProductos() {
        List<ProductoDto> productos = servicioProducto.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos); // HttpStatus.OK (200) por defecto
    }

    /**
     * GET /api/productos/{id} – Obtener un producto por ID.
     * @param id ID del producto a buscar.
     * @return ResponseEntity con el ProductoDto si se encuentra (200), o 404 si no.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtenerProductoPorId(@PathVariable Integer id) {
        return servicioProducto.obtenerProductoPorId(id)
                .map(ResponseEntity::ok) // Si encuentra el producto, devuelve 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si no lo encuentra, devuelve 404 Not Found
    }

    /**
     * PUT /api/productos/{id} – Actualizar un producto (datos descriptivos).
     * @param id ID del producto a actualizar.
     * @param productoActualizacionDTO DTO con los datos a actualizar.
     * @return ResponseEntity con el ProductoDto actualizado (200), o 404 si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizarProducto(@PathVariable Integer id,
                                                          @RequestBody ProductoDto productoActualizacionDTO) {
        try {
            ProductoDto productoActualizado = servicioProducto.actualizarProducto(id, productoActualizacionDTO);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            // Esto podría ser un 404 si el producto no existe o 400 si hay un problema de validación
            // El @ControllerAdvice manejará estas excepciones para dar una respuesta consistente
            return ResponseEntity.notFound().build(); // Si la excepción es por producto no encontrado
        }
    }

    /**
     * DELETE /api/productos/{id} – Eliminar un producto.
     * @param id ID del producto a eliminar.
     * @return ResponseEntity con estado 204 (No Content) si se elimina, o 404 si no se encuentra.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        boolean eliminado = servicioProducto.eliminarProducto(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * GET /api/productos/filtrar?categoria={nombreCategoria} – Obtener productos por nombre de categoría.
     * @param nombreCategoria Nombre de la categoría por la cual filtrar.
     * @return ResponseEntity con una lista de ProductoDto y estado 200.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<ProductoDto>> obtenerProductosPorCategoria(@RequestParam("categoria") String nombreCategoria) {
        List<ProductoDto> productos = servicioProducto.obtenerProductosPorCategoria(nombreCategoria);
        return ResponseEntity.ok(productos);
    }
}

