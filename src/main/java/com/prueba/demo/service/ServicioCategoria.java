package com.prueba.demo.service;


import com.prueba.demo.dto.CategoriaDto;
import com.prueba.demo.entity.Categoria;

import java.util.List;
import java.util.Optional;

// @Service // Anotación de Spring para indicar que es un componente de servicio
public interface ServicioCategoria {

    /**
     * Crea una nueva categoría.
     * @param nombre El nombre de la categoría.
     * @param descripcion La descripción de la categoría.
     * @return El DTO de la categoría creada.
     * @throws IllegalArgumentException Si el nombre de la categoría ya existe.
     */
    CategoriaDto crearCategoria(String nombre, String descripcion);

    /**
     * Obtiene una categoría por su ID.
     * @param id El ID de la categoría.
     * @return Un Optional que contiene el DTO de la categoría si existe, o un Optional vacío.
     */
    Optional<CategoriaDto> obtenerCategoriaPorId(Integer id);

    /**
     * Obtiene una categoría por su nombre.
     * @param nombre El nombre de la categoría.
     * @return Un Optional que contiene la entidad Categoria si existe, o un Optional vacío.
     * Este método devuelve la entidad para uso interno por otros servicios (ej. ServicioProducto).
     */
    Optional<Categoria> obtenerCategoriaPorNombre(String nombre);

    /**
     * Obtiene todas las categorías.
     * @return Una lista de DTOs de todas las categorías.
     */
    List<CategoriaDto> obtenerTodasLasCategorias();

    /**
     * Actualiza una categoría existente.
     * @param id El ID de la categoría a actualizar.
     * @param nuevoNombre El nuevo nombre de la categoría (puede ser null para no cambiar).
     * @param nuevaDescripcion La nueva descripción de la categoría (puede ser null para no cambiar).
     * @return El DTO de la categoría actualizada.
     * @throws IllegalArgumentException Si la categoría no existe o el nuevo nombre ya está en uso.
     */
    CategoriaDto actualizarCategoria(Integer id, String nuevoNombre, String nuevaDescripcion);

    /**
     * Elimina una categoría por su ID.
     * @param id El ID de la categoría a eliminar.
     * @return true si la categoría fue eliminada, false si no se encontró.
     */
    boolean eliminarCategoria(Integer id);
}

