package com.prueba.demo.service;


import com.prueba.demo.entity.Producto;
import com.prueba.demo.dto.ProductoDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ServicioProducto {

    /**
     * Crea un nuevo producto.
     * @param productoDTO Datos del producto a crear.
     * @return El DTO del producto creado con su ID.
     * @throws IllegalArgumentException Si el SKU ya existe o los datos son inválidos.
     */
    ProductoDto crearProducto(ProductoDto productoDTO);

    /**
     * Obtiene un producto por su ID.
     * @param id El ID del producto.
     * @return Un Optional que contiene el DTO del producto si existe, o un Optional vacío.
     */
    Optional<ProductoDto> obtenerProductoPorId(Integer id);

    /**
     * Obtiene todos los productos.
     * @return Una lista de DTOs de productos.
     */
    List<ProductoDto> obtenerTodosLosProductos();

    /**
     * Actualiza la información descriptiva de un producto (nombre, precio, categoría).
     * @param id El ID del producto a actualizar.
     * @param productoDTO Datos a actualizar.
     * @return El DTO del producto actualizado.
     * @throws IllegalArgumentException Si el producto no existe o los datos son inválidos.
     */
    ProductoDto actualizarProducto(Integer id, ProductoDto productoDTO);

    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar.
     * @return true si el producto fue eliminado, false si no se encontró.
     */
    boolean eliminarProducto(Integer id);

    /**
     * Obtiene productos filtrados por el nombre de la categoría.
     * @param categoriaNombre El nombre de la categoría.
     * @return Una lista de DTOs de productos que pertenecen a esa categoría.
     */
    List<ProductoDto> obtenerProductosPorCategoria(String categoriaNombre);

    /**
     * Disminuye la cantidad en stock de un producto.
     * Esta es una operación interna, típicamente llamada por el ServicioVenta.
     * @param productoId El ID del producto.
     * @param cantidad La cantidad a disminuir.
     * @throws IllegalArgumentException Si el stock es insuficiente o el producto no existe.
     */
    void disminuirStock(Integer productoId, int cantidad); // Operación de negocio interna

    /**
     * Obtiene un producto por su ID para uso interno del servicio (no expone DTOs).
     * @param id El ID del producto.
     * @return Un Optional que contiene la entidad Producto si existe, o un Optional vacío.
     */
    Optional<Producto> obtenerProductoDominioPorId(Integer id); // Usado internamente por otros servicios
}
