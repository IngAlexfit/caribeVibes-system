package org.project.caribevibes.exception;

/**
 * Excepción lanzada cuando un recurso no es encontrado.
 * 
 * Esta excepción se utiliza cuando se intenta acceder a un
 * recurso que no existe en la base de datos.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje de error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con detalles del recurso no encontrado.
     * 
     * @param resourceName Nombre del recurso
     * @param fieldName Nombre del campo
     * @param fieldValue Valor del campo
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
