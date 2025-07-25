package org.project.caribevibes.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para manejar las rutas del frontend Angular.
 * 
 * Este controlador redirige todas las rutas que no son de la API
 * al index.html de Angular, permitiendo que el router de Angular
 * maneje la navegación del lado del cliente.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Controller
public class AngularRoutingController {

    /**
     * Mapea todas las rutas del frontend Angular para que sirvan index.html.
     * Esto permite que Angular maneje la navegación del lado del cliente
     * y evita errores 404 cuando el usuario hace refresh (F5) en rutas específicas.
     * 
     * Excluye:
     * - Rutas de API (/api/**)
     * - Archivos estáticos (assets, favicon, etc.)
     * - Rutas del actuator (/actuator/**)
     * 
     * @return el nombre de la vista index
     */
    @GetMapping(value = {
        "/",
        "/home",
        "/destinations",
        "/destinations/**",
        "/hotels",
        "/hotels/**",
        "/activities",
        "/activities/**",
        "/contact",
        "/bookings/**",
        "/admin/**",
        "/login",
        "/register"
    })
    public String forwardToAngular() {
        return "forward:/index.html";
    }
}
