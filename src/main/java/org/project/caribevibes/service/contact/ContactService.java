package org.project.caribevibes.service.contact;

import org.project.caribevibes.entity.contact.Contact;
import org.project.caribevibes.repository.contact.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de mensajes de contacto.
 * 
 * Este servicio proporciona funcionalidades para:
 * - Gestión completa de mensajes de contacto (CRUD)
 * - Cambio de estado de mensajes (nuevo, leído, respondido)
 * - Búsqueda y filtrado de mensajes
 * - Estadísticas de mensajes
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
@Service
@Transactional
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Obtiene todos los mensajes de contacto activos paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de mensajes de contacto activos
     */
    @Transactional(readOnly = true)
    public Page<Contact> findAllActiveContacts(Pageable pageable) {
        logger.debug("Obteniendo todos los mensajes de contacto activos - página: {}, tamaño: {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findAllByActiveTrue(pageable);
    }

    /**
     * Obtiene los mensajes de contacto por estado.
     * 
     *
     * @param pageable Configuración de paginación
     * @return Página de mensajes con el estado especificado
     */
    @Transactional(readOnly = true)
    public Page<Contact> findContactsByStatus(String statusStr, Pageable pageable) {
        logger.debug("Obteniendo mensajes de contacto con estado: {} - página: {}, tamaño: {}", 
                    statusStr, pageable.getPageNumber(), pageable.getPageSize());
        Contact.ContactStatus status = Contact.ContactStatus.valueOf(statusStr);
        return contactRepository.findByStatusAndActiveTrue(status, pageable);
    }

    /**
     * Obtiene los mensajes de contacto en un rango de fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param pageable Configuración de paginación
     * @return Página de mensajes en el rango de fechas
     */
    @Transactional(readOnly = true)
    public Page<Contact> findContactsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        logger.debug("Obteniendo mensajes de contacto entre {} y {} - página: {}, tamaño: {}", 
                    startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findByCreatedAtBetweenAndActiveTrue(startDate, endDate, pageable);
    }

    /**
     * Busca mensajes de contacto por email del remitente.
     * 
     * @param email Email del remitente
     * @param pageable Configuración de paginación
     * @return Página de mensajes del email especificado
     */
    @Transactional(readOnly = true)
    public Page<Contact> findContactsByEmail(String email, Pageable pageable) {
        logger.debug("Obteniendo mensajes de contacto para email: {} - página: {}, tamaño: {}", 
                    email, pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findByEmailAndActiveTrue(email, pageable);
    }

    /**
     * Busca mensajes de contacto por asunto (búsqueda difusa).
     * 
     * @param subject Asunto o parte del asunto
     * @param pageable Configuración de paginación
     * @return Página de mensajes que coinciden con el asunto
     */
    @Transactional(readOnly = true)
    public Page<Contact> findContactsBySubject(String subject, Pageable pageable) {
        logger.debug("Buscando mensajes de contacto por asunto: '{}' - página: {}, tamaño: {}", 
                    subject, pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findBySubjectContainingIgnoreCaseAndActiveTrue(subject, pageable);
    }

    /**
     * Obtiene un mensaje de contacto por su ID.
     * 
     * @param id ID del mensaje de contacto
     * @return Mensaje de contacto encontrado o Optional vacío
     */
    @Transactional(readOnly = true)
    public Optional<Contact> findContactById(Long id) {
        logger.debug("Buscando mensaje de contacto por ID: {}", id);
        return contactRepository.findByIdAndActiveTrue(id);
    }

    /**
     * Crea un nuevo mensaje de contacto.
     * 
     * @param contact Mensaje de contacto a crear
     * @return Mensaje de contacto creado
     */
    public Contact createContact(Contact contact) {
        logger.info("Creando nuevo mensaje de contacto de: {} con asunto: {}", 
                   contact.getEmail(), contact.getSubject());

        // Establecer valores por defecto
        contact.setCreatedAt(LocalDateTime.now());
        contact.setStatus(Contact.ContactStatus.NEW);
        contact.setActive(true);

        Contact savedContact = contactRepository.save(contact);
        logger.info("Mensaje de contacto creado exitosamente con ID: {}", savedContact.getId());
        
        return savedContact;
    }

    /**
     * Actualiza un mensaje de contacto existente.
     * 
     * @param id ID del mensaje de contacto a actualizar
     * @param contactDetails Detalles actualizados del mensaje
     * @return Mensaje de contacto actualizado o Optional vacío si no existe
     */
    public Optional<Contact> updateContact(Long id, Contact contactDetails) {
        logger.info("Actualizando mensaje de contacto con ID: {}", id);
        
        return contactRepository.findByIdAndActiveTrue(id)
                .map(existingContact -> {
                    existingContact.setName(contactDetails.getName());
                    existingContact.setEmail(contactDetails.getEmail());
                    existingContact.setPhoneNumber(contactDetails.getPhoneNumber());
                    existingContact.setSubject(contactDetails.getSubject());
                    existingContact.setMessage(contactDetails.getMessage());
                    existingContact.setResponseMessage(contactDetails.getResponseMessage());
                    existingContact.setStatus(contactDetails.getStatus());
                    
                    Contact updatedContact = contactRepository.save(existingContact);
                    logger.info("Mensaje de contacto actualizado exitosamente de: {}", updatedContact.getEmail());
                    return updatedContact;
                });
    }

    /**
     * Marca un mensaje de contacto como leído.
     * 
     * @param id ID del mensaje de contacto
     * @return true si se marcó como leído exitosamente, false si no existe
     */
    public boolean markAsRead(Long id) {
        logger.info("Marcando mensaje de contacto con ID: {} como leído", id);
        
        return contactRepository.findByIdAndActiveTrue(id)
                .map(contact -> {
                    if (Contact.ContactStatus.NEW.equals(contact.getStatus())) {
                        contact.setStatus(Contact.ContactStatus.READ);
                        contactRepository.save(contact);
                        logger.info("Mensaje de contacto marcado como leído exitosamente");
                        return true;
                    } else {
                        logger.debug("El mensaje ya tenía estado: {}", contact.getStatus());
                        return true; // Ya estaba leído o respondido
                    }
                })
                .orElse(false);
    }

    /**
     * Marca un mensaje de contacto como respondido y guarda la respuesta.
     * 
     * @param id ID del mensaje de contacto
     * @param response Respuesta del mensaje
     * @return true si se marcó como respondido exitosamente, false si no existe
     */
    public boolean markAsReplied(Long id, String response) {
        logger.info("Marcando mensaje de contacto con ID: {} como respondido", id);
        
        return contactRepository.findByIdAndActiveTrue(id)
                .map(contact -> {
                    contact.setStatus(Contact.ContactStatus.RESPONDED);
                    contact.setResponseMessage(response);
                    contact.setRespondedAt(LocalDateTime.now());
                    contactRepository.save(contact);
                    logger.info("Mensaje de contacto marcado como respondido exitosamente");
                    return true;
                })
                .orElse(false);
    }

    /**
     * Desactiva un mensaje de contacto (eliminación lógica).
     * 
     * @param id ID del mensaje de contacto a desactivar
     * @return true si se desactivó exitosamente, false si no existe
     */
    public boolean deactivateContact(Long id) {
        logger.info("Desactivando mensaje de contacto con ID: {}", id);
        
        return contactRepository.findByIdAndActiveTrue(id)
                .map(contact -> {
                    contact.setActive(false);
                    contactRepository.save(contact);
                    logger.info("Mensaje de contacto desactivado exitosamente de: {}", contact.getEmail());
                    return true;
                })
                .orElse(false);
    }

    /**
     * Obtiene los mensajes de contacto nuevos (no leídos).
     * 
     * @param pageable Configuración de paginación
     * @return Página de mensajes nuevos
     */
    @Transactional(readOnly = true)
    public Page<Contact> findNewContacts(Pageable pageable) {
        logger.debug("Obteniendo mensajes de contacto nuevos - página: {}, tamaño: {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findByStatusAndActiveTrue(Contact.ContactStatus.NEW, pageable);
    }

    /**
     * Obtiene la cantidad de mensajes de contacto por estado.
     * 
     * @return Lista con el conteo de mensajes por estado
     */
    @Transactional(readOnly = true)
    public List<Object[]> getContactCountByStatus() {
        logger.debug("Obteniendo conteo de mensajes de contacto por estado");

        List<Object[]> stats = new ArrayList<>();
        for (Contact.ContactStatus status : Contact.ContactStatus.values()) {
            long count = contactRepository.countByStatusAndActiveTrue(status);
            stats.add(new Object[]{status, count});
        }
        return stats;
    }

    /**
     * Obtiene los mensajes de contacto recientes (últimos 7 días).
     * 
     * @param pageable Configuración de paginación
     * @return Página de mensajes recientes
     */
    @Transactional(readOnly = true)
    public Page<Contact> findRecentContacts(Pageable pageable) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        logger.debug("Obteniendo mensajes de contacto recientes (desde {}) - página: {}, tamaño: {}", 
                    weekAgo, pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findByCreatedAtAfterAndActiveTrue(weekAgo, pageable);
    }

    /**
     * Búsqueda global de mensajes de contacto por término.
     * 
     * @param searchTerm Término de búsqueda (busca en nombre, email, asunto y mensaje)
     * @param pageable Configuración de paginación
     * @return Página de mensajes que coinciden con el término
     */
    @Transactional(readOnly = true)
    public Page<Contact> searchContacts(String searchTerm, Pageable pageable) {
        logger.debug("Buscando mensajes de contacto por término: '{}' - página: {}, tamaño: {}", 
                    searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.searchContacts(searchTerm, pageable);
    }

    /**
     * Obtiene estadísticas básicas de mensajes de contacto.
     * 
     * @return Array con [total, nuevos, leídos, respondidos]
     */
    @Transactional(readOnly = true)
    public long[] getContactStatistics() {
        logger.debug("Obteniendo estadísticas de mensajes de contacto");
        
        long total = contactRepository.countByActiveTrue();
        long newMessages = contactRepository.countByStatusAndActiveTrue(Contact.ContactStatus.NEW);
        long readMessages = contactRepository.countByStatusAndActiveTrue(Contact.ContactStatus.READ);
        long repliedMessages = contactRepository.countByStatusAndActiveTrue(Contact.ContactStatus.RESPONDED);
        
        logger.debug("Estadísticas - Total: {}, Nuevos: {}, Leídos: {}, Respondidos: {}", 
                    total, newMessages, readMessages, repliedMessages);
        
        return new long[]{total, newMessages, readMessages, repliedMessages};
    }

    /**
     * Obtiene los mensajes de contacto pendientes de respuesta.
     * 
     * @param pageable Configuración de paginación
     * @return Página de mensajes pendientes de respuesta
     */
    @Transactional(readOnly = true)
    public Page<Contact> findPendingContacts(Pageable pageable) {
        logger.debug("Obteniendo mensajes de contacto pendientes de respuesta - página: {}, tamaño: {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        return contactRepository.findByStatusInAndActiveTrue(List.of(Contact.ContactStatus.NEW, Contact.ContactStatus.READ), pageable);
    }

    /**
     * Responde automáticamente a un mensaje de contacto con un template.
     * 
     * @param id ID del mensaje de contacto
     * @param templateType Tipo de template a usar
     * @return true si se respondió exitosamente, false si no existe
     */
    public boolean sendAutoReply(Long id, String templateType) {
        logger.info("Enviando respuesta automática para mensaje de contacto con ID: {} usando template: {}", 
                   id, templateType);
        
        String autoResponse = generateAutoResponse(templateType);
        return markAsReplied(id, autoResponse);
    }

    /**
     * Genera una respuesta automática basada en el tipo de template.
     * 
     * @param templateType Tipo de template
     * @return Respuesta automática generada
     */
    private String generateAutoResponse(String templateType) {
        switch (templateType.toUpperCase()) {
            case "GENERAL":
                return "Gracias por contactar con Caribe Vibes. Hemos recibido tu mensaje y te responderemos en un plazo máximo de 24 horas. ¡Esperamos poder ayudarte a planificar unas vacaciones inolvidables!";
            case "BOOKING":
                return "Gracias por tu consulta sobre reservas. Un especialista en destinos del Caribe se pondrá en contacto contigo muy pronto para ayudarte con tu reserva. Mientras tanto, puedes explorar nuestros destinos en nuestra página web.";
            case "SUPPORT":
                return "Hemos recibido tu solicitud de soporte. Nuestro equipo técnico revisará tu consulta y te proporcionará una solución lo antes posible. Te agradecemos tu paciencia.";
            default:
                return "Gracias por contactarnos. Hemos recibido tu mensaje y te responderemos pronto. ¡Saludos desde Caribe Vibes!";
        }
    }

    /**
     * Cuenta los mensajes por estado
     * @param status Estado del mensaje
     * @return Número de mensajes con el estado especificado
     */
    public long countContactsByStatus(Contact.ContactStatus status) {
        logger.debug("Contando mensajes con estado: {}", status);
        return contactRepository.countByStatusAndActiveTrue(status);
    }
}
