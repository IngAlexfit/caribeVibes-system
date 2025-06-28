package org.project.caribevibes.repository.destination;

import org.project.caribevibes.entity.destination.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de países en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Country.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Busca un país por su código ISO
     * 
     * @param code Código ISO del país
     * @return País encontrado o Optional vacío
     */
    Optional<Country> findByCode(String code);

    /**
     * Busca un país por su nombre
     * 
     * @param name Nombre del país
     * @return País encontrado o Optional vacío
     */
    Optional<Country> findByName(String name);

    /**
     * Obtiene todos los países activos
     * 
     * @return Lista de países activos
     */
    List<Country> findByIsActiveTrueOrderByNameAsc();

    /**
     * Obtiene países por continente
     * 
     * @param continent Nombre del continente
     * @return Lista de países del continente
     */
    List<Country> findByContinentAndIsActiveTrueOrderByNameAsc(String continent);

    /**
     * Busca países que tengan destinos disponibles
     * 
     * @return Lista de países con destinos
     */
    @Query("SELECT DISTINCT c FROM Country c JOIN c.destinations d WHERE c.isActive = true AND d.id IS NOT NULL ORDER BY c.name")
    List<Country> findCountriesWithDestinations();
}
