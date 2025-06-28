-- Migración para insertar datos de ejemplo en el sistema Caribe Vibes
-- Autor: Sistema Caribe Vibes
-- Fecha: 2024-12-11
-- Descripción: Inserción de datos de ejemplo para experiencias, destinos y hoteles

-- Insertar experiencias
INSERT INTO experiences (slug, name, description, icon_url, color, display_order, is_active) VALUES
('aventura', 'Aventura', 'Actividades llenas de adrenalina y emoción para los más aventureros', '/assets/images/aventura.png', '#FF6B35', 1, TRUE),
('relax', 'Relax', 'Momentos de tranquilidad y descanso en entornos paradisíacos', '/assets/images/relax.png', '#4ECDC4', 2, TRUE),
('cultura', 'Cultura', 'Descubre la rica historia y tradiciones del Caribe colombiano', '/assets/images/cultura.png', '#45B7D1', 3, TRUE),
('gastronomia', 'Gastronomía', 'Degusta los sabores únicos de la cocina caribeña', '/assets/images/gastronomia.png', '#F7DC6F', 4, TRUE),
('naturaleza', 'Naturaleza', 'Explora ecosistemas únicos y biodiversidad excepcional', '/assets/images/naturaleza.png', '#58D68D', 5, TRUE),
('romance', 'Romance', 'Escapadas románticas perfectas para parejas', '/assets/images/romance.png', '#EC7063', 6, TRUE);

-- Insertar destinos
INSERT INTO destinations (country_id, slug, name, description, long_description, location, image_url, tags, experiences, low_season_price, high_season_price) VALUES
(1, 'cartagena', 'Cartagena de Indias', 'Ciudad amurallada llena de historia y encanto colonial', 
'Cartagena de Indias es una joya del Caribe colombiano que combina historia, cultura y belleza natural. Sus murallas coloniales, calles empedradas y arquitectura colonial la convierten en Patrimonio de la Humanidad. Disfruta de playas paradisíacas, gastronomía exquisita y una vibrante vida nocturna.',
'Bolívar, Colombia',
'/assets/images/cartagena.png', 
'["Historia", "Patrimonio", "Playas", "Gastronomía", "Vida Nocturna"]',
'["cultura", "gastronomia", "romance", "relax"]',
350000.00, 450000.00),

(1, 'santa-marta', 'Santa Marta', 'La perla del Caribe colombiano con playas espectaculares',
'Santa Marta es la ciudad más antigua de Colombia, ubicada entre la Sierra Nevada y el mar Caribe. Ofrece una combinación única de playas paradisíacas, montañas nevadas, parques nacionales y sitios arqueológicos. Es la puerta de entrada al Parque Nacional Tayrona y Ciudad Perdida.',
'Magdalena, Colombia',
'/assets/images/santa-marta.png',
'["Playas", "Montañas", "Parques Nacionales", "Arqueología", "Aventura"]',
'["aventura", "naturaleza", "relax", "cultura"]',
320000.00, 420000.00),

(1, 'san-andres', 'San Andrés', 'Isla paradisíaca con el mar de los siete colores',
'San Andrés es un archipiélago colombiano en el Caribe conocido por sus aguas cristalinas de múltiples tonalidades azules, arrecifes de coral, y cultura raizal única. Perfect para buceo, snorkeling y relajación en playas de arena blanca.',
'Archipiélago de San Andrés, Providencia y Santa Catalina',
'/assets/images/san-andres.png',
'["Buceo", "Arrecifes", "Playas", "Cultura Raizal", "Aguas Cristalinas"]',
'["relax", "aventura", "naturaleza", "cultura"]',
400000.00, 550000.00),

(1, 'providencia', 'Providencia', 'Isla virgen con naturaleza exuberante',
'Providencia es una isla menos comercial que San Andrés, perfecta para quienes buscan tranquilidad y contacto directo con la naturaleza. Conocida por sus increíbles sitios de buceo, manglares y la hospitalidad de su gente.',
'Archipiélago de San Andrés, Providencia y Santa Catalina',
'/assets/images/providencia.png',
'["Naturaleza Virgen", "Buceo", "Manglares", "Tranquilidad", "Ecoturismo"]',
'["naturaleza", "relax", "aventura"]',
380000.00, 500000.00),

(1, 'tayrona', 'Parque Tayrona', 'Paraíso natural entre selva y mar',
'El Parque Nacional Natural Tayrona combina selva tropical, playas vírgenes y sitios arqueológicos. Es hogar de una biodiversidad única y ofrece experiencias de ecoturismo, senderismo y conexión con pueblos indígenas.',
'Magdalena, Colombia',
'/assets/images/tayrona.png',
'["Ecoturismo", "Senderismo", "Biodiversidad", "Arqueología", "Playas Vírgenes"]',
'["naturaleza", "aventura", "cultura"]',
280000.00, 380000.00);

