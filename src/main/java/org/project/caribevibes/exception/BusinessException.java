package org.project.caribevibes.exception;

/**
 * Excepción lanzada cuando una operación de negocio no es válida.
 * 
 * Esta excepción se utiliza para indicar que una operación
 * solicitada viola las reglas de negocio de la aplicación.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2024
 */
public class BusinessException extends RuntimeException {

    private String errorCode;

    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message Mensaje de error
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y código de error.
     * 
     * @param message Mensaje de error
     * @param errorCode Código de error
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
