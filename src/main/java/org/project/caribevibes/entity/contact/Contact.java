package org.project.caribevibes.entity.contact;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje de contacto en el sistema Caribe Vibes.
 * 
 * Esta clase almacena los mensajes enviados por los usuarios a través
 * del formulario de contacto, incluyendo consultas, sugerencias y
 * solicitudes de información.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Entity
@Table(name = "contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Contact {

    /**
     * Identificador único del mensaje de contacto
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la persona que envía el mensaje
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    /**
     * Correo electrónico de contacto
     */
    @Column(nullable = false, length = 120)
    @Email(message = "Debe proporcionar un email válido")
    @NotBlank(message = "El email es requerido")
    private String email;

    /**
     * Asunto del mensaje
     */
    @Column(nullable = false, length = 200)
    @NotBlank(message = "El asunto es requerido")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String subject;

    /**
     * Contenido del mensaje
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "El mensaje es requerido")
    @Size(max = 2000, message = "El mensaje no puede exceder 2000 caracteres")
    private String message;

    /**
     * Fecha y hora de creación del mensaje
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Número de teléfono de contacto (opcional)
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Tipo de consulta
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type")
    @Builder.Default
    private InquiryType inquiryType = InquiryType.GENERAL;

    /**
     * Estado del mensaje de contacto
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ContactStatus status = ContactStatus.NEW;

    /**
     * Fecha de respuesta al mensaje
     */
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    /**
     * ID del usuario administrador que respondió
     */
    @Column(name = "responded_by")
    private Long respondedBy;

    /**
     * Respuesta enviada al usuario
     */
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    /**
     * Indica si el mensaje está activo o ha sido eliminado lógicamente
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Enumeración para los tipos de consulta
     */
    @Getter
    public enum InquiryType {
        /**
         * Consulta general
         */
        GENERAL("Consulta General"),
        
        /**
         * Información sobre reservas
         */
        BOOKING("Información de Reservas"),
        
        /**
         * Información sobre destinos
         */
        DESTINATIONS("Información de Destinos"),
        
        /**
         * Información sobre precios
         */
        PRICING("Información de Precios"),
        
        /**
         * Soporte técnico
         */
        TECHNICAL_SUPPORT("Soporte Técnico"),
        
        /**
         * Queja o reclamo
         */
        COMPLAINT("Queja o Reclamo"),
        
        /**
         * Sugerencia
         */
        SUGGESTION("Sugerencia"),
        
        /**
         * Solicitud de cancelación
         */
        CANCELLATION("Solicitud de Cancelación");

        private final String displayName;

        InquiryType(String displayName) {
            this.displayName = displayName;
        }

    }

    /**
     * Enumeración para los estados del mensaje de contacto
     */
    @Getter
    public enum ContactStatus {
        /**
         * Mensaje nuevo, sin leer
         */
        NEW("Nuevo"),
        
        /**
         * Mensaje leído, pendiente de respuesta
         */
        READ("Leído"),
        
        /**
         * Mensaje en proceso de respuesta
         */
        IN_PROGRESS("En Proceso"),
        
        /**
         * Mensaje respondido
         */
        RESPONDED("Respondido"),
        
        /**
         * Mensaje cerrado
         */
        CLOSED("Cerrado");

        private final String displayName;

        ContactStatus(String displayName) {
            this.displayName = displayName;
        }

    }

    /**
     * Marca el mensaje como leído
     */
    public void markAsRead() {
        if (status == ContactStatus.NEW) {
            status = ContactStatus.READ;
        }
    }

    /**
     * Marca el mensaje como respondido
     * 
     * @param responderId ID del usuario que responde
     * @param response Mensaje de respuesta
     */
    public void markAsResponded(Long responderId, String response) {
        this.respondedBy = responderId;
        this.responseMessage = response;
        this.respondedAt = LocalDateTime.now();
        this.status = ContactStatus.RESPONDED;
    }

    /**
     * Verifica si el mensaje requiere respuesta
     * 
     * @return true si el mensaje está pendiente de respuesta
     */
    public boolean requiresResponse() {
        return status == ContactStatus.NEW || 
               status == ContactStatus.READ || 
               status == ContactStatus.IN_PROGRESS;
    }

    /**
     * Obtiene una representación resumida del mensaje
     * 
     * @return String con el resumen del mensaje
     */
    public String getSummary() {
        String messagePreview = message.length() > 100 ? 
            message.substring(0, 100) + "..." : message;
        return String.format("%s - %s: %s", name, subject, messagePreview);
    }
}
