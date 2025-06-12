package org.project.caribevibes.repository.contact;

import org.project.caribevibes.entity.contact.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de mensajes de contacto en el sistema Caribe Vibes.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas
 * sobre la entidad Contact, incluyendo filtros por estado, tipo de consulta
 * y fechas de creación.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2024
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Obtiene mensajes de contacto por dirección de email
     * 
     * @param email Dirección de correo electrónico
     * @return Lista de mensajes del email especificado
     */
    List<Contact> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * Obtiene mensajes de contacto por estado
     * 
     * @param status Estado del mensaje
     * @return Lista de mensajes con el estado especificado
     */
    List<Contact> findByStatusOrderByCreatedAtDesc(Contact.ContactStatus status);

    /**
     * Obtiene mensajes de contacto por tipo de consulta
     * 
     * @param inquiryType Tipo de consulta
     * @return Lista de mensajes del tipo especificado
     */
    List<Contact> findByInquiryTypeOrderByCreatedAtDesc(Contact.InquiryType inquiryType);

    /**
     * Obtiene mensajes nuevos (sin leer)
     * 
     * @return Lista de mensajes nuevos ordenados por fecha de creación descendente
     */
    List<Contact> findByStatusOrderByCreatedAtAsc(Contact.ContactStatus status);

    /**
     * Obtiene mensajes que requieren respuesta
     * 
     * @return Lista de mensajes pendientes de respuesta
     */
    @Query("SELECT c FROM Contact c WHERE c.status IN ('NEW', 'READ', 'IN_PROGRESS') " +
           "ORDER BY c.createdAt ASC")
    List<Contact> findPendingMessages();

    /**
     * Busca mensajes por nombre del remitente
     * 
     * @param name Nombre o parte del nombre del remitente
     * @return Lista de mensajes que coinciden con el nombre
     */
    List<Contact> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

    /**
     * Busca mensajes por asunto
     * 
     * @param subject Asunto o parte del asunto
     * @return Lista de mensajes que coinciden con el asunto
     */
    List<Contact> findBySubjectContainingIgnoreCaseOrderByCreatedAtDesc(String subject);

    /**
     * Busca mensajes por contenido
     * 
     * @param searchTerm Término de búsqueda en el mensaje
     * @return Lista de mensajes que contienen el término
     */
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(c.message) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY c.createdAt DESC")
    List<Contact> findByMessageContaining(@Param("searchTerm") String searchTerm);

    /**
     * Obtiene todos los mensajes activos con paginación
     */
    Page<Contact> findAllByActiveTrue(Pageable pageable);

    /**
     * Obtiene mensajes por estado y activos con paginación
     */
    Page<Contact> findByStatusAndActiveTrue(Contact.ContactStatus status, Pageable pageable);

    /**
     * Obtiene mensajes por rango de fechas y activos con paginación
     */
    Page<Contact> findByCreatedAtBetweenAndActiveTrue(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Obtiene mensajes por email y activos con paginación
     */
    Page<Contact> findByEmailAndActiveTrue(String email, Pageable pageable);

    /**
     * Obtiene mensajes por asunto y activos con paginación
     */
    Page<Contact> findBySubjectContainingIgnoreCaseAndActiveTrue(String subject, Pageable pageable);

    /**
     * Obtiene un mensaje por ID y activo
     */
    Optional<Contact> findByIdAndActiveTrue(Long id);

    /**
     * Obtiene mensajes creados después de una fecha y activos con paginación
     */
    Page<Contact> findByCreatedAtAfterAndActiveTrue(LocalDateTime date, Pageable pageable);

    /**
     * Obtiene mensajes por varios estados y activos con paginación
     */
    Page<Contact> findByStatusInAndActiveTrue(List<Contact.ContactStatus> statuses, Pageable pageable);

    /**
     * Cuenta mensajes activos
     */
    long countByActiveTrue();

    /**
     * Cuenta mensajes por estado y activos
     */
    long countByStatusAndActiveTrue(Contact.ContactStatus status);

    /**
     * Busca mensajes por término en varios campos con paginación
     */
    @Query("SELECT c FROM Contact c WHERE c.active = true AND (" +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY c.createdAt DESC")
    Page<Contact> searchContacts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Obtiene mensajes creados en un rango de fechas
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de mensajes creados en el rango
     */
    List<Contact> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Obtiene mensajes respondidos por un usuario específico
     * 
     * @param responderId ID del usuario que respondió
     * @return Lista de mensajes respondidos por el usuario
     */
    List<Contact> findByRespondedByOrderByRespondedAtDesc(Long responderId);

    /**
     * Busca mensajes por múltiples criterios
     * 
     * @param status Estado del mensaje (puede ser null)
     * @param inquiryType Tipo de consulta (puede ser null)
     * @param email Email del remitente (puede ser null)
     * @param startDate Fecha de inicio del rango (puede ser null)
     * @param endDate Fecha de fin del rango (puede ser null)
     * @return Lista de mensajes que cumplen los criterios
     */
    @Query("SELECT c FROM Contact c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:inquiryType IS NULL OR c.inquiryType = :inquiryType) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate) " +
           "ORDER BY c.createdAt DESC")
    List<Contact> findByMultipleCriteria(@Param("status") Contact.ContactStatus status,
                                       @Param("inquiryType") Contact.InquiryType inquiryType,
                                       @Param("email") String email,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Cuenta mensajes por estado
     * 
     * @param status Estado del mensaje
     * @return Número de mensajes con el estado especificado
     */
    long countByStatus(Contact.ContactStatus status);

    /**
     * Cuenta mensajes por tipo de consulta
     * 
     * @param inquiryType Tipo de consulta
     * @return Número de mensajes del tipo especificado
     */
    long countByInquiryType(Contact.InquiryType inquiryType);

    /**
     * Obtiene estadísticas de mensajes por día en un período
     * 
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Lista con [fecha, cantidad] de mensajes por día
     */
    @Query("SELECT DATE(c.createdAt), COUNT(c) FROM Contact c " +
           "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(c.createdAt) " +
           "ORDER BY DATE(c.createdAt)")
    List<Object[]> getMessageStatisticsByDay(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Obtiene los mensajes más recientes
     * 
     * @param limit Número máximo de mensajes a retornar
     * @return Lista de mensajes más recientes
     */
    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC")
    List<Contact> findRecentMessages(@Param("limit") int limit);

    /**
     * Obtiene tiempo promedio de respuesta
     * 
     * @return Tiempo promedio de respuesta en horas
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, c.createdAt, c.respondedAt)) " +
           "FROM Contact c WHERE c.respondedAt IS NOT NULL")
    Double getAverageResponseTime();

    /**
     * Obtiene mensajes sin responder por más de X horas
     * 
     * @param thresholdDate Fecha límite calculada en Java (ahora menos X horas)
     * @return Lista de mensajes sin responder en el tiempo especificado
     */
    @Query("SELECT c FROM Contact c WHERE c.status IN ('NEW', 'READ', 'IN_PROGRESS') " +
           "AND c.createdAt < :thresholdDate " +
           "ORDER BY c.createdAt ASC")
    List<Contact> findOverdueMessages(@Param("thresholdDate") LocalDateTime thresholdDate);

    /**
     * Busca mensajes por texto completo (nombre, email, asunto o mensaje)
     * 
     * @param searchTerm Término de búsqueda
     * @return Lista de mensajes que contienen el término
     */
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.message) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY c.createdAt DESC")
    List<Contact> searchMessages(@Param("searchTerm") String searchTerm);
}