-- Insertar actividades
INSERT INTO activities (destination_id, name, description, duration, price, difficulty_level, max_capacity, is_available) VALUES
-- Actividades Cartagena
(1, 'Tour por la Ciudad Amurallada', 'Recorrido a pie por el centro histórico de Cartagena', '3 horas', 45000.00, 'EASY', 25, TRUE),
(1, 'Paseo en Chiva por la ciudad', 'Tour nocturno en chiva rumbera', '4 horas', 80000.00, 'EASY', 30, TRUE),
(1, 'Clase de Cocina Caribeña', 'Aprende a preparar platos típicos', '4 horas', 120000.00, 'EASY', 12, TRUE),
(1, 'Tour de Islas del Rosario', 'Excursión en lancha a las islas', 'Día completo', 180000.00, 'MODERATE', 20, TRUE),

-- Actividades Santa Marta
(2, 'Trekking a Ciudad Perdida', 'Caminata de varios días al sitio arqueológico', '4 días', 650000.00, 'HARD', 16, TRUE),
(2, 'Tour al Parque Tayrona', 'Excursión de un día al parque nacional', 'Día completo', 150000.00, 'MODERATE', 25, TRUE),
(2, 'Avistamiento de Aves', 'Tour especializado en observación de aves', '6 horas', 95000.00, 'EASY', 15, TRUE),

-- Actividades San Andrés
(3, 'Buceo en Arrecifes', 'Inmersión en los arrecifes de coral', '4 horas', 220000.00, 'MODERATE', 8, TRUE),
(3, 'Snorkeling en Johnny Cay', 'Snorkeling en aguas cristalinas', '3 horas', 85000.00, 'EASY', 20, TRUE),
(3, 'Tour Acuático Completo', 'Recorrido por varias islas y cayos', 'Día completo', 250000.00, 'EASY', 25, TRUE),

-- Actividades Providencia
(4, 'Kayak en Manglares', 'Exploración en kayak por los manglares', '3 horas', 75000.00, 'EASY', 12, TRUE),
(4, 'Buceo Avanzado', 'Inmersiones para buzos certificados', '5 horas', 280000.00, 'HARD', 6, TRUE),

-- Actividades Tayrona
(5, 'Senderismo Ecológico', 'Caminata por senderos del parque', '5 horas', 65000.00, 'MODERATE', 20, TRUE),
(5, 'Camping en la Playa', 'Experiencia de camping frente al mar', '2 días', 180000.00, 'MODERATE', 10, TRUE);

-- Insertar hoteles
INSERT INTO hotels (destination_id, name, description, address, image_url, stars, amenities, phone_number, email, website_url, is_active) VALUES
-- Hoteles Cartagena
(1, 'Hotel Boutique Casa San Agustín', 'Hotel boutique de lujo en el centro histórico', 'Calle Universidad 36-44, Centro Histórico', '/assets/images/hotel-casa-san-agustin.jpg', 5, 
'["wifi", "spa", "piscina", "restaurante", "bar", "gimnasio", "aire_acondicionado", "servicio_habitaciones"]', 
'+57 5 642 8000', 'reservas@casasanagustin.com', 'https://casasanagustin.com', TRUE),

(1, 'Hotel Charleston Santa Teresa', 'Hotel de lujo con vista al mar', 'Plaza de Santa Teresa, Centro Histórico', '/assets/images/hotel-charleston.jpg', 5,
'["wifi", "spa", "piscina_infinita", "restaurante", "bar", "gimnasio", "terraza", "vista_mar"]',
'+57 5 664 9494', 'info@charleston.com.co', 'https://charleston.com.co', TRUE),

(1, 'Hotel Caribe', 'Hotel clásico frente al mar', 'Carrera 1a 2-87, Bocagrande', '/assets/images/hotel-caribe.jpg', 4,
'["wifi", "piscina", "restaurante", "bar", "playa_privada", "aire_acondicionado"]',
'+57 5 650 1010', 'reservas@hotelcaribe.com', 'https://hotelcaribe.com', TRUE),

-- Hoteles Santa Marta
(2, 'Hotel Casa de Leda', 'Boutique hotel con encanto colonial', 'Calle 16 3-56, Centro Histórico', '/assets/images/hotel-casa-leda.jpg', 4,
'["wifi", "piscina", "restaurante", "bar", "terraza", "aire_acondicionado"]',
'+57 5 438 1111', 'info@casadeleda.com', 'https://casadeleda.com', TRUE),

