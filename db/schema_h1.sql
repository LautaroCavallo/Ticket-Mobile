-- Schema H1 - Sistema de Gestión de Tickets

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('user','support','sysAdmin')) NOT NULL DEFAULT 'user',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastLogin TIMESTAMP
);

CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('open','in_progress','resolved','closed')) DEFAULT 'open',
    priority VARCHAR(20) CHECK (priority IN ('low','medium','high','urgent')) DEFAULT 'medium',
    creatorId INT NOT NULL,
    assigneeId INT,
    categoriaId INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP,
    resolvedAt TIMESTAMP,
    CONSTRAINT fk_ticket_creator FOREIGN KEY (creatorId) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_assignee FOREIGN KEY (assigneeId) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_ticket_categoria FOREIGN KEY (categoriaId) REFERENCES categorias(id) ON DELETE SET NULL
);

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    ticketId INT NOT NULL,
    userId INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isPrivate BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_comment_ticket FOREIGN KEY (ticketId) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE attachments (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    fileSize INT,
    mimeType VARCHAR(100),
    ticketId INT NOT NULL,
    userId INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isPrivate BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_attachment_ticket FOREIGN KEY (ticketId) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);

-- Datos iniciales de categorías
INSERT INTO categorias (nombre, descripcion) VALUES
('Hardware', 'Problemas relacionados con hardware'),
('Software', 'Errores o consultas relacionadas con software'),
('Redes', 'Incidencias de conectividad y red'),
('Otros', 'Cualquier otro tipo de solicitud');
