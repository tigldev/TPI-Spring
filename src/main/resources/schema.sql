-- Tabla para las categorías
CREATE TABLE categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),
    PRIMARY KEY (id)
);

-- Tabla para los productos
CREATE TABLE producto (
    id VARCHAR(36) NOT NULL,
    nombre VARCHAR(255),
    descripcion VARCHAR(255),
    precio DOUBLE,
    stock INT,
    fecha_de_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    PRIMARY KEY (id)
);

-- Tabla de unión para la relación muchos a muchos entre producto y categoria
CREATE TABLE producto_categorias (
    producto_id VARCHAR(36) NOT NULL,
    categorias_id BIGINT NOT NULL,
    PRIMARY KEY (producto_id, categorias_id),
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (categorias_id) REFERENCES categoria(id)
);

-- Secuencia para H2/Hibernate
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;