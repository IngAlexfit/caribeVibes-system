package org.project.caribevibes.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de detalles de usuario para Spring Security en el sistema Caribe Vibes.
 * 
 * Esta clase implementa UserDetailsService para cargar información de usuario
 * durante el proceso de autenticación, permitiendo que Spring Security
 * valide credenciales y establezca el contexto de seguridad.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario (email) para autenticación
     * 
     * @param username Nombre de usuario (email) del usuario a cargar
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario para autenticación: {}", username);

        // Buscar usuario por email (que es el username en nuestro sistema)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException(
                        String.format("Usuario no encontrado con email: %s", username)
                    );
                });

        // Verificar que el usuario esté activo
        if (!user.getIsActive()) {
            log.warn("Intento de acceso con usuario inactivo: {}", username);
            throw new UsernameNotFoundException(
                String.format("Usuario %s está inactivo", username)
            );
        }

        log.debug("Usuario cargado exitosamente: {} (ID: {})", username, user.getId());

        // Retornar el usuario (que implementa UserDetails)
        return user;
    }

    /**
     * Carga un usuario por su ID
     * 
     * @param userId ID del usuario a cargar
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Cargando usuario por ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con ID: {}", userId);
                    return new UsernameNotFoundException(
                        String.format("Usuario no encontrado con ID: %d", userId)
                    );
                });

        // Verificar que el usuario esté activo
        if (!user.getIsActive()) {
            log.warn("Intento de acceso con usuario inactivo (ID: {})", userId);
            throw new UsernameNotFoundException(
                String.format("Usuario con ID %d está inactivo", userId)
            );
        }

        log.debug("Usuario cargado exitosamente por ID: {} (Email: {})", userId, user.getEmail());

        return user;
    }

    /**
     * Verifica si existe un usuario con el email especificado
     * 
     * @param email Email a verificar
     * @return true si el usuario existe y está activo
     */
    @Transactional(readOnly = true)
    public boolean userExistsAndIsActive(String email) {
        log.debug("Verificando existencia de usuario: {}", email);
        
        return userRepository.findByEmail(email)
                .map(User::getIsActive)
                .orElse(false);
    }

    /**
     * Obtiene información básica del usuario sin cargar toda la entidad
     * 
     * @param email Email del usuario
     * @return Información básica del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(String email) throws UsernameNotFoundException {
        log.debug("Obteniendo información de usuario: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado para obtener información: {}", email);
                    return new UsernameNotFoundException(
                        String.format("Usuario no encontrado con email: %s", email)
                    );
                });

        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(user.getRoleNames())
                .isActive(user.getIsActive())
                .build();
    }

    /**
     * Clase interna para información básica de usuario
     */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String username;
        private String fullName;
        private List<String> roles;
        private Boolean isActive;
    }
}
