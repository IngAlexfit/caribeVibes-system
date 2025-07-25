package org.project.caribevibes.controller;

import org.project.caribevibes.dto.request.ContactReplyRequestDTO;
import org.project.caribevibes.dto.request.ContactRequestDTO;
import org.project.caribevibes.entity.contact.Contact;
import org.project.caribevibes.service.contact.ContactService;
import org.project.caribevibes.service.email.EmailService;
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
 * Controlador REST para operaciones de mensajes de contacto.
 * 
 * Este controlador maneja todas las operaciones relacionadas con
 * la gestión de mensajes de contacto y soporte al cliente.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private EmailService emailService;

    /**
     * Crea un nuevo mensaje de contacto (público).
     * 
     * @param contactRequest Datos del mensaje de contacto
     * @return ResponseEntity con confirmación del mensaje enviado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createContact(@Valid @RequestBody ContactRequestDTO contactRequest) {
        logger.info("Nuevo mensaje de contacto de: {} con asunto: {}", 
                   contactRequest.getEmail(), contactRequest.getSubject());
        
        // Crear entidad Contact
        Contact contact = new Contact();
        contact.setName(contactRequest.getName());
        contact.setEmail(contactRequest.getEmail());
        contact.setPhoneNumber(contactRequest.getPhone()); // Corregido: usando getPhone()
        contact.setSubject(contactRequest.getSubject());
        contact.setMessage(contactRequest.getMessage());
        
        Contact createdContact = contactService.createContact(contact);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Tu mensaje ha sido enviado exitosamente. Te responderemos pronto.",
            "contactId", createdContact.getId(),
            "status", "NEW"
        );
        
        logger.info("Mensaje de contacto creado exitosamente con ID: {}", createdContact.getId());
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Obtiene todos los mensajes de contacto (solo para administradores).
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenamiento (default: createdAt)
     * @param sortDir Dirección de ordenamiento (default: desc)
     * @return ResponseEntity con página de mensajes de contacto
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Contact>> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Obteniendo mensajes de contacto - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Contact> contacts = contactService.findAllActiveContacts(pageable);
        
        logger.debug("Retornando {} mensajes de {} total", contacts.getNumberOfElements(), contacts.getTotalElements());
        return ResponseEntity.ok(contacts);
    }

    /**
     * Obtiene un mensaje de contacto específico por su ID (solo para administradores).
     * 
     * @param id ID del mensaje de contacto
     * @return ResponseEntity con los detalles del mensaje
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        logger.debug("Obteniendo mensaje de contacto por ID: {}", id);
        
        Contact contact = contactService.findContactById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje de contacto", "id", id));
        
        // Marcar como leído automáticamente
        contactService.markAsRead(id);
        
        logger.debug("Mensaje de contacto encontrado de: {}", contact.getEmail());
        return ResponseEntity.ok(contact);
    }

    /**
     * Obtiene mensajes de contacto por estado (solo para administradores).
     * 
     * @param status Estado de los mensajes (NEW, READ, REPLIED)
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de mensajes del estado especificado
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Contact>> getContactsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo mensajes de contacto por estado: {} - página: {}, tamaño: {}", 
                    status, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contacts = contactService.findContactsByStatus(status, pageable);
        
        logger.debug("Encontrados {} mensajes con estado: {}", contacts.getNumberOfElements(), status);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Obtiene mensajes de contacto nuevos (no leídos) (solo para administradores).
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de mensajes nuevos
     */
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Contact>> getNewContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo mensajes de contacto nuevos - página: {}, tamaño: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contacts = contactService.findNewContacts(pageable);
        
        logger.debug("Encontrados {} mensajes nuevos", contacts.getNumberOfElements());
        return ResponseEntity.ok(contacts);
    }

    /**
     * Obtiene mensajes de contacto recientes (últimos 7 días) (solo para administradores).
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de mensajes recientes
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Contact>> getRecentContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Obteniendo mensajes de contacto recientes - página: {}, tamaño: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contacts = contactService.findRecentContacts(pageable);
        
        logger.debug("Encontrados {} mensajes recientes", contacts.getNumberOfElements());
        return ResponseEntity.ok(contacts);
    }

    /**
     * Busca mensajes de contacto por término (solo para administradores).
     * 
     * @param searchTerm Término de búsqueda
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @return ResponseEntity con página de mensajes que coinciden con el término
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Contact>> searchContacts(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Buscando mensajes de contacto por término: '{}' - página: {}, tamaño: {}", 
                    searchTerm, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contact> contacts = contactService.searchContacts(searchTerm, pageable);
        
        logger.debug("Encontrados {} mensajes con término: '{}'", contacts.getNumberOfElements(), searchTerm);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Responde a un mensaje de contacto (solo para administradores).
     * 
     * @param id ID del mensaje de contacto
     * @param response Respuesta al mensaje
     * @return ResponseEntity con confirmación de respuesta
     */
    @PutMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> replyToContact(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String response = request.get("response");
        logger.info("Respondiendo a mensaje de contacto con ID: {}", id);
        
        boolean replied = contactService.markAsReplied(id, response);
        if (!replied) {
            throw new ResourceNotFoundException("Mensaje de contacto", "id", id);
        }
        
        Map<String, String> responseMap = Map.of(
            "message", "Respuesta enviada exitosamente",
            "status", "REPLIED"
        );
        
        logger.info("Respuesta enviada exitosamente para mensaje ID: {}", id);
        return ResponseEntity.ok(responseMap);
    }

    /**
     * Responde a un mensaje de contacto enviando un email (solo para administradores).
     * 
     * @param id ID del mensaje de contacto
     * @param replyRequest DTO con la información de la respuesta
     * @return ResponseEntity con confirmación de respuesta
     */
    @PutMapping("/{id}/send-reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendReplyToContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactReplyRequestDTO replyRequest) {
        
        logger.info("Enviando respuesta por email a mensaje de contacto con ID: {}", id);
        
        try {
            boolean replied = contactService.replyToContact(id, replyRequest);
            if (!replied) {
                throw new ResourceNotFoundException("Mensaje de contacto", "id", id);
            }
            
            Map<String, Object> response = Map.of(
                "message", "Respuesta enviada exitosamente por email",
                "status", "REPLIED",
                "emailSent", true,
                "adminCopySent", replyRequest.isSendCopyToAdmin()
            );
            
            logger.info("Respuesta por email enviada exitosamente para mensaje ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error al enviar respuesta por email para mensaje ID {}: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = Map.of(
                "message", "Error al enviar la respuesta por email",
                "error", e.getMessage(),
                "status", "ERROR",
                "emailSent", false
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Marca un mensaje como leído (solo para administradores).
     * 
     * @param id ID del mensaje de contacto
     * @return ResponseEntity con confirmación
     */
    @PutMapping("/{id}/mark-read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        logger.info("Marcando mensaje de contacto con ID: {} como leído", id);
        
        boolean marked = contactService.markAsRead(id);
        if (!marked) {
            throw new ResourceNotFoundException("Mensaje de contacto", "id", id);
        }
        
        Map<String, String> response = Map.of(
            "message", "Mensaje marcado como leído",
            "status", "read"
        );
        
        logger.info("Mensaje marcado como leído exitosamente con ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Envía una respuesta automática (solo para administradores).
     * 
     * @param id ID del mensaje de contacto
     * @param templateType Tipo de template para la respuesta automática
     * @return ResponseEntity con confirmación
     */
    @PutMapping("/{id}/auto-reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> sendAutoReply(
            @PathVariable Long id,
            @RequestParam(defaultValue = "GENERAL") String templateType) {
        
        logger.info("Enviando respuesta automática para mensaje ID: {} con template: {}", id, templateType);
        
        boolean replied = contactService.sendAutoReply(id, templateType);
        if (!replied) {
            throw new ResourceNotFoundException("Mensaje de contacto", "id", id);
        }
        
        Map<String, String> response = Map.of(
            "message", "Respuesta automática enviada exitosamente",
            "status", "REPLIED",
            "template", templateType
        );
        
        logger.info("Respuesta automática enviada exitosamente para mensaje ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene estadísticas de mensajes de contacto (solo para administradores).
     * 
     * @return ResponseEntity con estadísticas de mensajes
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getContactStatistics() {
        logger.debug("Obteniendo estadísticas de mensajes de contacto");
        
        long[] stats = contactService.getContactStatistics();
        List<Object[]> countByStatus = contactService.getContactCountByStatus();
        
        Map<String, Object> statistics = Map.of(
            "total", stats[0],
            "new", stats[1],
            "read", stats[2],
            "replied", stats[3],
            "countByStatus", countByStatus
        );
        
        logger.debug("Estadísticas obtenidas - Total: {}, Nuevos: {}, Leídos: {}, Respondidos: {}", 
                    stats[0], stats[1], stats[2], stats[3]);
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * Elimina un mensaje de contacto (solo para administradores).
     * 
     * @param id ID del mensaje de contacto a eliminar
     * @return ResponseEntity con confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateContact(@PathVariable Long id) {
        logger.info("Desactivando mensaje de contacto con ID: {}", id);
        
        boolean deactivated = contactService.deactivateContact(id);
        if (!deactivated) {
            throw new ResourceNotFoundException("Mensaje de contacto", "id", id);
        }
        
        Map<String, String> response = Map.of(
            "message", "Mensaje de contacto eliminado exitosamente"
        );
        
        logger.info("Mensaje de contacto desactivado exitosamente con ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de salud para verificar que el servicio de contacto está funcionando.
     * 
     * @return ResponseEntity con el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = Map.of(
            "status", "OK",
            "service", "Contact Service",
            "message", "Servicio de contacto funcionando correctamente"
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Prueba la configuración SMTP enviando un email de prueba de forma asíncrona.
     * 
     * @param testRequest Datos para el email de prueba
     * @return ResponseEntity con confirmación inmediata del envío
     */
    @PostMapping("/test-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testEmailConfiguration(
            @RequestBody Map<String, String> testRequest) {
        
        String testEmail = testRequest.get("email");
        logger.info("Iniciando prueba de configuración SMTP para email: {}", testEmail);
        
        try {
            // Enviar email de forma asíncrona para respuesta inmediata
            emailService.sendTestEmailAsync(testEmail);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "✅ Email de prueba enviado en segundo plano. Revisa tu bandeja en unos segundos.",
                "testEmail", testEmail,
                "timestamp", System.currentTimeMillis(),
                "note", "El envío es asíncrono - revisa los logs para confirmar el resultado"
            );
            
            logger.info("Prueba de email iniciada exitosamente para: {}", testEmail);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al iniciar prueba de configuración SMTP: {}", e.getMessage(), e);
            
            // Determinar el tipo específico de error
            String errorMessage;
            String errorType;
            
            if (e.getMessage().contains("Authentication failed") || e.getMessage().contains("535")) {
                errorMessage = "❌ Error de autenticación SMTP. Verifica tus credenciales de Maileroo";
                errorType = "AUTHENTICATION_ERROR";
            } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("connect")) {
                errorMessage = "❌ No se pudo conectar al servidor SMTP. Verifica tu conexión a internet";
                errorType = "CONNECTION_ERROR";
            } else if (e.getMessage().contains("timeout")) {
                errorMessage = "❌ Timeout en la conexión SMTP. El servidor tardó demasiado en responder";
                errorType = "TIMEOUT_ERROR";
            } else {
                errorMessage = "❌ Error SMTP: " + e.getMessage();
                errorType = "GENERAL_ERROR";
            }
            
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", errorMessage,
                "errorType", errorType,
                "originalError", e.getMessage(),
                "testEmail", testEmail,
                "timestamp", System.currentTimeMillis(),
                "solution", errorType.equals("AUTHENTICATION_ERROR") ? 
                    "Verifica las credenciales en application.yml" :
                    "Verifica tu conexión y configuración SMTP"
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint de diagnóstico para verificar la configuración SMTP actual.
     * 
     * @return ResponseEntity con la configuración SMTP actual (sin passwords)
     */
    @GetMapping("/smtp-config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSmtpConfiguration() {
        logger.info("Obteniendo configuración SMTP actual");
        
        try {
            // Verificar variables de entorno directamente
            String envMailUsername = System.getenv("MAIL_USERNAME");
            String envMailPassword = System.getenv("MAIL_PASSWORD");
            String envEmailFrom = System.getenv("EMAIL_FROM");
            String envAdminEmail = System.getenv("ADMIN_EMAIL");
            
            Map<String, Object> config = Map.of(
                "smtp", Map.of(
                    "host", "smtp.maileroo.com",
                    "port", 587,
                    "username", envMailUsername != null ? envMailUsername : "NOT_SET",
                    "hasPassword", envMailPassword != null && !envMailPassword.isEmpty()
                ),
                "email", Map.of(
                    "from", envEmailFrom != null ? envEmailFrom : "noreply@caribevibes.com (DEFAULT)",
                    "adminEmail", envAdminEmail != null ? envAdminEmail : "admin@caribevibes.com (DEFAULT)"
                ),
                "environment", Map.of(
                    "MAIL_USERNAME", envMailUsername != null ? envMailUsername : "❌ NOT_SET",
                    "MAIL_PASSWORD", envMailPassword != null ? "✅ SET" : "❌ NOT_SET",
                    "EMAIL_FROM", envEmailFrom != null ? envEmailFrom : "❌ NOT_SET (usando default)",
                    "ADMIN_EMAIL", envAdminEmail != null ? envAdminEmail : "❌ NOT_SET (usando default)"
                ),
                "recommendation", "Si EMAIL_FROM no está configurado, usa el mismo email que MAIL_USERNAME",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(config);
            
        } catch (Exception e) {
            logger.error("Error al obtener configuración SMTP: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Error al obtener configuración SMTP",
                "error", e.getMessage()
            );
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
