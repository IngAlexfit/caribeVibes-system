-- Migración inicial para crear las tablas del sistema Caribe Vibes
-- Autor: Sistema Caribe Vibes
-- Fecha: 2024-12-11
-- Descripción: Creación de todas las tablas principales del sistema

-- Tabla de usuarios
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(80),
    last_name VARCHAR(80),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    preferences JSON,
    role ENUM('USER', 'ADMIN', 'OPERATOR') DEFAULT 'USER',
    
    INDEX idx_users_email (email),
    INDEX idx_users_username (username)
);

-- Tabla de experiencias
CREATE TABLE experiences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slug VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    color VARCHAR(7),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_experiences_slug (slug)
);

-- Tabla de destinos
CREATE TABLE destinations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    long_description TEXT,
    image_url VARCHAR(500),
    tags JSON,
    experiences JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    low_season_price DECIMAL(10,2),
    high_season_price DECIMAL(10,2),
    
    INDEX idx_destinations_slug (slug),
    INDEX idx_destinations_name (name)
);

-- Tabla de actividades
CREATE TABLE activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destination_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration VARCHAR(50),
    price DECIMAL(10,2),
    difficulty_level ENUM('EASY', 'MODERATE', 'HARD', 'EXTREME') DEFAULT 'EASY',
    max_capacity INT,
    is_available BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE CASCADE,
    INDEX idx_activities_destination (destination_id),
    INDEX idx_activities_name (name)
);

-- Tabla de hoteles
CREATE TABLE hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destination_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(500),
    image_url VARCHAR(500),
    stars INT CHECK (stars >= 1 AND stars <= 5),
    base_price DECIMAL(10, 2) NULL,      -- <--- Incluida desde V3
    rating DECIMAL(3, 2) NULL,           -- <--- Incluida desde V4
    amenities JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    website_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    checkin_policy TEXT,
    checkout_policy TEXT,
    cancellation_policy TEXT,
    FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE CASCADE,
    INDEX idx_hotels_destination (destination_id),
    INDEX idx_hotels_name (name)
);

-- Tabla de tipos de habitaciones
CREATE TABLE room_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    capacity INT NOT NULL CHECK (capacity >= 1),
    price_per_night DECIMAL(10,2) NOT NULL CHECK (price_per_night >= 0),
    available_rooms INT DEFAULT 0,
    total_rooms INT DEFAULT 0,
    room_size INT,
    bed_type ENUM('SINGLE', 'DOUBLE', 'KING', 'QUEEN', 'TWIN_BEDS', 'SOFA_BED') DEFAULT 'DOUBLE',
    is_active BOOLEAN DEFAULT TRUE,
    view_type ENUM('OCEAN', 'CITY', 'GARDEN', 'MOUNTAIN', 'POOL', 'INTERIOR') DEFAULT 'CITY',
    
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    INDEX idx_room_types_hotel (hotel_id),
    INDEX idx_room_types_capacity (capacity)
);

-- Tabla de reservas
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    destination_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    room_type_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL CHECK (number_of_guests >= 1),
    number_of_rooms INT DEFAULT 1,
    total_price DECIMAL(12,2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'CHECKED_IN', 'CHECKED_OUT') DEFAULT 'PENDING',
    confirmation_code VARCHAR(20) UNIQUE,
    special_requests TEXT,
    accommodation_price DECIMAL(10,2),
    activities_price DECIMAL(10,2) DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE CASCADE,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE CASCADE,
    
    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_dates (check_in_date, check_out_date),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_confirmation (confirmation_code)
);

-- Tabla de actividades en reservas
CREATE TABLE booking_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    scheduled_date DATE NOT NULL,
    quantity INT NOT NULL,               -- Unifica el nombre con la entidad Java
    price_per_person DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    is_active BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE,

    INDEX idx_booking_activities_booking (booking_id),
    INDEX idx_booking_activities_activity (activity_id),
    INDEX idx_booking_activities_date (scheduled_date)
);

-- Tabla de contactos
CREATE TABLE contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    phone_number VARCHAR(20),
    inquiry_type ENUM('GENERAL', 'BOOKING', 'DESTINATIONS', 'PRICING', 'TECHNICAL_SUPPORT', 'COMPLAINT', 'SUGGESTION', 'CANCELLATION') DEFAULT 'GENERAL',
    status ENUM('NEW', 'READ', 'IN_PROGRESS', 'RESPONDED', 'CLOSED') DEFAULT 'NEW',
    responded_at TIMESTAMP NULL,
    responded_by BIGINT,
    response_message TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    INDEX idx_contacts_email (email),
    INDEX idx_contacts_status (status),
    INDEX idx_contacts_created (created_at)
);
