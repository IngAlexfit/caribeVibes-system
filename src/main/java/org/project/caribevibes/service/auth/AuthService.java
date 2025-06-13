package org.project.caribevibes.service.auth;

import org.project.caribevibes.dto.request.LoginRequestDTO;
import org.project.caribevibes.dto.request.RegisterRequestDTO;
import org.project.caribevibes.dto.response.AuthResponseDTO;
import org.project.caribevibes.entity.user.Role;
import org.project.caribevibes.entity.user.User;
import org.project.caribevibes.exception.BusinessException;
import org.project.caribevibes.repository.user.RoleRepository;
import org.project.caribevibes.repository.user.UserRepository;
import org.project.caribevibes.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servicio de autenticación para el sistema Caribe Vibes.
 * 
 * Maneja las operaciones de registro, login y gestión de tokens JWT,
 * incluyendo validaciones de seguridad y manejo de errores específicos.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param registerRequest Datos de registro del usuario
     * @return Respuesta de autenticación con token JWT
     * @throws BusinessException Si los datos no son válidos o el usuario ya existe
     */
    @Transactional
    public AuthResponseDTO registerUser(RegisterRequestDTO registerRequest) {
        logger.info("Iniciando registro de usuario con email: {}", registerRequest.getEmail());

        // Validar datos de entrada
        validateRegistrationData(registerRequest);

        // Verificar que el usuario no exista
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("Ya existe un usuario con este email");
        }

        // Obtener el rol CLIENT desde la base de datos
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new BusinessException("No se encontró el rol CLIENT en la base de datos"));

        // Crear el nuevo usuario con rol CLIENT por defecto
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setIsActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.getRoles().add(clientRole);

        // Guardar en la base de datos
        User savedUser = userRepository.save(newUser);
        logger.info("Usuario registrado exitosamente: {} (ID: {})", savedUser.getEmail(), savedUser.getId());

        // Generar token JWT
        String token = jwtTokenUtil.generateToken(savedUser);

        // Crear respuesta
        return buildAuthResponse(savedUser, token);
    }

    /**
     * Autentica un usuario en el sistema.
     * 
     * @param loginRequest Credenciales de login
     * @return Respuesta de autenticación con token JWT
     * @throws BadCredentialsException Si las credenciales son inválidas
     */
    @Transactional(readOnly = true)
    public AuthResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        logger.info("Iniciando autenticación para usuario: {}", loginRequest.getEmail());

        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            // Obtener el usuario autenticado
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));
            
            logger.info("Usuario autenticado exitosamente: {} (ID: {})", user.getEmail(), user.getId());

            // Generar token JWT
            String token = jwtTokenUtil.generateToken(user);

            // Crear respuesta
            return buildAuthResponse(user, token);

        } catch (BadCredentialsException e) {
            logger.warn("Credenciales inválidas para usuario: {}", loginRequest.getEmail());
            throw e;
        } catch (Exception e) {
            logger.error("Error durante la autenticación para usuario: {} - Error: {}", 
                     loginRequest.getEmail(), e.getMessage());
            throw new BadCredentialsException("Error durante la autenticación");
        }
    }

    /**
     * Valida un token JWT.
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        logger.debug("Validando token JWT");
        
        try {
            return jwtTokenUtil.validateToken(token);
        } catch (Exception e) {
            logger.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información del usuario a partir del token JWT.
     * 
     * @param token Token JWT
     * @return Información del usuario
     * @throws BusinessException Si el token no es válido
     */
    public Map<String, Object> getUserInfoFromToken(String token) {
        logger.debug("Obteniendo información de usuario desde token");

        try {
            if (!jwtTokenUtil.validateToken(token)) {
                throw new BusinessException("Token inválido");
            }

            String email = jwtTokenUtil.getUsernameFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("roles", user.getRoleNames());
            userInfo.put("active", user.getIsActive());
            return userInfo;

        } catch (Exception e) {
            logger.error("Error obteniendo información de usuario desde token: {}", e.getMessage());
            throw new BusinessException("Error procesando token");
        }
    }

    /**
     * Refresca un token JWT válido.
     * 
     * @param token Token JWT a refrescar
     * @return Nuevo token JWT
     * @throws BusinessException Si el token no puede ser refrescado
     */
    public String refreshToken(String token) {
        logger.debug("Refrescando token JWT");

        try {
            if (!jwtTokenUtil.validateToken(token)) {
                throw new BusinessException("Token inválido para refrescar");
            }

            String email = jwtTokenUtil.getUsernameFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
            return jwtTokenUtil.generateToken(user);

        } catch (Exception e) {
            logger.error("Error refrescando token: {}", e.getMessage());
            throw new BusinessException("No se pudo refrescar el token");
        }
    }

    /**
     * Busca un usuario por su email.
     * 
     * @param email Email del usuario
     * @return Optional con el usuario encontrado
     */
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        logger.debug("Buscando usuario por email: {}", email);
        return userRepository.findByEmailAndIsActiveTrue(email);
    }

    /**
     * Valida los datos de registro.
     */
    private void validateRegistrationData(RegisterRequestDTO request) {
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new BusinessException("El nombre es obligatorio");
        }
        
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio");
        }

        if (request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BusinessException("Email inválido");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Las contraseñas no coinciden");
        }
    }    /**
     * Construye la respuesta de autenticación.
     */
    private AuthResponseDTO buildAuthResponse(User user, String token) {
        // Crear el DTO de información del usuario
        AuthResponseDTO.UserInfoDTO userInfo = AuthResponseDTO.UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .roles(user.getRoleNames())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();

        // Crear la respuesta de autenticación
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userInfo)
                .build();
    }
}