(2, 'Marriott Santa Marta', 'Hotel moderno frente al mar', 'Calle 22 3-34, Playa Salguero', '/assets/images/marriott-santa-marta.jpg', 5,
'["wifi", "spa", "piscina", "restaurante", "bar", "gimnasio", "playa_privada", "centro_negocios"]',
'+57 5 438 4000', 'santamarta@marriott.com', 'https://marriott.com/santamarta', TRUE),

-- Hoteles San Andrés
(3, 'Hotel Casa Harb', 'Hotel boutique con vista al mar', 'Av. Francisco Newball, Punta Norte', '/assets/images/hotel-casa-harb.jpg', 4,
'["wifi", "piscina", "restaurante", "bar", "vista_mar", "aire_acondicionado"]',
'+57 8 513 0043', 'reservas@casaharb.com', 'https://casaharb.com', TRUE),

(3, 'Decamerón San Luis', 'Resort todo incluido', 'Bahía San Luis', '/assets/images/decameron-san-luis.jpg', 4,
'["wifi", "todo_incluido", "piscina", "restaurante", "bar", "playa_privada", "entretenimiento", "deportes_acuaticos"]',
'+57 8 513 7018', 'sanandres@decameron.com', 'https://decameron.com/sanandres', TRUE),

-- Hoteles Providencia
(4, 'Hotel Sirius', 'Hotel ecológico en la naturaleza', 'Aguadulce, Providencia', '/assets/images/hotel-sirius.jpg', 3,
'["wifi", "restaurante", "bar", "ecoturismo", "aire_acondicionado"]',
'+57 8 514 8213', 'info@hotelsirius.com', 'https://hotelsirius.com', TRUE),

-- Hoteles Tayrona
(5, 'Ecohabs Tayrona', 'Cabañas ecológicas en el parque', 'Parque Nacional Tayrona', '/assets/images/ecohabs-tayrona.jpg', 3,
'["ecoturismo", "restaurante", "senderos", "playa_virgen"]',
'+57 5 438 3000', 'reservas@ecohabs.com', 'https://ecohabs.com', TRUE);

-- Insertar tipos de habitaciones
INSERT INTO room_types (hotel_id, name, description, capacity, price_per_night, available_rooms, total_rooms, room_size, bed_type, view_type) VALUES
-- Habitaciones Hotel Casa San Agustín
(1, 'Habitación Superior', 'Habitación elegante con decoración colonial', 2, 850000.00, 8, 12, 35, 'KING', 'CITY'),
(1, 'Suite Junior', 'Suite espaciosa con sala de estar', 3, 1200000.00, 4, 6, 55, 'KING', 'GARDEN'),
(1, 'Suite Presidencial', 'Suite de lujo con terraza privada', 4, 2500000.00, 1, 2, 85, 'KING', 'CITY'),

-- Habitaciones Hotel Charleston
(2, 'Habitación Estándar', 'Habitación cómoda con vista parcial al mar', 2, 750000.00, 15, 20, 32, 'DOUBLE', 'OCEAN'),
(2, 'Habitación Deluxe', 'Habitación amplia con balcón', 3, 950000.00, 8, 12, 42, 'KING', 'OCEAN'),
(2, 'Suite Imperial', 'Suite de lujo con vista panorámica', 4, 1800000.00, 2, 4, 75, 'KING', 'OCEAN'),

-- Habitaciones Hotel Caribe
(3, 'Habitación Estándar', 'Habitación clásica con aire acondicionado', 2, 450000.00, 25, 35, 28, 'DOUBLE', 'CITY'),
(3, 'Habitación Vista Mar', 'Habitación con vista directa al mar', 2, 650000.00, 12, 18, 32, 'QUEEN', 'OCEAN'),

-- Habitaciones Marriott Santa Marta
(5, 'Habitación Deluxe', 'Habitación moderna con vista al mar', 2, 580000.00, 20, 30, 38, 'KING', 'OCEAN'),
(5, 'Suite Ejecutiva', 'Suite con sala de estar y terraza', 4, 920000.00, 6, 10, 62, 'KING', 'OCEAN'),

-- Habitaciones Decamerón San Luis
(7, 'Habitación Estándar', 'Habitación todo incluido', 2, 380000.00, 30, 50, 30, 'DOUBLE', 'GARDEN'),
(7, 'Habitación Superior', 'Habitación con vista al mar', 3, 450000.00, 20, 30, 35, 'KING', 'OCEAN'),

-- Habitaciones Ecohabs Tayrona
(9, 'Ecohab Estándar', 'Cabaña ecológica básica', 2, 320000.00, 8, 12, 25, 'DOUBLE', 'GARDEN'),
(9, 'Ecohab Familiar', 'Cabaña para familias', 4, 480000.00, 4, 8, 40, 'TWIN_BEDS', 'GARDEN');
