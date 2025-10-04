
CREATE TABLE IF NOT EXISTS Trabajos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente TEXT NOT NULL,
    descripcion TEXT,
    fecha TEXT,
    hora TEXT
);


CREATE TABLE IF NOT EXISTS Estructuras (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trabajo_id INTEGER NOT NULL,
    descripcion TEXT,
    imagen TEXT,
    FOREIGN KEY(trabajo_id) REFERENCES Trabajos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Piezas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    estructura_id INTEGER NOT NULL,
    tipoMaterial TEXT NOT NULL,
    ancho REAL,
    alto REAL,
    largo REAL,
    cantidad INTEGER,
    descripcion TEXT,
    totalM2 REAL,
    FOREIGN KEY(estructura_id) REFERENCES Estructuras(id) ON DELETE CASCADE
);
