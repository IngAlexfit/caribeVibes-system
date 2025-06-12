package org.project.caribevibes.entity.user;

/**
 * Enumeración que define los roles disponibles para los usuarios del sistema.
 * 
 * Define los diferentes niveles de acceso y permisos que pueden tener
 * los usuarios en la aplicación Caribe Vibes.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
public enum UserRole {
    
    /**
     * Usuario regular del sistema con permisos básicos
     * Puede realizar reservas, ver destinos y gestionar su perfil
     */
    USER,
    
    /**
     * Administrador del sistema con permisos completos
     * Puede gestionar destinos, hoteles, usuarios y todas las operaciones
     */
    ADMIN,
    
    /**
     * Operador de turismo con permisos intermedios
     * Puede gestionar reservas y asistir a los usuarios
     */
    OPERATOR
}
