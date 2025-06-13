package org.project.caribevibes.repository.destination;

import org.project.caribevibes.entity.destination.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de experiencias en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Experience, incluyendo búsquedas por slug y estado activo.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    /**
     * Busca una experiencia por su slug único
     * 
     * @param slug Identificador único de la experiencia
     * @return Optional con la experiencia encontrada o vacío si no existe
     */
    Optional<Experience> findBySlug(String slug);

    /**
     * Verifica si existe una experiencia con el slug especificado
     * 
     * @param slug Slug a verificar
     * @return true si existe una experiencia con ese slug
     */
    boolean existsBySlug(String slug);

    /**
     * Obtiene todas las experiencias activas
     * 
     * @return Lista de experiencias activas
     */
    List<Experience> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Obtiene todas las experiencias ordenadas por orden de visualización
     * 
     * @return Lista de experiencias ordenadas
     */
    List<Experience> findAllByOrderByDisplayOrderAsc();

    /**
     * Busca experiencias por nombre (búsqueda parcial, sin distinción de mayúsculas)
     * 
     * @param name Nombre o parte del nombre de la experiencia
     * @return Lista de experiencias que coinciden con el nombre
     */
    List<Experience> findByNameContainingIgnoreCase(String name);

    /**
     * Obtiene experiencias por estado activo
     * 
     * @param isActive Estado de la experiencia
     * @return Lista de experiencias con el estado especificado
     */
    List<Experience> findByIsActive(Boolean isActive);

    /**
     * Cuenta el número total de experiencias activas
     * 
     * @return Número de experiencias activas
     */
    long countByIsActiveTrue();

    /**
     * Obtiene el siguiente número de orden disponible para una nueva experiencia
     * 
     * @return Siguiente número de orden
     */
    @Query("SELECT COALESCE(MAX(e.displayOrder), 0) + 1 FROM Experience e")
    Integer getNextDisplayOrder();

    /**
     * Obtiene experiencias con un orden de visualización específico
     * 
     * @param displayOrder Orden de visualización
     * @return Lista de experiencias con el orden especificado
     */
    List<Experience> findByDisplayOrder(Integer displayOrder);

    /**
     * Obtiene experiencias ordenadas por nombre alfabéticamente
     * 
     * @return Lista de experiencias ordenadas por nombre
     */
    List<Experience> findAllByOrderByNameAsc();
}
