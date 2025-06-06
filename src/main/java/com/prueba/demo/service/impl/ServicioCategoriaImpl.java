package com.prueba.demo.service.impl;

import com.prueba.demo.service.ServicioCategoria;
import com.prueba.demo.entity.Categoria;
import com.prueba.demo.dto.CategoriaDto;
import com.prueba.demo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioCategoriaImpl implements ServicioCategoria {

    // Inyección de dependencias (en un proyecto Spring, esto sería con @Autowired)
    private final CategoriaRepository categoriaRepository;

    public ServicioCategoriaImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Método auxiliar para convertir una entidad Categoria a un DTO de respuesta.
     * Este es el core de tu solicitud.
     * @param categoria La entidad Categoria a convertir.
     * @return Un CategoriaRespuestaDTO.
     */
    private CategoriaDto mapToDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

    @Override
    public CategoriaDto crearCategoria(String nombre, String descripcion) {
        // Validación de negocio: El nombre de la categoría debe ser único
        if (categoriaRepository.findByNombreContains(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + nombre);
        }

        Categoria nuevaCategoria = new Categoria(nombre, descripcion);
        Categoria categoriaGuardada = categoriaRepository.save(nuevaCategoria);
        return mapToDTO(categoriaGuardada); // Convertir y devolver el DTO
    }

    @Override
    public Optional<CategoriaDto> obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id)
                .map(this::mapToDTO); // Usa el método de mapeo
    }

    @Override
    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        // Este método devuelve la entidad completa, ya que es probable que otros servicios
        // (como ServicioProductoImpl) necesiten el ID de la categoría para asociarla.
        return categoriaRepository.findByNombreContains(nombre);
    }

    @Override
    public List<CategoriaDto> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToDTO) // Mapea cada entidad a un DTO
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaDto actualizarCategoria(Integer id, String nuevoNombre, String nuevaDescripcion) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

        // Validación de negocio: Si se cambia el nombre, debe ser único
        if (nuevoNombre != null && !nuevoNombre.isEmpty() && !nuevoNombre.equals(categoriaExistente.getNombre())) {
            if (categoriaRepository.findByNombreContains(nuevoNombre).isPresent()) {
                throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + nuevoNombre);
            }
            categoriaExistente.setNombre(nuevoNombre);
        }

        if (nuevaDescripcion != null) {
            categoriaExistente.setDescripcion(nuevaDescripcion);
        }

        Categoria categoriaActualizada = categoriaRepository.save(categoriaExistente);
        return mapToDTO(categoriaActualizada); // Convertir y devolver el DTO
    }

    @Override
    public boolean eliminarCategoria(Integer id) {
        // En una aplicación real, aquí deberías verificar si hay productos asociados a esta categoría
        // Si los hay, deberías decidir si eliminar en cascada, reasignar los productos a una categoría por defecto,
        // o lanzar una excepción para evitar la eliminación.
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}