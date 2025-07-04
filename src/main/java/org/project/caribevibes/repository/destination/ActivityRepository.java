package org.project.caribevibes.repository.destination;

import org.project.caribevibes.entity.destination.Activity;
import org.project.caribevibes.entity.destination.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de actividades en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Activity, incluyendo filtros por destino, precio y disponibilidad.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Obtiene todas las actividades de un destino específico
     * 
     * @param destination Destino del cual obtener las actividades
     * @return Lista de actividades del destino
     */
    List<Activity> findByDestination(Destination destination);

    /**
     * Obtiene todas las actividades de un destino por ID
     * 
     * @param destinationId ID del destino
     * @return Lista de actividades del destino
     */
    List<Activity> findByDestinationId(Long destinationId);

    /**
     * Obtiene todas las actividades de un destino con paginación
     *
     * @param destinationId ID del destino
     * @param pageable Configuración de paginación
     * @return Página de actividades del destino
     */
    Page<Activity> findByDestinationId(Long destinationId, Pageable pageable);

    /**
     * Obtiene actividades disponibles de un destino específico
     * 
     * @param destinationId ID del destino
     * @return Lista de actividades disponibles
     */
    List<Activity> findByDestinationIdAndIsAvailableTrue(Long destinationId);

    /**
     * Busca actividades por nombre (búsqueda parcial, sin distinción de mayúsculas)
     * 
     * @param name Nombre o parte del nombre de la actividad
     * @return Lista de actividades que coinciden con el nombre
     */
    List<Activity> findByNameContainingIgnoreCase(String name);

    /**
     * Obtiene actividades con precio menor o igual al especificado
     * 
     * @param maxPrice Precio máximo
     * @return Lista de actividades dentro del rango de precio
     */
    List<Activity> findByPriceLessThanEqual(BigDecimal maxPrice);

    /**
     * Obtiene actividades en un rango de precios
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de actividades en el rango de precios
     */
    List<Activity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Obtiene actividades por nivel de dificultad
     * 
     * @param difficultyLevel Nivel de dificultad
     * @return Lista de actividades con el nivel de dificultad especificado
     */
    List<Activity> findByDifficultyLevel(Activity.DifficultyLevel difficultyLevel);

    /**
     * Obtiene actividades disponibles ordenadas por precio ascendente
     * 
     * @return Lista de actividades ordenadas por precio
     */
    List<Activity> findByIsAvailableTrueOrderByPriceAsc();

    /**
     * Obtiene actividades con capacidad mayor o igual a la especificada
     * 
     * @param minCapacity Capacidad mínima requerida
     * @return Lista de actividades con capacidad suficiente
     */
    List<Activity> findByMaxCapacityGreaterThanEqual(Integer minCapacity);

    /**
     * Busca actividades por múltiples criterios
     * 
     * @param destinationId ID del destino (puede ser null)
     * @param maxPrice Precio máximo (puede ser null)
     * @param difficultyLevel Nivel de dificultad (puede ser null)
     * @param minCapacity Capacidad mínima (puede ser null)
     * @return Lista de actividades que cumplen los criterios
     */
    @Query("SELECT a FROM Activity a WHERE " +
           "(:destinationId IS NULL OR a.destination.id = :destinationId) AND " +
           "(:maxPrice IS NULL OR a.price <= :maxPrice) AND " +
           "(:difficultyLevel IS NULL OR a.difficultyLevel = :difficultyLevel) AND " +
           "(:minCapacity IS NULL OR a.maxCapacity >= :minCapacity) AND " +
           "a.isAvailable = true")
    List<Activity> findByMultipleCriteria(@Param("destinationId") Long destinationId,
                                        @Param("maxPrice") BigDecimal maxPrice,
                                        @Param("difficultyLevel") Activity.DifficultyLevel difficultyLevel,
                                        @Param("minCapacity") Integer minCapacity);

    /**
     * Obtiene el número de actividades disponibles por destino
     * 
     * @param destinationId ID del destino
     * @return Número de actividades disponibles
     */
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.destination.id = :destinationId AND a.isAvailable = true")
    long countAvailableActivitiesByDestination(@Param("destinationId") Long destinationId);

    /**
     * Obtiene actividades populares (con más reservas)
     * 
     * @param limit Número máximo de actividades a retornar
     * @return Lista de actividades más populares
     */
    @Query("SELECT a FROM Activity a " +
           "LEFT JOIN BookingActivity ba ON a.id = ba.activity.id " +
           "WHERE a.isAvailable = true " +
           "GROUP BY a.id " +
           "ORDER BY COUNT(ba.id) DESC")
    List<Activity> findMostPopularActivities(@Param("limit") int limit);

    /**
     * Busca actividades por duración específica
     * 
     * @param duration Duración de la actividad
     * @return Lista de actividades con la duración especificada
     */
    List<Activity> findByDurationContainingIgnoreCase(String duration);

    /**
     * Obtiene estadísticas de precios de actividades por destino
     * 
     * @param destinationId ID del destino
     * @return Array con [precio_minimo, precio_maximo, precio_promedio]
     */
    @Query("SELECT MIN(a.price), MAX(a.price), AVG(a.price) " +
           "FROM Activity a WHERE a.destination.id = :destinationId AND a.isAvailable = true")
    Object[] getPriceStatisticsByDestination(@Param("destinationId") Long destinationId);

    /**
     * Obtiene una actividad activa por su ID
     * 
     * @param id ID de la actividad
     * @return Optional con la actividad encontrada o vacío si no existe
     */
    Optional<Activity> findByIdAndIsAvailableTrue(Long id);

    /**
     * Obtiene todas las actividades de un destino por ID ordenadas por nombre
     * 
     * @param destinationId ID del destino
     * @return Lista de actividades del destino ordenadas por nombre
     */
    List<Activity> findByDestinationIdOrderByNameAsc(Long destinationId);
}
