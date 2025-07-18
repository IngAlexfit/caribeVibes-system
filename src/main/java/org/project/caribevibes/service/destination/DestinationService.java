package org.project.caribevibes.service.destination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.caribevibes.dto.request.CreateDestinationRequestDTO;
import org.project.caribevibes.dto.request.UpdateDestinationRequestDTO;
import org.project.caribevibes.dto.response.DestinationResponseDTO;
import org.project.caribevibes.dto.response.ExperienceResponseDTO;
import org.project.caribevibes.entity.destination.Activity;
import org.project.caribevibes.entity.destination.Country;
import org.project.caribevibes.entity.destination.Destination;
import org.project.caribevibes.entity.destination.Experience;
import org.project.caribevibes.exception.destination.DestinationNotFoundException;
import org.project.caribevibes.exception.ResourceNotFoundException;
import org.project.caribevibes.repository.destination.ActivityRepository;
import org.project.caribevibes.repository.destination.CountryRepository;
import org.project.caribevibes.repository.destination.DestinationRepository;
import org.project.caribevibes.repository.destination.ExperienceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de destinos turísticos en el sistema Caribe Vibes.
 * 
 * Proporciona funcionalidades para consultar, filtrar y obtener información
 * detallada de destinos, experiencias y actividades disponibles.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final ExperienceRepository experienceRepository;
    private final ActivityRepository activityRepository;
    private final CountryRepository countryRepository;

    /**
     * Obtiene todos los destinos disponibles con paginación
     * 
     * @param page Número de página (0-based)
     * @param size Tamaño de página
     * @param sortBy Campo por el cual ordenar
     * @param sortDir Dirección del ordenamiento (asc/desc)
     * @return Página de destinos
     */
    @Transactional(readOnly = true)
    public Page<DestinationResponseDTO> getAllDestinations(int page, int size, String sortBy, String sortDir) {
        log.debug("Obteniendo destinos activos - Página: {}, Tamaño: {}, Orden: {} {}", page, size, sortBy, sortDir);

        // Crear configuración de paginación y ordenamiento
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Obtener solo destinos activos con paginación
        Page<Destination> destinationsPage = destinationRepository.findActiveDestinations(pageable);

        // Convertir a DTOs (el país se carga automáticamente por la relación @ManyToOne)
        return destinationsPage.map(this::convertToDestinationDTO);
    }

    /**
     * Obtiene todos los destinos para administración (incluye activos e inactivos) con paginación
     * 
     * @param page Número de página (0-based)
     * @param size Tamaño de página
     * @param sortBy Campo por el cual ordenar
     * @param sortDir Dirección del ordenamiento (asc/desc)
     * @return Página de destinos
     */
    @Transactional(readOnly = true)
    public Page<DestinationResponseDTO> getAllDestinationsForAdmin(int page, int size, String sortBy, String sortDir) {
        log.debug("Obteniendo destinos para administración - Página: {}, Tamaño: {}, Orden: {} {}", page, size, sortBy, sortDir);

        // Crear configuración de paginación y ordenamiento
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Obtener todos los destinos (activos e inactivos) para administración
        Page<Destination> destinationsPage = destinationRepository.findAll(pageable);

        // Convertir a DTOs (el país se carga automáticamente por la relación @ManyToOne)
        Page<DestinationResponseDTO> result = destinationsPage.map(this::convertToDestinationDTO);
        
        log.debug("Retornando {} destinos de {} total para administración", result.getNumberOfElements(), result.getTotalElements());
        return result;
    }

    /**
     * Obtiene un destino específico por su slug
     * 
     * @param slug Identificador único del destino
     * @return DTO del destino con información detallada
     * @throws DestinationNotFoundException Si el destino no existe o está inactivo
     */
    @Transactional(readOnly = true)
    public DestinationResponseDTO getDestinationBySlug(String slug) {
        log.debug("Obteniendo destino activo por slug: {}", slug);

        Destination destination = destinationRepository.findBySlugAndIsActiveTrue(slug)
            .orElseThrow(() -> {
                log.warn("Destino activo no encontrado con slug: {}", slug);
                return new DestinationNotFoundException("Destino no encontrado: " + slug);
            });

        // Cargar actividades del destino
        List<Activity> activities = activityRepository.findByDestinationIdAndIsAvailableTrue(destination.getId());

        DestinationResponseDTO dto = convertToDestinationDTO(destination);
        // Ensure experiences are loaded and converted if not already handled by convertToDestinationDTO
        if (destination.getExperiences() != null && (dto.getExperiences() == null || dto.getExperiences().isEmpty())) {
            dto.setExperiences(destination.getExperiences().stream()
                .map(this::convertToExperienceDTO) // Assuming Experience entity needs conversion
                .collect(Collectors.toList()));
        }
        dto.setActivities(activities.stream()
            .map(this::convertToActivityDTO) // This was correct
            .collect(Collectors.toList()));

        log.debug("Destino encontrado: {} con {} actividades", destination.getName(), activities.size());
        return dto;
    }

    /**
     * Filtra destinos por criterios específicos
     * 
     * @param experience Tipo de experiencia (opcional)
     * @param maxPrice Precio máximo (opcional)
     * @param searchTerm Término de búsqueda (opcional)
     * @param page Número de página
     * @param size Tamaño de página
     * @return Lista de destinos filtrados
     */
    @Transactional(readOnly = true)
    public Page<DestinationResponseDTO> filterDestinations(String experience, BigDecimal maxPrice, 
                                                          String searchTerm, int page, int size) {
        log.debug("Filtrando destinos - Experiencia: {}, Precio máx: {}, Búsqueda: {}", 
                 experience, maxPrice, searchTerm);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        // Si no hay filtros, retornar todos
        if (experience == null && maxPrice == null && searchTerm == null) {
            return getAllDestinations(page, size, "name", "asc");
        }

        // Usar consulta con múltiples criterios
        List<Destination> destinations = destinationRepository.findByMultipleCriteria(
            experience, maxPrice, searchTerm);

        // Simular paginación manualmente (en una implementación real, 
        // se debería implementar la paginación en la consulta)
        int start = page * size;
        int end = Math.min(start + size, destinations.size());
        List<Destination> pageContent = destinations.subList(start, end);

        // Convertir a DTOs
        List<DestinationResponseDTO> dtos = pageContent.stream()
            .map(this::convertToDestinationDTO)
            .collect(Collectors.toList());

        // Crear página simulada
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, destinations.size());
    }

    /**
     * Obtiene todas las experiencias disponibles
     * 
     * @return Lista de experiencias ordenadas por orden de visualización
     */
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getAllExperiences() {
        log.debug("Obteniendo todas las experiencias");

        List<Experience> experiences = experienceRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        
        return experiences.stream()
            .map(this::convertToExperienceDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene destinos recomendados basados en una experiencia
     * 
     * @param experienceSlug Slug de la experiencia
     * @param limit Número máximo de destinos a retornar
     * @return Lista de destinos recomendados
     */
    @Transactional(readOnly = true)
    public List<DestinationResponseDTO> getRecommendedDestinations(String experienceSlug, int limit) {
        log.debug("Obteniendo destinos recomendados para experiencia: {}, límite: {}", experienceSlug, limit);

        // Verificar que la experiencia existe
        Experience experience = experienceRepository.findBySlug(experienceSlug)
            .orElseThrow(() -> new DestinationNotFoundException("Experiencia no encontrada: " + experienceSlug));

        // Buscar destinos que contengan la experiencia
        // Assuming Destination entity has a List<Experience> and ExperienceRepository can find by slug
        List<Destination> destinations = destinationRepository.findByExperiences_Slug(experienceSlug); // Adjusted to a more likely JPA query method

        // Limitar resultados y convertir a DTOs
        return destinations.stream()
            .limit(limit)
            .map(this::convertToDestinationDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas básicas de destinos
     * 
     * @return Mapa con estadísticas
     */
    @Transactional(readOnly = true)
    public DestinationStatsDTO getDestinationStats() {
        log.debug("Obteniendo estadísticas de destinos");

        long totalDestinations = destinationRepository.count();
        long totalExperiences = experienceRepository.countByIsActiveTrue();
        long totalActivities = activityRepository.count();

        return DestinationStatsDTO.builder()
            .totalDestinations(totalDestinations)
            .totalExperiences(totalExperiences)
            .totalActivities(totalActivities)
            .build();
    }

    /**
     * Busca destinos por texto en nombre o descripción
     * 
     * @param searchTerm Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @return Página de destinos que coinciden con la búsqueda
     */
    @Transactional(readOnly = true)
    public Page<DestinationResponseDTO> searchDestinations(String searchTerm, int page, int size) {
        log.debug("Buscando destinos activos con término: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllDestinations(page, size, "name", "asc");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        List<Destination> destinations = destinationRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchTerm.trim());

        // Simular paginación
        int start = page * size;
        int end = Math.min(start + size, destinations.size());
        List<Destination> pageContent = destinations.subList(start, end);

        List<DestinationResponseDTO> dtos = pageContent.stream()
            .map(this::convertToDestinationDTO)
            .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, destinations.size());
    }

    /**
     * Convierte una entidad Destination a DTO
     * @param destination Entidad destino a convertir
     * @return DTO con la información del destino
     */
    public DestinationResponseDTO convertToDestinationDTO(Destination destination) {
        DestinationResponseDTO.DestinationResponseDTOBuilder builder = DestinationResponseDTO.builder()
            .id(destination.getId())
            .slug(destination.getSlug())
            .name(destination.getName())
            .description(destination.getDescription())
            .longDescription(destination.getLongDescription())
            .imageUrl(destination.getImageUrl())
            .tags(destination.getTags())
            .lowSeasonPrice(destination.getLowSeasonPrice())
            .highSeasonPrice(destination.getHighSeasonPrice())
            .createdAt(destination.getCreatedAt())
            .isActive(destination.getIsActive());

        // Mapear información del país
        if (destination.getCountry() != null) {
            builder.country(destination.getCountry().getCode())
                   .countryName(destination.getCountry().getName());
        }

        if (destination.getExperiences() != null) {
            builder.experiences(destination.getExperiences().stream()
                .map(experienceSlug -> experienceRepository.findBySlug(experienceSlug)
                    .map(this::convertToExperienceDTO)
                    .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
        }

        return builder.build();
    }

    /**
     * Convierte una entidad Experience a DTO
     */
    private ExperienceResponseDTO convertToExperienceDTO(Experience experience) {
        return ExperienceResponseDTO.builder()
            .id(experience.getId())
            .slug(experience.getSlug())
            .name(experience.getName())
            .description(experience.getDescription())
            .iconUrl(experience.getIconUrl())
            .color(experience.getColor())
            .displayOrder(experience.getDisplayOrder())
            .isActive(experience.getIsActive())
            .build();
    }

    /**
     * Convierte un slug de Experience a DTO, obteniendo primero la entidad.
     */
    private ExperienceResponseDTO convertToExperienceDTO(String experienceSlug) {
        Experience experience = experienceRepository.findBySlug(experienceSlug)
            .orElseThrow(() -> {
                log.warn("Experience not found with slug: {} during DTO conversion", experienceSlug);
                // Consider if a different exception or null return is more appropriate here
                // depending on how critical this conversion is.
                return new DestinationNotFoundException("Experience not found: " + experienceSlug);
            });
        return convertToExperienceDTO(experience);
    }

    /**
     * Convierte una entidad Activity a DTO
     */
    private DestinationResponseDTO.ActivityDTO convertToActivityDTO(Activity activity) {
        return DestinationResponseDTO.ActivityDTO.builder()
            .id(activity.getId())
            .name(activity.getName())
            .description(activity.getDescription())
            .duration(activity.getDuration())
            .price(activity.getPrice())
            .difficultyLevel(activity.getDifficultyLevel().name())
            .maxCapacity(activity.getMaxCapacity())
            .isAvailable(activity.getIsAvailable())
            .build();
    }

    /**
     * Obtiene los destinos más populares
     * 
     * @param limit Número máximo de destinos a retornar
     * @return Lista de destinos populares
     */
    @Transactional(readOnly = true)
    public List<DestinationResponseDTO> getPopularDestinations(int limit) {
        log.debug("Obteniendo los {} destinos más populares", limit);

        // Como no hay un criterio de popularidad implementado,
        // simplemente retornamos los primeros 'limit' destinos
        List<Destination> destinations = destinationRepository.findAll(PageRequest.of(0, limit)).getContent();

        return destinations.stream()
            .map(this::convertToDestinationDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene las actividades asociadas a un destino con paginación
     * 
     * @param destinationId ID del destino
     * @param pageable Configuración de paginación
     * @return Página de actividades
     */
    @Transactional(readOnly = true)
    public Page<Activity> getActivitiesByDestination(Long destinationId, Pageable pageable) {
        log.debug("Obteniendo actividades para destino ID: {}", destinationId);

        return activityRepository.findByDestinationId(destinationId, pageable);
    }

    /**
     * Obtiene todas las actividades asociadas a un destino (sin paginación)
     * 
     * @param destinationId ID del destino
     * @return Lista de todas las actividades del destino
     */
    @Transactional(readOnly = true)
    public List<Activity> getAllActivitiesByDestination(Long destinationId) {
        log.debug("Obteniendo todas las actividades para destino ID: {}", destinationId);

        return activityRepository.findByDestinationIdOrderByNameAsc(destinationId);
    }

    /**
     * Obtiene las experiencias asociadas a un destino con paginación
     * 
     * @param destinationId ID del destino
     * @param pageable Configuración de paginación
     * @return Página de experiencias
     */
    @Transactional(readOnly = true)
    public Page<Experience> getExperiencesByDestination(Long destinationId, Pageable pageable) {
        log.debug("Obteniendo experiencias para destino ID: {}", destinationId);

        // Como no tenemos un método directo, obtenemos el destino y sus experiencias
        Destination destination = destinationRepository.findById(destinationId)
            .orElseThrow(() -> new DestinationNotFoundException("Destino no encontrado: " + destinationId));

        // Obtenemos las experiencias por sus slugs
        List<Experience> experiences = new java.util.ArrayList<>();
        if (destination.getExperiences() != null) {
            for (String experienceSlug : destination.getExperiences()) {
                experienceRepository.findBySlug(experienceSlug)
                    .ifPresent(experiences::add);
            }
        }

        // Aplicamos paginación manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), experiences.size());

        return new org.springframework.data.domain.PageImpl<>(
            experiences.subList(start, end), pageable, experiences.size());
    }

    /**
     * Busca una actividad por su ID
     * 
     * @param activityId ID de la actividad
     * @return Actividad encontrada o empty si no existe
     */
    @Transactional(readOnly = true)
    public java.util.Optional<Activity> findActivityById(Long activityId) {
        log.debug("Buscando actividad con ID: {}", activityId);
        return activityRepository.findById(activityId);
    }

    /**
     * Busca una experiencia por su ID
     * 
     * @param experienceId ID de la experiencia
     * @return Experiencia encontrada o empty si no existe
     */
    @Transactional(readOnly = true)
    public java.util.Optional<Experience> findExperienceById(Long experienceId) {
        log.debug("Buscando experiencia con ID: {}", experienceId);
        return experienceRepository.findById(experienceId);
    }

    /**
     * Crea un nuevo destino en el sistema
     * 
     * @param destination Datos del destino a crear
     * @return Destino creado
     */
    @Transactional
    public Destination createDestination(Destination destination) {
        log.info("Creando nuevo destino: {}", destination.getName());

        // Aseguramos que el ID es nulo para crear uno nuevo
        destination.setId(null);

        return destinationRepository.save(destination);
    }

    /**
     * Actualiza un destino existente
     * 
     * @param id ID del destino a actualizar
     * @param destination Datos actualizados
     * @return Destino actualizado o empty si no existe
     */
    @Transactional
    public java.util.Optional<Destination> updateDestination(Long id, Destination destination) {
        log.info("Actualizando destino con ID: {}", id);

        return destinationRepository.findById(id)
            .map(existingDestination -> {
                // Actualizamos solo los campos que no son nulos
                if (destination.getName() != null) {
                    existingDestination.setName(destination.getName());
                }
                if (destination.getSlug() != null) {
                    existingDestination.setSlug(destination.getSlug());
                }
                if (destination.getDescription() != null) {
                    existingDestination.setDescription(destination.getDescription());
                }
                if (destination.getLongDescription() != null) {
                    existingDestination.setLongDescription(destination.getLongDescription());
                }
                if (destination.getImageUrl() != null) {
                    existingDestination.setImageUrl(destination.getImageUrl());
                }
                if (destination.getTags() != null) {
                    existingDestination.setTags(destination.getTags());
                }
                if (destination.getExperiences() != null) {
                    existingDestination.setExperiences(destination.getExperiences());
                }
                if (destination.getLowSeasonPrice() != null) {
                    existingDestination.setLowSeasonPrice(destination.getLowSeasonPrice());
                }
                if (destination.getHighSeasonPrice() != null) {
                    existingDestination.setHighSeasonPrice(destination.getHighSeasonPrice());
                }

                return destinationRepository.save(existingDestination);
            });
    }

    /**
     * Desactiva un destino (soft delete)
     * 
     * @param id ID del destino a desactivar
     * @return true si se desactivó correctamente, false si no existía
     */
    @Transactional
    public boolean deactivateDestination(Long id) {
        log.info("Desactivando destino con ID: {}", id);

        Optional<Destination> destinationOpt = destinationRepository.findById(id);
        if (destinationOpt.isPresent()) {
            Destination destination = destinationOpt.get();
            destination.setIsActive(false);
            destinationRepository.save(destination);
            log.info("Destino desactivado exitosamente: {}", destination.getName());
            return true;
        }
        
        log.warn("No se encontró el destino con ID: {}", id);
        return false;
    }

    /**
     * Cuenta el total de destinos activos en el sistema.
     * 
     * @return Número total de destinos activos
     */
    @Transactional(readOnly = true)
    public long countActiveDestinations() {
        log.debug("Contando destinos activos");
        return destinationRepository.countByIsActiveTrue();
    }

    /**
     * DTO para estadísticas de destinos
     */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class DestinationStatsDTO {
        private Long totalDestinations;
        private Long totalExperiences;
        private Long totalActivities;
    }

    /**
     * Crea un nuevo destino desde un DTO.
     * 
     * @param createDestinationDTO DTO con los datos del destino
     * @return Destino creado
     */
    public Destination createDestinationFromDTO(CreateDestinationRequestDTO createDestinationDTO) {
        log.info("Creando nuevo destino: {}", createDestinationDTO.getName());
        
        // Buscar el país por código
        Country country = countryRepository.findByCode(createDestinationDTO.getCountry())
                .orElseThrow(() -> {
                    log.warn("País no encontrado con código: {}", createDestinationDTO.getCountry());
                    return new DestinationNotFoundException("País no encontrado con código: " + createDestinationDTO.getCountry());
                });
        
        Destination destination = new Destination();
        destination.setName(createDestinationDTO.getName());
        destination.setDescription(createDestinationDTO.getDescription());
        destination.setCountry(country);
        destination.setLocation(createDestinationDTO.getCountry()); // Usar country como location por ahora
        destination.setImageUrl(createDestinationDTO.getImageUrl());
        destination.setIsActive(true); // Asegurar que el destino esté activo por defecto
        
        // Generar slug a partir del nombre
        destination.setSlug(generateSlug(createDestinationDTO.getName()));
        
        Destination savedDestination = destinationRepository.save(destination);
        log.info("Destino creado exitosamente con ID: {}", savedDestination.getId());
        
        return savedDestination;
    }

    /**
     * Actualiza un destino existente desde un DTO.
     * 
     * @param id ID del destino a actualizar
     * @param updateDestinationDTO DTO con los datos actualizados
     * @return Optional con el destino actualizado
     */
    public Optional<Destination> updateDestinationFromDTO(Long id, UpdateDestinationRequestDTO updateDestinationDTO) {
        log.info("Actualizando destino con ID: {}", id);
        
        return destinationRepository.findById(id)
                .map(destination -> {
                    // Buscar o crear el país si cambió
                    if (!destination.getCountry().getCode().equals(updateDestinationDTO.getCountry())) {
                        Country country = countryRepository.findByCode(updateDestinationDTO.getCountry())
                                .orElseThrow(() -> {
                                    log.warn("País no encontrado con código: {}", updateDestinationDTO.getCountry());
                                    return new DestinationNotFoundException("País no encontrado con código: " + updateDestinationDTO.getCountry());
                                });
                        destination.setCountry(country);
                    }
                    
                    destination.setName(updateDestinationDTO.getName());
                    destination.setDescription(updateDestinationDTO.getDescription());
                    destination.setLocation(updateDestinationDTO.getCountry()); // Usar country como location
                    destination.setImageUrl(updateDestinationDTO.getImageUrl());
                    
                    // Actualizar el estado activo si se proporciona
                    if (updateDestinationDTO.getActive() != null) {
                        destination.setIsActive(updateDestinationDTO.getActive());
                    }
                    
                    Destination savedDestination = destinationRepository.save(destination);
                    log.info("Destino actualizado exitosamente: {}", savedDestination.getName());
                    
                    return savedDestination;
                });
    }

    /**
     * Genera un slug a partir del nombre del destino.
     * 
     * @param name Nombre del destino
     * @return Slug generado
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
