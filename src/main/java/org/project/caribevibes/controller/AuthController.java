package org.project.caribevibes.controller;

import org.project.caribevibes.dto.request.LoginRequestDTO;
import org.project.caribevibes.dto.request.RegisterRequestDTO;
import org.project.caribevibes.dto.response.AuthResponseDTO;
import org.project.caribevibes.service.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Controlador REST para operaciones de autenticación.
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * la autenticación y autorización de usuarios, incluyendo registro,
 * login y validación de tokens JWT.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param registerRequest Datos del usuario a registrar
     * @return ResponseEntity con los datos del usuario registrado y token JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        logger.info("Intentando registrar nuevo usuario con email: {}", registerRequest.getEmail());
        
        try {
            AuthResponseDTO response = authService.registerUser(registerRequest);
            logger.info("Usuario registrado exitosamente: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error al registrar usuario {}: {}", registerRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param loginRequest Credenciales del usuario
     * @return ResponseEntity con los datos del usuario autenticado y token JWT
     */    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("Intento de login para usuario: {}", loginRequest.getEmail());
        
        try {
            AuthResponseDTO response = authService.authenticateUser(loginRequest);
            logger.info("Usuario autenticado exitosamente: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Fallo de autenticación para usuario {}: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * Valida un token JWT.
     * 
     * @param token Token JWT a validar
     * @return ResponseEntity con el resultado de la validación
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        logger.debug("Validando token JWT");
        
        try {
            boolean isValid = authService.validateToken(token);
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "Token válido" : "Token inválido"
            );
            
            logger.debug("Resultado de validación de token: {}", isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al validar token: {}", e.getMessage());
            Map<String, Object> response = Map.of(
                "valid", false,
                "message", "Error al validar token"
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtiene información del usuario a partir del token JWT.
     * 
     * @param token Token JWT
     * @return ResponseEntity con la información del usuario
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserFromToken(@RequestParam String token) {
        logger.debug("Obteniendo información de usuario desde token");
        
        try {
            Map<String, Object> userInfo = authService.getUserInfoFromToken(token);
            logger.debug("Información de usuario obtenida exitosamente");
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            logger.error("Error al obtener información de usuario desde token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Refresca un token JWT válido.
     * 
     * @param token Token JWT a refrescar
     * @return ResponseEntity con el nuevo token
     */    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestParam String token) {
        logger.debug("Refrescando token JWT");
        
        try {
            String newToken = authService.refreshToken(token);
            Map<String, String> response = Map.of(
                "token", newToken,
                "message", "Token refrescado exitosamente"
            );
            
            logger.debug("Token refrescado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al refrescar token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Endpoint de salud para verificar que el servicio de autenticación está funcionando.
     * 
     * @return ResponseEntity con el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = Map.of(
            "status", "OK",
            "service", "Auth Service",
            "message", "Servicio de autenticación funcionando correctamente"
        );
        return ResponseEntity.ok(response);
    }
}
