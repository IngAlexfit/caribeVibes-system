package org.project.caribevibes.controller;

import org.project.caribevibes.dto.request.CreateActivityRequestDTO;
import org.project.caribevibes.dto.request.UpdateActivityRequestDTO;
import org.project.caribevibes.dto.response.ActivityResponseDTO;
import org.project.caribevibes.entity.destination.Activity;
import org.project.caribevibes.service.destination.ActivityService;
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
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operaciones administrativas de actividades.
 * 
 * Este controlador maneja todas las operaciones CRUD relacionadas con
 * la gestión de actividades por parte de administradores.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/admin/activities")
@PreAuthorize("hasRole('ADMIN')")
public class ActivityAdminController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityAdminController.class);

    @Autowired
    private ActivityService activityService;

    /**
     * Obtiene todas las actividades con paginación (para administradores).
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: name)
     * @param sortDir Dirección de ordenamiento (default: asc)
     * @return ResponseEntity con página de actividades
     */
    @GetMapping
    public ResponseEntity<Page<ActivityResponseDTO>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.debug("Admin obteniendo todas las actividades - página: {}, tamaño: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Activity> activities = activityService.findAllActivitiesForAdmin(pageable);
        
        Page<ActivityResponseDTO> activityDTOs = activities.map(this::convertToActivityResponseDTO);
        
        logger.debug("Retornando {} actividades de {} total", 
                    activityDTOs.getNumberOfElements(), activityDTOs.getTotalElements());
        return ResponseEntity.ok(activityDTOs);
    }

    /**
     * Obtiene una actividad específica por ID.
     * 
     * @param id ID de la actividad
     * @return ResponseEntity con los datos de la actividad
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        logger.debug("Admin obteniendo actividad con ID: {}", id);
        
        Activity activity = activityService.findActivityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));
        
        ActivityResponseDTO activityDTO = convertToActivityResponseDTO(activity);
        return ResponseEntity.ok(activityDTO);
    }

    /**
     * Crea una nueva actividad.
     * 
     * @param createActivityDTO Datos de la actividad a crear
     * @return ResponseEntity con la actividad creada
     */
    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody CreateActivityRequestDTO createActivityDTO) {
        logger.info("Creando nueva actividad: {}", createActivityDTO.getName());
        
        Activity createdActivity = activityService.createActivityFromDTO(createActivityDTO);
        ActivityResponseDTO activityDTO = convertToActivityResponseDTO(createdActivity);
        
        logger.info("Actividad creada exitosamente con ID: {}", createdActivity.getId());
        return ResponseEntity.status(201).body(activityDTO);
    }

    /**
     * Actualiza una actividad existente.
     * 
     * @param id ID de la actividad a actualizar
     * @param updateActivityDTO Datos actualizados de la actividad
     * @return ResponseEntity con la actividad actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(@PathVariable Long id, @Valid @RequestBody UpdateActivityRequestDTO updateActivityDTO) {
        logger.info("Actualizando actividad con ID: {}", id);
        
        Activity updatedActivity = activityService.updateActivityFromDTO(id, updateActivityDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad", "id", id));
        
        ActivityResponseDTO activityDTO = convertToActivityResponseDTO(updatedActivity);
        
        logger.info("Actividad actualizada exitosamente: {}", updatedActivity.getName());
        return ResponseEntity.ok(activityDTO);
    }

    /**
     * Desactiva una actividad (eliminación lógica).
     * 
     * @param id ID de la actividad a desactivar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deactivateActivity(@PathVariable Long id) {
        logger.info("Desactivando actividad con ID: {}", id);
        
        boolean deactivated = activityService.deactivateActivity(id);
        if (!deactivated) {
            throw new ResourceNotFoundException("Actividad", "id", id);
        }
        
        logger.info("Actividad desactivada exitosamente con ID: {}", id);
        return ResponseEntity.ok(Map.of("message", "Actividad desactivada exitosamente"));
    }

    /**
     * Reactiva una actividad previamente desactivada.
     * 
     * @param id ID de la actividad a reactivar
     * @return ResponseEntity con mensaje de confirmación
     */
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<Map<String, String>> reactivateActivity(@PathVariable Long id) {
        logger.info("Reactivando actividad con ID: {}", id);
        
        boolean reactivated = activityService.reactivateActivity(id);
        if (!reactivated) {
            throw new ResourceNotFoundException("Actividad", "id", id);
        }
        
        logger.info("Actividad reactivada exitosamente con ID: {}", id);
        return ResponseEntity.ok(Map.of("message", "Actividad reactivada exitosamente"));
    }

    /**
     * Obtiene actividades por destino para administradores.
     * 
     * @param destinationId ID del destino
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de actividades del destino
     */
    @GetMapping("/by-destination/{destinationId}")
    public ResponseEntity<Page<ActivityResponseDTO>> getActivitiesByDestination(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Admin obteniendo actividades para destino ID: {}", destinationId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Activity> activities = activityService.findActivitiesByDestinationForAdmin(destinationId, pageable);
        
        Page<ActivityResponseDTO> activityDTOs = activities.map(this::convertToActivityResponseDTO);
        
        logger.debug("Encontradas {} actividades para destino ID: {}", 
                    activityDTOs.getNumberOfElements(), destinationId);
        return ResponseEntity.ok(activityDTOs);
    }

    /**
     * Convierte una entidad Activity a ActivityResponseDTO.
     * 
     * @param activity Entidad Activity
     * @return ActivityResponseDTO
     */
    private ActivityResponseDTO convertToActivityResponseDTO(Activity activity) {
        ActivityResponseDTO dto = new ActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setDescription(activity.getDescription());
        dto.setPrice(activity.getPrice());
        dto.setDuration(activity.getDuration());
        dto.setDifficultyLevel(activity.getDifficultyLevel() != null ? 
                             activity.getDifficultyLevel().getDisplayName() : null);
        dto.setMaxCapacity(activity.getMaxCapacity());
        dto.setIsAvailable(activity.getIsAvailable());
        // Nota: createdAt y updatedAt no están en la entidad actual
        
        if (activity.getDestination() != null) {
            dto.setDestinationId(activity.getDestination().getId());
            dto.setDestinationName(activity.getDestination().getName());
        }
        
        return dto;
    }
}
