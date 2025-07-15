-- Migración para agregar tabla de reseñas de hoteles
-- Autor: Sistema Caribe Vibes
-- Fecha: 2024-12-11
-- Descripción: Creación de tabla para reseñas y ratings de hoteles

-- Tabla de reseñas de hoteles
CREATE TABLE hotel_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(100) NOT NULL,
    comment TEXT NOT NULL,
    check_in_date DATE,
    check_out_date DATE,
    guest_name VARCHAR(200),
    review_date TIMESTAMP,
    is_editable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    
    -- Un usuario solo puede hacer una reseña por reserva
    UNIQUE KEY unique_booking_review (booking_id),
    
    INDEX idx_hotel_reviews_hotel (hotel_id),
    INDEX idx_hotel_reviews_user (user_id),
    INDEX idx_hotel_reviews_rating (rating),
    INDEX idx_hotel_reviews_created (created_at)
);

-- Trigger para actualizar el rating promedio del hotel cuando se inserta una reseña
DELIMITER $$
CREATE TRIGGER update_hotel_rating_after_insert
AFTER INSERT ON hotel_reviews
FOR EACH ROW
BEGIN
    UPDATE hotels 
    SET rating = (
        SELECT ROUND(AVG(rating), 2) 
        FROM hotel_reviews 
        WHERE hotel_id = NEW.hotel_id
    )
    WHERE id = NEW.hotel_id;
END$$
DELIMITER ;

-- Trigger para actualizar el rating promedio del hotel cuando se actualiza una reseña
DELIMITER $$
CREATE TRIGGER update_hotel_rating_after_update
AFTER UPDATE ON hotel_reviews
FOR EACH ROW
BEGIN
    UPDATE hotels 
    SET rating = (
        SELECT ROUND(AVG(rating), 2) 
        FROM hotel_reviews 
        WHERE hotel_id = NEW.hotel_id
    )
    WHERE id = NEW.hotel_id;
END$$
DELIMITER ;

-- Trigger para actualizar el rating promedio del hotel cuando se elimina una reseña
DELIMITER $$
CREATE TRIGGER update_hotel_rating_after_delete
AFTER DELETE ON hotel_reviews
FOR EACH ROW
BEGIN
    UPDATE hotels 
    SET rating = (
        SELECT ROUND(AVG(rating), 2) 
        FROM hotel_reviews 
        WHERE hotel_id = OLD.hotel_id
    )
    WHERE id = OLD.hotel_id;
END$$
DELIMITER ;
