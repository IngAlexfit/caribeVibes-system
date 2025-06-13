package org.project.caribevibes.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para la gestión de tokens JWT en el sistema Caribe Vibes.
 * 
 * Esta clase proporciona métodos para generar, validar y extraer información
 * de los tokens JWT utilizados para la autenticación de usuarios.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Component
@Slf4j
public class JwtTokenUtil {

    /**
     * Clave secreta para firmar los tokens JWT
     */
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    /**
     * Tiempo de expiración del token en milisegundos
     */
    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Emisor del token
     */
    private static final String JWT_ISSUER = "caribe-vibes-api";

    /**
     * Obtiene la clave secreta para firmar tokens
     * 
     * @return Clave secreta
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Extrae el nombre de usuario del token JWT
     * 
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extrae el ID del usuario del token JWT
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extrae la fecha de expiración del token JWT
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token JWT
     * 
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            log.error("Firma del token JWT inválida: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Token JWT vacío: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si el token JWT ha expirado
     * 
     * @param token Token JWT
     * @return true si el token ha expirado
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Genera un token JWT para un usuario
     * 
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Agregar claims adicionales si el usuario implementa nuestra interfaz
        if (userDetails instanceof org.project.caribevibes.entity.user.User user) {
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());
            claims.put("firstName", user.getFirstName());
            claims.put("lastName", user.getLastName());
            claims.put("roles", user.getRoleNames()); // ahora es una lista de roles
        }
        
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Genera un token JWT utilizando solo el email del usuario
     * 
     * @param email Email del usuario
     * @return Token JWT generado
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    /**
     * Genera un token JWT con claims personalizados
     * 
     * @param extraClaims Claims adicionales
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea un token JWT con los claims especificados
     * 
     * @param claims Claims del token
     * @param subject Sujeto del token (username)
     * @return Token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(JWT_ISSUER)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Valida un token JWT contra los detalles del usuario
     * 
     * @param token Token JWT
     * @param userDetails Detalles del usuario
     * @return true si el token es válido
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Error validando token JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida un token JWT sin verificar contra un usuario específico
     * 
     * @param token Token JWT
     * @return true si el token es válido
     */
    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el tiempo restante antes de que expire el token
     * 
     * @param token Token JWT
     * @return Milisegundos hasta la expiración
     */
    public Long getTimeUntilExpiration(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * Obtiene la fecha de expiración como LocalDateTime
     * 
     * @param token Token JWT
     * @return Fecha de expiración como LocalDateTime
     */
    public LocalDateTime getExpirationAsLocalDateTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return Instant.ofEpochMilli(expiration.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Verifica si el token puede ser renovado (no ha expirado)
     * 
     * @param token Token JWT
     * @return true si el token puede ser renovado
     */
    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Renueva un token JWT existente
     * 
     * @param token Token JWT actual
     * @return Nuevo token JWT
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Map<String, Object> claimsMap = new HashMap<>(claims);

            return Jwts.builder()
                    .claims(claimsMap)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error renovando token JWT: {}", e.getMessage());
            throw new RuntimeException("No se pudo renovar el token", e);
        }
    }

    /**
     * Extrae el token de la cabecera Authorization
     * 
     * @param authHeader Cabecera Authorization
     * @return Token JWT sin el prefijo "Bearer "
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
