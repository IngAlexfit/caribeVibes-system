package org.project.caribevibes.repository.destination;

import org.project.caribevibes.entity.destination.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de destinos en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Destination, incluyendo filtros por experiencias, precios
 * y búsquedas por slug.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    /**
     * Busca un destino por su slug único
     * 
     * @param slug Identificador único del destino
     * @return Optional con el destino encontrado o vacío si no existe
     */
    Optional<Destination> findBySlug(String slug);

    /**
     * Verifica si existe un destino con el slug especificado
     * 
     * @param slug Slug a verificar
     * @return true si existe un destino con ese slug
     */
    boolean existsBySlug(String slug);

    /**
     * Busca destinos por nombre (búsqueda parcial, sin distinción de mayúsculas)
     * 
     * @param name Nombre o parte del nombre del destino
     * @return Lista de destinos que coinciden con el nombre
     */
    List<Destination> findByNameContainingIgnoreCase(String name);

    /**
     * Obtiene destinos que incluyen una experiencia específica (consulta nativa)
     *
     * @param experience Tipo de experiencia a buscar
     * @return Lista de destinos que ofrecen la experiencia especificada
     */
    @Query(value = "SELECT * FROM destinations d WHERE JSON_CONTAINS(d.experiences, :experience)", nativeQuery = true)
    List<Destination> findByExperiencesContaining(@Param("experience") String experience);

    /**
     * Obtiene destinos que incluyen un tag específico (consulta nativa)
     *
     * @param tag Tag a buscar en los destinos
     * @return Lista de destinos que contienen el tag especificado
     */
    @Query(value = "SELECT * FROM destinations d WHERE JSON_CONTAINS(d.tags, :tag)", nativeQuery = true)
    List<Destination> findByTagsContaining(@Param("tag") String tag);

    /**
     * Obtiene destinos con precio de temporada baja menor o igual al especificado
     * 
     * @param maxPrice Precio máximo de temporada baja
     * @return Lista de destinos dentro del rango de precio
     */
    List<Destination> findByLowSeasonPriceLessThanEqual(BigDecimal maxPrice);

    /**
     * Obtiene destinos con precio de temporada alta menor o igual al especificado
     * 
     * @param maxPrice Precio máximo de temporada alta
     * @return Lista de destinos dentro del rango de precio
     */
    List<Destination> findByHighSeasonPriceLessThanEqual(BigDecimal maxPrice);

    /**
     * Obtiene destinos en un rango de precios para temporada baja
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de destinos en el rango de precios
     */
    List<Destination> findByLowSeasonPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Busca destinos por múltiples criterios usando consulta nativa (MySQL JSON)
     *
     * @param experience Experiencia requerida (puede ser null)
     * @param maxPrice Precio máximo (puede ser null)
     * @param searchTerm Término de búsqueda en nombre o descripción (puede ser null)
     * @return Lista de destinos que cumplen los criterios
     */
    @Query(value = "SELECT DISTINCT * FROM destinations d WHERE " +
           "(:experience IS NULL OR JSON_CONTAINS(d.experiences, :experience)) AND " +
           "(:maxPrice IS NULL OR d.low_season_price <= :maxPrice) AND " +
           "(:searchTerm IS NULL OR  LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR  LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))" ,
           nativeQuery = true)
    List<Destination> findByMultipleCriteria(@Param("experience") String experience,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           @Param("searchTerm") String searchTerm);

    /**
     * Obtiene destinos ordenados por precio de temporada baja ascendente
     * 
     * @return Lista de destinos ordenados por precio
     */
    List<Destination> findAllByOrderByLowSeasonPriceAsc();

    /**
     * Obtiene destinos ordenados por nombre alfabéticamente
     * 
     * @return Lista de destinos ordenados por nombre
     */
    List<Destination> findAllByOrderByNameAsc();

    /**
     * Obtiene los destinos más recientes
     * 
     * @param limit Número máximo de destinos a retornar
     * @return Lista de destinos más recientes
     */
    @Query("SELECT d FROM Destination d ORDER BY d.createdAt DESC")
    List<Destination> findLatestDestinations(@Param("limit") int limit);

    /**
     * Cuenta el número total de destinos
     * 
     * @return Número total de destinos en el sistema
     */
    @Query("SELECT COUNT(d) FROM Destination d")
    long countAllDestinations();

    /**
     * Busca destinos que contengan todas las experiencias especificadas
     * 
     * @param experiences Lista de experiencias requeridas
     * @return Lista de destinos que contienen todas las experiencias
     * 
     * Nota: Esta implementación recupera todos los destinos y filtra en memoria.
     */
    default List<Destination> findByAllExperiences(List<String> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return findAll();
        }
        return findAll().stream()
                .filter(destination -> destination.getExperiences() != null &&
                        destination.getExperiences().containsAll(experiences))
                .toList();
    }

    /**
     * Busca destinos que contengan una experiencia específica por su slug (consulta nativa JSON)
     * @param experienceSlug Slug de la experiencia
     * @return Lista de destinos con la experiencia especificada
     */
    @Query(value = "SELECT * FROM destinations d WHERE JSON_CONTAINS(d.experiences, :experienceSlug)", nativeQuery = true)
    List<Destination> findByExperiences_Slug(@Param("experienceSlug") String experienceSlug);

}
