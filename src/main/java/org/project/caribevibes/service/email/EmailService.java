package org.project.caribevibes.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para el envío de emails a través de SMTP.
 * 
 * Este servicio maneja el envío de emails usando Maileroo como proveedor SMTP,
 * incluyendo respuestas a contactos, notificaciones y confirmaciones.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.email.admin-email}")
    private String adminEmail;

    /**
     * Envía un email simple de texto plano de forma asíncrona.
     * 
     * @param to Destinatario del email
     * @param subject Asunto del email
     * @param text Contenido del email en texto plano
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("Enviando email simple a: {} con asunto: {}", to, subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            
            log.info("Email simple enviado exitosamente. Detalles: [Para: {}] [Desde: {}] [Nombre Remitente: {}] [Admin: {}] [Asunto: {}]", 
                to, fromEmail, fromName, adminEmail, subject);
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("Error al enviar email simple a: {} - Error: {}", to, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Envía un email HTML de forma asíncrona.
     * 
     * @param to Destinatario del email
     * @param subject Asunto del email
     * @param htmlContent Contenido del email en HTML
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Enviando email HTML a: {} con asunto: {}", to, subject);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Email HTML enviado exitosamente a: {}", to);
            return CompletableFuture.completedFuture(true);
            
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error al enviar email HTML a: {} - Error: {}", to, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Responde a un mensaje de contacto con un email personalizado.
     * 
     * @param contactEmail Email del contacto original
     * @param contactName Nombre del contacto
     * @param originalSubject Asunto original del contacto
     * @param replyMessage Mensaje de respuesta del administrador
     * @param adminName Nombre del administrador que responde
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> sendContactReply(
            String contactEmail, 
            String contactName, 
            String originalSubject, 
            String replyMessage,
            String adminName) {
        
        try {
            log.info("Respondiendo a contacto: {} sobre: {}", contactEmail, originalSubject);
            
            String subject = "Re: " + originalSubject + " - Respuesta de Caribe Vibes";
            String htmlContent = buildContactReplyHtml(contactName, replyMessage, adminName, originalSubject);
            
            return sendHtmlEmail(contactEmail, subject, htmlContent);
            
        } catch (Exception e) {
            log.error("Error al responder contacto a: {} - Error: {}", contactEmail, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Envía confirmación de recepción de mensaje de contacto.
     * 
     * @param contactEmail Email del contacto
     * @param contactName Nombre del contacto
     * @param subject Asunto del mensaje original
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> sendContactConfirmation(
            String contactEmail, 
            String contactName, 
            String subject) {
        
        try {
            log.info("Enviando confirmación de contacto a: {}", contactEmail);
            
            String confirmationSubject = "Confirmación: Hemos recibido tu mensaje - " + subject;
            String htmlContent = buildContactConfirmationHtml(contactName, subject);
            
            return sendHtmlEmail(contactEmail, confirmationSubject, htmlContent);
            
        } catch (Exception e) {
            log.error("Error al enviar confirmación de contacto a: {} - Error: {}", contactEmail, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Notifica al administrador sobre un nuevo mensaje de contacto.
     * 
     * @param contactName Nombre del contacto
     * @param contactEmail Email del contacto
     * @param subject Asunto del mensaje
     * @param message Contenido del mensaje
     * @param contactId ID del contacto en la base de datos
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> notifyAdminNewContact(
            String contactName,
            String contactEmail,
            String subject,
            String message,
            Long contactId) {
        
        try {
            log.info("Notificando al admin sobre nuevo contacto de: {}", contactEmail);
            
            String adminSubject = "🔔 Nuevo mensaje de contacto: " + subject;
            String htmlContent = buildAdminNotificationHtml(contactName, contactEmail, subject, message, contactId);
            
            return sendHtmlEmail(adminEmail, adminSubject, htmlContent);
            
        } catch (Exception e) {
            log.error("Error al notificar admin sobre contacto - Error: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Construye el contenido HTML para respuesta a contacto.
     */
    private String buildContactReplyHtml(String contactName, String replyMessage, String adminName, String originalSubject) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Respuesta - Caribe Vibes</title>
                <style>
                    .email-container { max-width: 600px; margin: 0 auto; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
                    .header { background: linear-gradient(135deg, #0077be, #4fb3d9); color: white; padding: 30px; text-align: center; }
                    .logo { font-size: 28px; font-weight: bold; margin-bottom: 10px; }
                    .content { padding: 30px; background: #fff; }
                    .message-box { background: #f8f9fa; border-left: 4px solid #0077be; padding: 20px; margin: 20px 0; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; }
                    .btn { display: inline-block; background: #ff6b35; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="logo">🏝️ Caribe Vibes</div>
                        <p>Tu Puerta al Paraíso Caribeño</p>
                    </div>
                    
                    <div class="content">
                        <h2>Hola %s,</h2>
                        
                        <p>Gracias por contactarnos. Hemos revisado tu consulta sobre "<strong>%s</strong>" y queremos responderte:</p>
                        
                        <div class="message-box">
                            <h3>📧 Respuesta de nuestro equipo:</h3>
                            <p>%s</p>
                        </div>
                        
                        <p>Esperamos que esta información te sea útil. Si tienes más preguntas, no dudes en contactarnos nuevamente.</p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="https://caribevibes.com/contact" class="btn">Contactar Nuevamente</a>
                        </div>
                        
                        <p><strong>Atentamente,</strong><br>
                        %s<br>
                        <em>Equipo de Soporte - Caribe Vibes</em></p>
                    </div>
                    
                    <div class="footer">
                        <p>🌴 <strong>Caribe Vibes</strong> - Creando experiencias inolvidables en el Caribe</p>
                        <p>📧 soporte@caribevibes.com | 📞 +57 (301) 234-5678 | 🌐 www.caribevibes.com</p>
                        <p><small>Este es un email automático, por favor no responder directamente.</small></p>
                    </div>
                </div>
            </body>
            </html>
            """, contactName, originalSubject, replyMessage.replace("\n", "<br>"), adminName);
    }

    /**
     * Construye el contenido HTML para confirmación de contacto.
     */
    private String buildContactConfirmationHtml(String contactName, String subject) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Mensaje Recibido - Caribe Vibes</title>
                <style>
                    .email-container { max-width: 600px; margin: 0 auto; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
                    .header { background: linear-gradient(135deg, #0077be, #4fb3d9); color: white; padding: 30px; text-align: center; }
                    .logo { font-size: 28px; font-weight: bold; margin-bottom: 10px; }
                    .content { padding: 30px; background: #fff; }
                    .info-box { background: #e3f2fd; border-radius: 8px; padding: 20px; margin: 20px 0; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="logo">🏝️ Caribe Vibes</div>
                        <p>Tu Puerta al Paraíso Caribeño</p>
                    </div>
                    
                    <div class="content">
                        <h2>¡Hola %s!</h2>
                        
                        <p>✅ <strong>Hemos recibido tu mensaje exitosamente.</strong></p>
                        
                        <div class="info-box">
                            <h3>📝 Detalles de tu consulta:</h3>
                            <p><strong>Asunto:</strong> %s</p>
                            <p><strong>Fecha:</strong> %s</p>
                        </div>
                        
                        <p>Nuestro equipo de expertos en el Caribe revisará tu consulta y te responderemos en un <strong>plazo máximo de 24 horas</strong>.</p>
                        
                        <p>Mientras tanto, te invitamos a explorar nuestros increíbles destinos y experiencias en nuestra página web.</p>
                        
                        <p><strong>¡Gracias por elegir Caribe Vibes!</strong><br>
                        <em>Equipo de Atención al Cliente</em></p>
                    </div>
                    
                    <div class="footer">
                        <p>🌴 <strong>Caribe Vibes</strong> - Creando experiencias inolvidables en el Caribe</p>
                        <p>📧 soporte@caribevibes.com | 📞 +57 (301) 234-5678 | 🌐 www.caribevibes.com</p>
                    </div>
                </div>
            </body>
            </html>
            """, contactName, subject, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    /**
     * Construye el contenido HTML para notificación a admin.
     */
    private String buildAdminNotificationHtml(String contactName, String contactEmail, String subject, String message, Long contactId) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Nuevo Contacto - Admin Panel</title>
                <style>
                    .email-container { max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif; }
                    .header { background: #dc3545; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #fff; }
                    .contact-info { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 6px; padding: 15px; margin: 15px 0; }
                    .message-content { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 15px; margin: 15px 0; }
                    .action-btn { display: inline-block; background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin: 10px 5px; }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <h2>🔔 Nuevo Mensaje de Contacto</h2>
                        <p>Panel de Administración - Caribe Vibes</p>
                    </div>
                    
                    <div class="content">
                        <h3>📋 Información del Contacto:</h3>
                        
                        <div class="contact-info">
                            <p><strong>👤 Nombre:</strong> %s</p>
                            <p><strong>📧 Email:</strong> %s</p>
                            <p><strong>📝 Asunto:</strong> %s</p>
                            <p><strong>🆔 ID Contacto:</strong> #%d</p>
                            <p><strong>📅 Fecha:</strong> %s</p>
                        </div>
                        
                        <h3>💬 Mensaje:</h3>
                        <div class="message-content">
                            <p>%s</p>
                        </div>
                        
                        <div style="text-align: center; margin: 20px 0;">
                            <a href="http://localhost:4200/admin/contacts?id=%d" class="action-btn">📧 Responder Ahora</a>
                            <a href="http://localhost:4200/admin/contacts" class="action-btn">📋 Ver Todos los Contactos</a>
                        </div>
                        
                        <p><small><em>Este es un email de notificación automática del sistema.</em></small></p>
                    </div>
                </div>
            </body>
            </html>
            """, contactName, contactEmail, subject, contactId, 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            message.replace("\n", "<br>"), contactId);
    }

    /**
     * Envía una notificación simple al administrador.
     * 
     * @param subject Asunto del email
     * @param message Contenido del mensaje
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> sendAdminNotification(String subject, String message) {
        try {
            log.info("Enviando notificación al admin: {}", subject);
            
            return sendSimpleEmail(adminEmail, subject, message);
            
        } catch (Exception e) {
            log.error("Error al enviar notificación al admin - Error: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Envía un email de prueba para verificar la configuración SMTP (síncrono).
     * 
     * @param testEmail Email de destino para la prueba
     * @throws RuntimeException si hay error al enviar el email
     */
    public void sendTestEmail(String testEmail) {
        try {
            log.info("Enviando email de prueba a: {}", testEmail);
            
            String subject = "✅ Test Email - Caribe Vibes SMTP Configuration";
            String content = """
                ¡Hola!
                
                Este es un email de prueba para verificar que la configuración SMTP de Caribe Vibes está funcionando correctamente.
                
                ✅ Configuración SMTP: OK
                ✅ Autenticación: OK  
                ✅ Envío de emails: OK
                
                Si recibes este mensaje, significa que el sistema de emails está funcionando perfectamente.
                
                Saludos,
                Sistema Caribe Vibes
                """;
            
            // Usar el método síncrono para propagar errores correctamente
            sendEmail(testEmail, subject, content);
            
            log.info("Email de prueba enviado exitosamente a: {}", testEmail);
            
        } catch (Exception e) {
            log.error("Error al enviar email de prueba a: {} - Error: {}", testEmail, e.getMessage(), e);
            throw new RuntimeException("Error al enviar email de prueba: " + e.getMessage(), e);
        }
    }

    /**
     * Envía un email de prueba de forma asíncrona (para uso interno del servicio).
     * 
     * @param testEmail Email de destino para la prueba
     * @return CompletableFuture<Boolean> true si se envió exitosamente
     */
    @Async
    public CompletableFuture<Boolean> sendTestEmailAsync(String testEmail) {
        try {
            log.info("Enviando email de prueba asíncrono a: {}", testEmail);
            
            String subject = "✅ Test Email - Caribe Vibes SMTP Configuration";
            String content = """
                ¡Hola!
                
                Este es un email de prueba para verificar que la configuración SMTP de Caribe Vibes está funcionando correctamente.
                
                ✅ Configuración SMTP: OK
                ✅ Autenticación: OK  
                ✅ Envío de emails: OK
                
                Si recibes este mensaje, significa que el sistema de emails está funcionando perfectamente.
                
                Saludos,
                Sistema Caribe Vibes
                """;
            
            // Usar directamente sendSimpleEmail (que ya es asíncrono)
            return sendSimpleEmail(testEmail, subject, content);
            
        } catch (Exception e) {
            log.error("Error al enviar email de prueba async - Error: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Envía un email simple de forma síncrona (para uso en controladores de prueba).
     * 
     * @param to Destinatario del email
     * @param subject Asunto del email
     * @param text Contenido del email
     * @throws RuntimeException si hay error al enviar el email
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            log.info("Enviando email síncrono a: {} con asunto: {}", to, subject);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            
            log.info("Email síncrono enviado exitosamente a: {}", to);
            
        } catch (Exception e) {
            log.error("Error al enviar email síncrono a: {} - Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error al enviar email: " + e.getMessage(), e);
        }
    }
}
