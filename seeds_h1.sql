-- Seeds H1 - Sistema de Gestión de Tickets

-- Usuarios
INSERT INTO users (firstName, lastName, email, password, role) VALUES
('Juan', 'Pérez', 'juan.perez@hospital.com', 'Password123!', 'user'),
('María', 'López', 'maria.lopez@hospital.com', 'Password123!', 'support'),
('Carlos', 'Gómez', 'carlos.gomez@hospital.com', 'Password123!', 'sysAdmin');

-- Categorías
INSERT INTO categorias (nombre, descripcion) VALUES
('Hardware', 'Problemas relacionados con hardware'),
('Software', 'Errores o consultas relacionadas con software'),
('Redes', 'Incidencias de conectividad y red'),
('Otros', 'Cualquier otro tipo de solicitud');

-- Tickets
INSERT INTO tickets (title, description, status, priority, creatorId, assigneeId, categoriaId) VALUES
('Error en login', 'No puedo iniciar sesión con mis credenciales', 'open', 'high', 1, 2, 2),
('PC sin red', 'La computadora de la guardia no tiene acceso a internet', 'in_progress', 'urgent', 1, 2, 3),
('Solicitud de nuevo usuario', 'Se requiere crear un nuevo usuario para el área de pediatría', 'resolved', 'medium', 2, 2, 4);

-- Comentarios
INSERT INTO comments (text, ticketId, userId, isPrivate) VALUES
('He revisado el problema y encontré la causa en la configuración del servidor.', 1, 2, FALSE),
('Se está trabajando en la conectividad del switch.', 2, 2, FALSE),
('Usuario creado exitosamente.', 3, 2, FALSE);

-- Adjuntos
INSERT INTO attachments (filename, fileSize, mimeType, ticketId, userId, isPrivate) VALUES
('screenshot_error.png', 204800, 'image/png', 1, 1, FALSE),
('log_conexion.txt', 10240, 'text/plain', 2, 2, TRUE);
