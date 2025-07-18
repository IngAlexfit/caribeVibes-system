package org.project.caribevibes.service.destination;

import org.project.caribevibes.dto.request.CreateActivityRequestDTO;
import org.project.caribevibes.dto.request.UpdateActivityRequestDTO;
import org.project.caribevibes.entity.destination.Activity;
import org.project.caribevibes.entity.destination.Destination;
import org.project.caribevibes.repository.destination.ActivityRepository;
import org.project.caribevibes.repository.destination.DestinationRepository;
import org.project.caribevibes.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para operaciones administrativas de actividades.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Service
@Transactional
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    /**
     * Obtiene todas las actividades (incluyendo inactivas) con paginación para administradores.
     * 
     * @param pageable Configuración de paginación
     * @return Page de actividades
     */
    @Transactional(readOnly = true)
    public Page<Activity> findAllActivitiesForAdmin(Pageable pageable) {
        logger.debug("Obteniendo todas las actividades para administrador con paginación: {}", pageable);
        return activityRepository.findAll(pageable);
    }

    /**
     * Busca una actividad por ID (incluyendo inactivas).
     * 
     * @param id ID de la actividad
     * @return Optional con la actividad si existe
     */
    @Transactional(readOnly = true)
    public Optional<Activity> findActivityById(Long id) {
        logger.debug("Buscando actividad con ID: {}", id);
        return activityRepository.findById(id);
    }

    /**
     * Crea una nueva actividad desde un DTO.
     * 
     * @param createActivityDTO DTO con los datos de la actividad
     * @return Actividad creada
     */
    public Activity createActivityFromDTO(CreateActivityRequestDTO createActivityDTO) {
        logger.info("Creando nueva actividad: {}", createActivityDTO.getName());
        
        // Buscar el destino si se proporciona el ID
        Destination destination = null;
        if (createActivityDTO.getDestinationId() != null) {
            destination = destinationRepository.findById(createActivityDTO.getDestinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destino no encontrado con ID: " + createActivityDTO.getDestinationId()));
        }
        
        Activity activity = new Activity();
        activity.setName(createActivityDTO.getName());
        activity.setDescription(createActivityDTO.getDescription());
        activity.setPrice(createActivityDTO.getPrice());
        activity.setDuration(createActivityDTO.getDuration()); // String en lugar de Integer
        
        // Convertir String a enum DifficultyLevel
        if (createActivityDTO.getDifficultyLevel() != null) {
            try {
                Activity.DifficultyLevel difficultyLevel = Activity.DifficultyLevel.valueOf(
                    createActivityDTO.getDifficultyLevel().toUpperCase());
                activity.setDifficultyLevel(difficultyLevel);
            } catch (IllegalArgumentException e) {
                // Si no es un valor válido del enum, usar EASY por defecto
                activity.setDifficultyLevel(Activity.DifficultyLevel.EASY);
            }
        }
        
        activity.setMaxCapacity(createActivityDTO.getMaxCapacity());
        activity.setIsAvailable(createActivityDTO.getIsAvailable()); // isAvailable en lugar de active
        
        if (destination != null) {
            activity.setDestination(destination);
        }
        
        Activity savedActivity = activityRepository.save(activity);
        logger.info("Actividad creada exitosamente con ID: {}", savedActivity.getId());
        
        return savedActivity;
    }

    /**
     * Actualiza una actividad existente desde un DTO.
     * 
     * @param id ID de la actividad a actualizar
     * @param updateActivityDTO DTO con los datos actualizados
     * @return Optional con la actividad actualizada
     */
    public Optional<Activity> updateActivityFromDTO(Long id, UpdateActivityRequestDTO updateActivityDTO) {
        logger.info("Actualizando actividad con ID: {}", id);
        
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setName(updateActivityDTO.getName());
                    activity.setDescription(updateActivityDTO.getDescription());
                    activity.setPrice(updateActivityDTO.getPrice());
                    activity.setDuration(updateActivityDTO.getDuration()); // String en lugar de Integer
                    
                    // Convertir String a enum DifficultyLevel
                    if (updateActivityDTO.getDifficultyLevel() != null) {
                        try {
                            Activity.DifficultyLevel difficultyLevel = Activity.DifficultyLevel.valueOf(
                                updateActivityDTO.getDifficultyLevel().toUpperCase());
                            activity.setDifficultyLevel(difficultyLevel);
                        } catch (IllegalArgumentException e) {
                            // Si no es un valor válido del enum, mantener el valor actual
                            logger.warn("Valor de dificultad inválido: {}", updateActivityDTO.getDifficultyLevel());
                        }
                    }
                    
                    activity.setMaxCapacity(updateActivityDTO.getMaxCapacity());
                    activity.setIsAvailable(updateActivityDTO.getIsAvailable()); // isAvailable en lugar de active
                    
                    Activity savedActivity = activityRepository.save(activity);
                    logger.info("Actividad actualizada exitosamente: {}", savedActivity.getName());
                    
                    return savedActivity;
                });
    }

    /**
     * Desactiva una actividad (eliminación lógica).
     * 
     * @param id ID de la actividad a desactivar
     * @return true si la actividad fue desactivada, false si no se encontró
     */
    public boolean deactivateActivity(Long id) {
        logger.info("Desactivando actividad con ID: {}", id);
        
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setIsAvailable(false); // isAvailable en lugar de active
                    activityRepository.save(activity);
                    
                    logger.info("Actividad desactivada exitosamente con ID: {}", id);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Reactiva una actividad previamente desactivada.
     * 
     * @param id ID de la actividad a reactivar
     * @return true si la actividad fue reactivada, false si no se encontró
     */
    public boolean reactivateActivity(Long id) {
        logger.info("Reactivando actividad con ID: {}", id);
        
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setIsAvailable(true); // isAvailable en lugar de active
                    activityRepository.save(activity);
                    
                    logger.info("Actividad reactivada exitosamente con ID: {}", id);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Obtiene actividades por destino para administradores (incluyendo inactivas).
     * 
     * @param destinationId ID del destino
     * @param pageable Configuración de paginación
     * @return Page de actividades del destino
     */
    @Transactional(readOnly = true)
    public Page<Activity> findActivitiesByDestinationForAdmin(Long destinationId, Pageable pageable) {
        logger.debug("Obteniendo actividades para destino ID: {} (admin)", destinationId);
        return activityRepository.findByDestinationId(destinationId, pageable);
    }

    /**
     * Asocia una actividad a un destino.
     * 
     * @param activityId ID de la actividad
     * @param destinationId ID del destino
     * @return true si la asociación fue exitosa
     */
    public boolean associateActivityToDestination(Long activityId, Long destinationId) {
        logger.info("Asociando actividad {} al destino {}", activityId, destinationId);
        
        Optional<Activity> activityOpt = activityRepository.findById(activityId);
        Optional<Destination> destinationOpt = destinationRepository.findById(destinationId);
        
        if (activityOpt.isPresent() && destinationOpt.isPresent()) {
            Activity activity = activityOpt.get();
            Destination destination = destinationOpt.get();
            
            activity.setDestination(destination);
            activityRepository.save(activity);
            
            logger.info("Actividad {} asociada exitosamente al destino {}", activityId, destinationId);
            return true;
        }
        
        logger.warn("No se pudo asociar actividad {} al destino {} - uno de los recursos no existe", 
                   activityId, destinationId);
        return false;
    }

    /**
     * Cuenta el total de actividades activas.
     * 
     * @return Número total de actividades activas
     */
    @Transactional(readOnly = true)
    public long countActiveActivities() {
        return activityRepository.countByIsAvailableTrue(); // isAvailable en lugar de active
    }

    /**
     * Cuenta el total de todas las actividades (incluyendo inactivas).
     * 
     * @return Número total de actividades
     */
    @Transactional(readOnly = true)
    public long countAllActivities() {
        return activityRepository.count();
    }
}
