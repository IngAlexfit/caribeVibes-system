package org.project.caribevibes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para responder mensajes de contacto.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactReplyRequestDTO {

    @NotBlank(message = "El mensaje de respuesta es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener entre 10 y 2000 caracteres")
    private String replyMessage;

    @Size(max = 100, message = "El nombre del administrador no puede superar los 100 caracteres")
    private String adminName;

    private boolean sendCopyToAdmin = false;
}
