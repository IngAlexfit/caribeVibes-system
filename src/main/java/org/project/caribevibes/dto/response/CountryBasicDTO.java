package org.project.caribevibes.dto.response;

/**
 * DTO de respuesta básico para información de países.
 * 
 * Este DTO contiene información esencial de un país
 * para ser usado en respuestas anidadas.
 * 
 * @author Caribe Vibes Team
 * @version 1.0
 * @since 2025
 */
public class CountryBasicDTO {

    private Long id;
    private String name;
    private String code;
    private String continent;
    private String currency;
    private String phonePrefix;

    /**
     * Constructor por defecto.
     */
    public CountryBasicDTO() {}

    /**
     * Constructor completo.
     * 
     * @param id Identificador único del país
     * @param name Nombre del país
     * @param code Código ISO del país
     * @param continent Continente del país
     * @param currency Moneda del país
     * @param phonePrefix Prefijo telefónico del país
     */
    public CountryBasicDTO(Long id, String name, String code, String continent, String currency, String phonePrefix) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.continent = continent;
        this.currency = currency;
        this.phonePrefix = phonePrefix;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    @Override
    public String toString() {
        return "CountryBasicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", continent='" + continent + '\'' +
                '}';
    }
}
