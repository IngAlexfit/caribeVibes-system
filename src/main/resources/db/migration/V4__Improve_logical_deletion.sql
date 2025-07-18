-- Migración para mejorar eliminación lógica en todas las tablas principales
-- Autor: Sistema Caribe Vibes
-- Fecha: 2025-07-16
-- Descripción: Añadir campos deleted_at y mejorar eliminación lógica de forma segura

-- 1. Agregar campos de eliminación lógica donde falten (con manejo de errores)
-- Destinations: agregar is_active y deleted_at si no existen
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'destinations' AND COLUMN_NAME = 'is_active') = 0,
    'ALTER TABLE destinations ADD COLUMN is_active BOOLEAN DEFAULT TRUE',
    'SELECT "Column is_active already exists in destinations"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'destinations' AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE destinations ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT "Column deleted_at already exists in destinations"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Activities: agregar deleted_at si no existe
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'activities' AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE activities ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT "Column deleted_at already exists in activities"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Hotels: agregar deleted_at si no existe
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'hotels' AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE hotels ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT "Column deleted_at already exists in hotels"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Room_types: agregar deleted_at si no existe
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'room_types' AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE room_types ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT "Column deleted_at already exists in room_types"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Countries: agregar deleted_at si no existe
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'countries' AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE countries ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT "Column deleted_at already exists in countries"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Experiences: agregar deleted_at si no existe
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'experiences' AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE experiences ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT "Column deleted_at already exists in experiences"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. Actualizar registros existentes para que deleted_at sea NULL (activos)
-- Solo actualizar si deleted_at es NULL
UPDATE destinations SET deleted_at = NULL WHERE deleted_at IS NULL;
UPDATE activities SET deleted_at = NULL WHERE deleted_at IS NULL;
UPDATE hotels SET deleted_at = NULL WHERE deleted_at IS NULL;
UPDATE room_types SET deleted_at = NULL WHERE deleted_at IS NULL;
UPDATE countries SET deleted_at = NULL WHERE deleted_at IS NULL;
UPDATE experiences SET deleted_at = NULL WHERE deleted_at IS NULL;

-- 3. Crear índices para optimizar consultas con eliminación lógica (solo si no existen)
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'destinations' AND INDEX_NAME = 'idx_destinations_logical_deletion') = 0,
    'CREATE INDEX idx_destinations_logical_deletion ON destinations(is_active, deleted_at)',
    'SELECT "Index idx_destinations_logical_deletion already exists"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'activities' AND INDEX_NAME = 'idx_activities_logical_deletion') = 0,
    'CREATE INDEX idx_activities_logical_deletion ON activities(is_available, deleted_at)',
    'SELECT "Index idx_activities_logical_deletion already exists"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'hotels' AND INDEX_NAME = 'idx_hotels_logical_deletion') = 0,
    'CREATE INDEX idx_hotels_logical_deletion ON hotels(is_active, deleted_at)',
    'SELECT "Index idx_hotels_logical_deletion already exists"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'room_types' AND INDEX_NAME = 'idx_room_types_logical_deletion') = 0,
    'CREATE INDEX idx_room_types_logical_deletion ON room_types(is_active, deleted_at)',
    'SELECT "Index idx_room_types_logical_deletion already exists"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'countries' AND INDEX_NAME = 'idx_countries_logical_deletion') = 0,
    'CREATE INDEX idx_countries_logical_deletion ON countries(is_active, deleted_at)',
    'SELECT "Index idx_countries_logical_deletion already exists"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'caribe_vibes' AND TABLE_NAME = 'experiences' AND INDEX_NAME = 'idx_experiences_logical_deletion') = 0,
    'CREATE INDEX idx_experiences_logical_deletion ON experiences(is_active, deleted_at)',
    'SELECT "Index idx_experiences_logical_deletion already exists"'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. Crear vistas para entidades activas (eliminar si existen y recrear)
DROP VIEW IF EXISTS active_destinations;
CREATE VIEW active_destinations AS
SELECT * FROM destinations 
WHERE is_active = TRUE AND deleted_at IS NULL;

DROP VIEW IF EXISTS active_hotels;
CREATE VIEW active_hotels AS
SELECT * FROM hotels 
WHERE is_active = TRUE AND deleted_at IS NULL;

DROP VIEW IF EXISTS available_activities;
CREATE VIEW available_activities AS
SELECT * FROM activities 
WHERE is_available = TRUE AND deleted_at IS NULL;

DROP VIEW IF EXISTS active_countries;
CREATE VIEW active_countries AS
SELECT * FROM countries 
WHERE is_active = TRUE AND deleted_at IS NULL;

DROP VIEW IF EXISTS active_experiences;
CREATE VIEW active_experiences AS
SELECT * FROM experiences 
WHERE is_active = TRUE AND deleted_at IS NULL;

DROP VIEW IF EXISTS active_room_types;
CREATE VIEW active_room_types AS
SELECT * FROM room_types 
WHERE is_active = TRUE AND deleted_at IS NULL;
