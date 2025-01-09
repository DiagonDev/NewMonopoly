/*INSERT INTO Pedina (nome) VALUES ('Rosso');
INSERT INTO Pedina (nome) VALUES ('Blu');
INSERT INTO Pedina (nome) VALUES ('Giallo');
INSERT INTO Pedina (nome) VALUES ('Verde');
INSERT INTO Pedina (nome) VALUES ('Arancione');
INSERT INTO Pedina (nome) VALUES ('Viola');

INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Via', null, null, null, 'Via', 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Vicolo Corto', 0, false, 'Marrone', 'Proprietà', 60, 2);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Probabilità', null, null, null, 'Probabilità', null);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Vicolo Stretto', 0, false, 'Marrone', 'Proprietà', 60, 4);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Tassa Patrimoniale', null, null, null, 'Tassa', 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Stazione Sud', null, null, null, 'Stazione', 200);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (7, 6, 50, 50, 50);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Bastioni Grand Sasso', 0, false, 'Azzurro', 'Proprietà', 100, 7);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Imprevisto', null, null, null, 'Imprevisto', null);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (9, 6, 50, 50, 50);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Viale Monte Rosa', 0, false, 'Azzurro', 'Proprietà', 100, 9);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (10, 8, 60, 50, 50);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Viale Vesuvio', 0, false, 'Azzurro', 'Proprietà', 120, 10);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Prigione', null, null, null, 'Prigione', null);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (12, 10, 70, 100, 100);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Via Accademia', 0, false, 'Rosa', 'Proprietà', 140, 12);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Società Elettrica', null, null, null, 'Società', 150);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (14, 10, 70, 100, 100);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Corso Ateneo', 0, false, 'Rosa', 'Proprietà', 140, 14);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (15, 12, 80, 100, 100);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Piazza Università', 0, false, 'Rosa', 'Proprietà', 160, 15);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Stazione Ovest', null, null, null, 'Stazione', 200);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (17, 14, 90, 100, 100);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Via Verdi', 0, false, 'Arancione', 'Proprietà', 180, 17);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Probabilità', null, null, null, 'Probabilità', null);
INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (19, 14, 90, 100, 100);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo,idprezzoproprieta) VALUES ('Corso Raffaello', 0, false, 'Arancione', 'Proprietà', 180, 19);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (20, 16, 100, 100, 100);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Piazza Dante', 0, false, 'Arancione', 'Proprietà', 200, 20);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Posteggio Gratuito', null, null, null, 'Posteggio', null);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (22, 18, 110, 150, 150);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Via Marco Polo', 0, false, 'Rosso', 'Proprietà', 220, 22);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Imprevisto', null, null, null, 'Imprevisto', null);
    INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (24, 18, 110, 150, 150);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Corso Magellano', 0, false, 'Rosso', 'Proprietà', 220, 24);
    INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (25, 20, 120, 150, 150);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Largo Colombo', 0, false, 'Rosso', 'Proprietà', 240, 25);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Stazione Nord', null, null, null, 'Stazione', 200);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (27, 22, 130, 150, 150);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Viale Costantino', 0, false, 'Giallo', 'Proprietà', 260, 27);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (28, 22, 130, 150, 150);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Viale Traiano', 0, false, 'Giallo', 'Proprietà', 260, 28);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Società Acqua Potabile', null, null, null, 'Società', 150);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (30, 24, 140, 150, 150);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Piazza Giulio Cesare', 0, false, 'Giallo', 'Proprietà', 280, 30);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('In Prigione', null, null, null, 'InPrigione', null);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (32, 26, 150, 200, 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Via Roma', 0, false, 'Verde', 'Proprietà', 300, 32);
    INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (33, 26, 150, 200, 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Corso Impero', 0, false, 'Verde', 'Proprietà', 300, 33);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Probabilità', null, null, null, 'Probabilità', null);
    INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (35, 28, 160, 200, 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Largo Augusto', 0, false, 'Verde', 'Proprietà', 320, 35);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Stazione Est', null, null, null, 'Stazione', 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Imprevisto', null, null, null, 'Imprevisto', null);
    INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (38, 35, 175, 200, 200);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo, idprezzoproprieta) VALUES ('Viale Dei Giardini', 0, false, 'Blu', 'Proprietà', 350, 38);
INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo) VALUES ('Tassa Di Lusso', null, null, null, 'Tassa', 100);
  INSERT INTO prezzoproprieta(idPrezzoproprieta,affitto,ipoteca,casa,hotel) VALUES (40, 50, 200, 200, 200);
  INSERT INTO Casella(nome, num_case, num_albergo, colore, tipo, prezzo,idprezzoproprieta) VALUES ('Parco Della Vittoria', 0, false, 'Blu', 'Proprietà', 400, 40);


INSERT INTO prezzoproprieta(idPrezzo,affitto,ipoteca,casa,hotel) VALUES (2, 2, 30, 50, 50);
INSERT INTO prezzoproprieta(idPrezzo,affitto,ipoteca,casa,hotel) VALUES (4, 4, 30, 50, 50);







CREATE TABLE Pedina(
    idPedina SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);
CREATE TABLE Giocatore(
	idGiocatore SERIAL PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	saldo INTEGER NOT NULL,
	puntiFedelta INTEGER NOT NULL,
	tipo VARCHAR(20),
	CONSTRAINT controlloTipo CHECK (tipo IN ('admin', 'giocatore', 'imprenditore')),
	idPedina INTEGER REFERENCES Pedina(idPedina),
	idPartita INTEGER REFERENCES Partita(idPartita)
);
CREATE TABLE Casella(
	idCasella SERIAL PRIMARY KEY,
	nome VARCHAR(50) NOT NULL,
	num_Case INTEGER,
	num_Albergo BOOLEAN,
	colore VARCHAR(100),
	tipo VARCHAR(100),
	prezzo INTEGER,
	idPrezzoProprieta INTEGER REFERENCES PrezzoProprieta(idPrezzoProprieta),
	idGiocatore INTEGER REFERENCES Giocatore(idGiocatore)
);

CREATE TABLE Imprevisto (
    idImprevisto SERIAL PRIMARY KEY,
    descrizione VARCHAR(255) NOT NULL
);
CREATE TABLE Probabilita (
    idProbabilita SERIAL PRIMARY KEY,
    descrizione VARCHAR(255) NOT NULL
);
CREATE TABLE RegolaFedelta (
    idRegolaFedelta SERIAL PRIMARY KEY,
	puntiFedelta INTEGER NOT NULL,
    descrizione VARCHAR(255) NOT NULL
);
CREATE TABLE Partita_Casella(
	PartitaidPartita INTEGER REFERENCES Partita(idPartita),
	CasellaidCasella INTEGER REFERENCES Casella(idCasella),
	PRIMARY KEY (PartitaidPartita, CasellaidCasella)
);
CREATE TABLE Partita_Imprevisto(
	PartitaidPartita INTEGER REFERENCES Partita(idPartita),
	ImprevistoidImprevisto INTEGER REFERENCES Imprevisto(idImprevisto),
	PRIMARY KEY (PartitaidPartita, ImprevistoidImprevisto)
);
CREATE TABLE Partita_Probabilita(
	PartitaidPartita INTEGER REFERENCES Partita(idPartita),
	ProbabilitaidProbabilita INTEGER REFERENCES Probabilita(idProbabilita),
	PRIMARY KEY (PartitaidPartita, ProbabilitaidProbabilita)
);
CREATE TABLE Partita_RegoleFedelta(
	PartitaidPartita INTEGER REFERENCES Partita(idPartita),
	RegolaFedeltaidRegolaFedelta INTEGER REFERENCES RegolaFedelta(idRegolaFedelta),
	PRIMARY KEY (PartitaidPartita, RegolaFedeltaidRegolaFedelta)
);*/






