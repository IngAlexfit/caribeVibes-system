package org.project.caribevibes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Configuración para servir la aplicación Angular desde Spring Boot.
 * 
 * Esta configuración permite que Spring Boot sirva tanto la API REST
 * como la aplicación Angular desde el mismo servidor, manejando correctamente
 * las rutas de Angular y los recursos estáticos.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura los manejadores de recursos estáticos para servir la aplicación Angular.
     * 
     * @param registry Registro de manejadores de recursos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos de Angular
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new AngularRouteResourceResolver());
    }

    /**
     * Resolver personalizado para manejar las rutas de Angular SPA.
     * 
     * Para rutas que no corresponden a archivos estáticos o APIs,
     * sirve el index.html para que Angular maneje el routing.
     */
    private static class AngularRouteResourceResolver implements ResourceResolver {
        
        private final Resource index = new ClassPathResource("static/index.html");
        private final List<String> handledExtensions = List.of("html", "js", "json", "csv", "css", "png", "svg", "eot", "ttf", "woff", "appcache", "jpg", "jpeg", "gif", "ico");
        private final List<String> ignoredPaths = List.of("api");

        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
            return resolve(requestPath, locations);
        }

        @Override
        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
            Resource resolvedResource = resolve(resourcePath, locations);
            if (resolvedResource == null) {
                return null;
            }
            try {
                return resolvedResource.getURL().toString();
            } catch (IOException e) {
                return resolvedResource.getFilename();
            }
        }

        private Resource resolve(String requestPath, List<? extends Resource> locations) {
            // Si es una ruta de API, no manejarla aquí
            if (isIgnored(requestPath)) {
                return null;
            }
            
            // Si es un archivo estático, intentar resolverlo
            if (isHandled(requestPath)) {
                return locations.stream()
                        .map(loc -> createRelative(loc, requestPath))
                        .filter(resource -> resource != null && resource.exists())
                        .findFirst()
                        .orElse(null);
            }
            
            // Para rutas de Angular SPA, servir index.html
            return index.exists() ? index : null;
        }

        private Resource createRelative(Resource resource, String relativePath) {
            try {
                return resource.createRelative(relativePath);
            } catch (IOException e) {
                return null;
            }
        }

        private boolean isIgnored(String path) {
            return ignoredPaths.stream().anyMatch(path::startsWith);
        }

        private boolean isHandled(String path) {
            String extension = getExtension(path);
            return handledExtensions.stream().anyMatch(ext -> ext.equals(extension));
        }

        private String getExtension(String path) {
            int dotIndex = path.lastIndexOf('.');
            return dotIndex >= 0 ? path.substring(dotIndex + 1) : "";
        }
    }
}
