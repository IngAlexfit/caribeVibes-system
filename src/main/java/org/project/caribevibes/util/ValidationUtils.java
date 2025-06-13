package org.project.caribevibes.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utilidades para validación de datos en el sistema Caribe Vibes.
 * 
 * Esta clase proporciona métodos estáticos para validar diferentes
 * tipos de datos como emails, teléfonos, fechas, etc.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class ValidationUtils {

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-\\(\\)]{7,20}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"
    );

    /**
     * Valida si un email tiene formato válido.
     * 
     * @param email Email a validar
     * @return true si el email es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida si un teléfono tiene formato válido.
     * 
     * @param phone Teléfono a validar
     * @return true si el teléfono es válido, false en caso contrario
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // El teléfono es opcional
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Valida si una contraseña cumple los criterios de seguridad.
     * Criterios: mínimo 8 caracteres, al menos una mayúscula, 
     * una minúscula y un número.
     * 
     * @param password Contraseña a validar
     * @return true si la contraseña es válida, false en caso contrario
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Valida si una contraseña simple cumple criterios básicos.
     * Criterios: mínimo 6 caracteres.
     * 
     * @param password Contraseña a validar
     * @return true si la contraseña es válida, false en caso contrario
     */
    public static boolean isValidSimplePassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Valida si un nombre tiene formato válido.
     * 
     * @param name Nombre a validar
     * @return true si el nombre es válido, false en caso contrario
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && trimmedName.length() <= 50 && 
               trimmedName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }

    /**
     * Valida si una fecha de nacimiento es válida.
     * 
     * @param birthDate Fecha de nacimiento
     * @return true si la fecha es válida, false en caso contrario
     */
    public static boolean isValidBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(120); // Máximo 120 años
        LocalDate maxDate = now.minusYears(13);  // Mínimo 13 años
        
        return birthDate.isAfter(minDate) && birthDate.isBefore(maxDate);
    }

    /**
     * Valida si una fecha está en el futuro.
     * 
     * @param date Fecha a validar
     * @return true si la fecha es futura, false en caso contrario
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Valida si una fecha y hora está en el futuro.
     * 
     * @param dateTime Fecha y hora a validar
     * @return true si es futura, false en caso contrario
     */
    public static boolean isFutureDateTime(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Valida si un rango de fechas es válido.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si el rango es válido, false en caso contrario
     */
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return startDate.isBefore(endDate) || startDate.isEqual(endDate);
    }

    /**
     * Valida si un texto no está vacío y tiene longitud válida.
     * 
     * @param text Texto a validar
     * @param minLength Longitud mínima
     * @param maxLength Longitud máxima
     * @return true si el texto es válido, false en caso contrario
     */
    public static boolean isValidText(String text, int minLength, int maxLength) {
        if (text == null) {
            return false;
        }
        String trimmedText = text.trim();
        return trimmedText.length() >= minLength && trimmedText.length() <= maxLength;
    }

    /**
     * Valida si un número está dentro de un rango.
     * 
     * @param number Número a validar
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return true si está en el rango, false en caso contrario
     */
    public static boolean isInRange(Number number, double min, double max) {
        if (number == null) {
            return false;
        }
        double value = number.doubleValue();
        return value >= min && value <= max;
    }

    /**
     * Normaliza un email eliminando espacios y convirtiendo a minúsculas.
     * 
     * @param email Email a normalizar
     * @return Email normalizado
     */
    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    /**
     * Normaliza un nombre eliminando espacios extra y capitalizando.
     * 
     * @param name Nombre a normalizar
     * @return Nombre normalizado
     */
    public static String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim().replaceAll("\\s+", " ");
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        
        StringBuilder normalized = new StringBuilder();
        String[] words = trimmed.split(" ");
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                normalized.append(" ");
            }
            String word = words[i].toLowerCase();
            if (!word.isEmpty()) {
                normalized.append(Character.toUpperCase(word.charAt(0)))
                          .append(word.substring(1));
            }
        }
        
        return normalized.toString();
    }

    /**
     * Valida y parsea una fecha desde string.
     * 
     * @param dateString Fecha en formato string
     * @param pattern Patrón de fecha esperado
     * @return LocalDate parseado o null si es inválido
     */
    public static LocalDate parseDate(String dateString, String pattern) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateString.trim(), formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Valida si un string representa un número válido.
     * 
     * @param numberString String a validar
     * @return true si es un número válido, false en caso contrario
     */
    public static boolean isValidNumber(String numberString) {
        if (numberString == null || numberString.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(numberString.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida si un ID es válido (mayor que 0).
     * 
     * @param id ID a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * Limpia y valida una URL.
     * 
     * @param url URL a validar
     * @return URL limpia o null si es inválida
     */
    public static String cleanUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        
        String cleanedUrl = url.trim();
        if (!cleanedUrl.startsWith("http://") && !cleanedUrl.startsWith("https://")) {
            cleanedUrl = "https://" + cleanedUrl;
        }
        
        return cleanedUrl;
    }
}
