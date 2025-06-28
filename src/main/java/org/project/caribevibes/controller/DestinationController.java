package org.project.caribevibes.controller;

import org.project.caribevibes.dto.response.DestinationResponseDTO;
import org.project.caribevibes.entity.destination.Destination;
import org.project.caribevibes.entity.destination.Activity;
import org.project.caribevibes.entity.destination.Experience;
import org.project.caribevibes.service.destination.DestinationService;
import org.project.caribevibes.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operaciones de destinos, actividades y experiencias.
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * la gestión de destinos turísticos y sus actividades asociadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private static final Logger logger = LoggerFactory.getLogger(DestinationController.class);

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private org.project.caribevibes.repository.destination.DestinationRepository destinationRepository;

    /**
     * Obtiene todos los destinos activos con paginación.
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: name)
     * @param sortDir Dirección de ordenamiento (default: asc)
     * @return ResponseEntity con página de destinos
     */
    @GetMapping
    public ResponseEntity<Page<DestinationResponseDTO>> getAllDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.debug("Obteniendo destinos - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDir);

        // Ya no necesitamos crear el objeto Sort aquí ya que el servicio lo maneja
        Page<DestinationResponseDTO> destinations = destinationService.getAllDestinations(page, size, sortBy, sortDir);
        
        logger.debug("Retornando {} destinos de {} total", destinations.getNumberOfElements(), destinations.getTotalElements());
        return ResponseEntity.ok(destinations);
    }

    /**
     * Obtiene un destino específico por su ID.
     * 
     * @param id ID del destino
     * @return ResponseEntity con los detalles del destino
     */
    @GetMapping("/{id}")
    public ResponseEntity<DestinationResponseDTO> getDestinationById(@PathVariable Long id) {
        logger.debug("Obteniendo destino por ID: {}", id);

        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino", "id", id));

        DestinationResponseDTO destinationDTO = destinationService.convertToDestinationDTO(destination);
        logger.debug("Destino encontrado: {}", destination.getName());
        return ResponseEntity.ok(destinationDTO);
    }

    /**
     * Busca destinos por país.
     * 
     * @param country País a buscar
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de destinos del país
     */
    @GetMapping("/by-country/{country}")
    public ResponseEntity<Page<DestinationResponseDTO>> getDestinationsByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.debug("Buscando destinos por país: {} - página: {}, tamaño: {}", 
                    country, page, size);

        // Usar filterDestinations pasando el país como término de búsqueda
        // Esto es una solución temporal, se debería implementar un método específico
        Page<DestinationResponseDTO> destinations = destinationService.filterDestinations(null, null, country, page, size);
        
        logger.debug("Encontrados {} destinos para país: {}", destinations.getNumberOfElements(), country);
        return ResponseEntity.ok(destinations);
    }

    /**
     * Busca destinos por nombre.
     * 
     * @param name Nombre o parte del nombre del destino
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de destinos que coinciden con el nombre
     */
    @GetMapping("/search")
    public ResponseEntity<Page<DestinationResponseDTO>> searchDestinationsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.debug("Buscando destinos por nombre: '{}' - página: {}, tamaño: {}", 
                    name, page, size);

        // Usar el método searchDestinations del servicio
        Page<DestinationResponseDTO> destinations = destinationService.searchDestinations(name, page, size);
        
        logger.debug("Encontrados {} destinos con nombre que contiene: '{}'", 
                    destinations.getNumberOfElements(), name);
        return ResponseEntity.ok(destinations);
    }

    /**
     * Obtiene los destinos más populares.
     * 
     * @param limit Número máximo de destinos a retornar (default: 10)
     * @return ResponseEntity con lista de destinos populares
     */
    @GetMapping("/popular")
    public ResponseEntity<List<DestinationResponseDTO>> getPopularDestinations(
            @RequestParam(defaultValue = "10") int limit) {

        logger.debug("Obteniendo top {} destinos populares", limit);

        List<DestinationResponseDTO> destinations = destinationService.getPopularDestinations(limit);
        
        logger.debug("Retornando {} destinos populares", destinations.size());
        return ResponseEntity.ok(destinations);
    }

    /**
     * Obtiene las actividades de un destino.
     * 
     * @param destinationId ID del destino
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de actividades del destino
     */
    @GetMapping("/{destinationId}/activities")
    public ResponseEntity<Page<Activity>> getActivitiesByDestination(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo actividades para destino ID: {} - página: {}, tamaño: {}", 
                    destinationId, page, size);
        
        // Verificar que el destino existe
        destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destino", "id", destinationId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Activity> activities = destinationService.getActivitiesByDestination(destinationId, pageable);
        
        logger.debug("Encontradas {} actividades para destino ID: {}", activities.getNumberOfElements(), destinationId);
        return ResponseEntity.ok(activities);
    }

    /**
     * Obtiene las experiencias de un destino.
     * 
     * @param destinationId ID del destino
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de experiencias del destino
     */
    @GetMapping("/{destinationId}/experiences")
    public ResponseEntity<Page<Experience>> getExperiencesByDestination(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo experiencias para destino ID: {} - página: {}, tamaño: {}", 
                    destinationId, page, size);
        
        // Verificar que el destino existe
        destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destino", "id", destinationId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Experience> experiences = destinationService.getExperiencesByDestination(destinationId, pageable);
        
        logger.debug("Encontradas {} experiencias para destino ID: {}", experiences.getNumberOfElements(), destinationId);
        return ResponseEntity.ok(experiences);
    }

    /**
     * Obtiene una actividad específica por su ID.
     * 
     * @param activityId ID de la actividad
     * @return ResponseEntity con los detalles de la actividad
     */
    @GetMapping("/activities/{activityId}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long activityId) {
        logger.debug("Obteniendo actividad por ID: {}", activityId);
        
        Activity activity = destinationService.findActivityById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", activityId));
        
        logger.debug("Actividad encontrada: {}", activity.getName());
        return ResponseEntity.ok(activity);
    }

    /**
     * Obtiene una experiencia específica por su ID.
     * 
     * @param experienceId ID de la experiencia
     * @return ResponseEntity con los detalles de la experiencia
     */
    @GetMapping("/experiences/{experienceId}")
    public ResponseEntity<Experience> getExperienceById(@PathVariable Long experienceId) {
        logger.debug("Obteniendo experiencia por ID: {}", experienceId);
        
        Experience experience = destinationService.findExperienceById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia", "id", experienceId));
        
        logger.debug("Experiencia encontrada: {}", experience.getName());
        return ResponseEntity.ok(experience);
    }

    /**
     * Crea un nuevo destino (solo para administradores).
     * 
     * @param destination Datos del destino a crear
     * @return ResponseEntity con el destino creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Destination> createDestination(@RequestBody Destination destination) {
        logger.info("Creando nuevo destino: {}", destination.getName());
        
        Destination createdDestination = destinationService.createDestination(destination);
        
        logger.info("Destino creado exitosamente con ID: {}", createdDestination.getId());
        return ResponseEntity.status(201).body(createdDestination);
    }

    /**
     * Actualiza un destino existente (solo para administradores).
     * 
     * @param id ID del destino a actualizar
     * @param destination Datos actualizados del destino
     * @return ResponseEntity con el destino actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id, @RequestBody Destination destination) {
        logger.info("Actualizando destino con ID: {}", id);
        
        Destination updatedDestination = destinationService.updateDestination(id, destination)
                .orElseThrow(() -> new ResourceNotFoundException("Destino", "id", id));
        
        logger.info("Destino actualizado exitosamente: {}", updatedDestination.getName());
        return ResponseEntity.ok(updatedDestination);
    }

    /**
     * Desactiva un destino (solo para administradores).
     * 
     * @param id ID del destino a desactivar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateDestination(@PathVariable Long id) {
        logger.info("Desactivando destino con ID: {}", id);
        
        boolean deactivated = destinationService.deactivateDestination(id);
        if (!deactivated) {
            throw new ResourceNotFoundException("Destino", "id", id);
        }
        
        logger.info("Destino desactivado exitosamente con ID: {}", id);
        return ResponseEntity.ok("Destino desactivado exitosamente");
    }

    /**
     * Endpoint de salud para verificar que el servicio de destinos está funcionando.
     * 
     * @return ResponseEntity con el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = Map.of(
            "status", "OK",
            "service", "Destination Service",
            "message", "Servicio de destinos funcionando correctamente"
        );
        return ResponseEntity.ok(response);
    }
}
