# NewMonopoly 

**NewMonopoly** è un'applicazione desktop che ricrea e digitalizza la classica logica di gioco da tavolo del *Monopoly*. Il progetto è stato sviluppato come elaborato universitario per il corso di Ingegneria del Software.

---

##  Tecnologie e Architettura

Il progetto è stato sviluppato applicando pattern architetturali e comportamentali per garantire modularità, testabilità e scalabilità:

* **Event-Driven Architecture & WebSockets:** Comunicazione in tempo reale gestita tramite socket asincroni per la reattività degli eventi di gioco.
* **Dispatcher Pattern (`WebSocketConnectorDispatcher`):** Centralizzazione della logica di routing dei messaggi per disaccoppiare lo smistamento dall'elaborazione.
* **Service & Repository Pattern:** Separazione netta tra logica di business e persistenza dei dati (PostgreSQL).
* **Manager/Handler Pattern (`TurnManager`, `GameHandler`):** Design basato su componenti con responsabilità isolate per la gestione di turni e partite.
* **Singleton (`GameBoardSingleton`):** Gestione dello stato di gioco in un'unica istanza condivisa e sincronizzata.
* **Observer Pattern (Frontend):** Sincronizzazione reattiva dello stato UI in React sfruttando gli hook del ciclo di vita (`useEffect`).
* **Principi SOLID (SRP e OCP):** Classi a responsabilità singola ed elevata estendibilità del sistema senza modifiche al codice sorgente esistente.

---

## Funzionalità Principali

* **Gestione Tabellone e Turni:** Simulazione completa del tabellone di gioco, dei dadi e del movimento dei segnalini.
* **Sistema Economico:** Compravendita delle proprietà, gestione di case/alberghi, ipoteche e transazioni bancarie.
* **Carte Imprevisti e Probabilità:** Gestione dinamica degli eventi casuali durante la partita.
* **Logica di Gioco & Regole:** Controllo automatico delle condizioni di bancarotta, della prigione e del vincitore finale.

---

## Team di Sviluppo

Progetto universitario realizzato da:
* [Alessandro Messa](https://github.com/DiagonDev)
* [Matteo Ronchi](https://github.com/MatteoRonchiDev)
* [Francesca Tentori](https://github.com/FrancescaTentoriDev)
* [Luca Teruzzi](https://github.com/LucaTeruUNIMIB)

---

## Struttura del Progetto

```
├── .github/                      # Workflows di CI/CD (GitHub Actions per rollback e deployment)
├── NewMonopolyBackEnd/           # Sviluppo server-side (Spring Boot, PostgreSQL, WebSockets)
├── NewMonopolyFrontEnd/          # Sviluppo client-side (React, HTML5, CSS3)
├── RelazioneNewMonopoly.pdf      # Documentazione tecnica completa e dettagliata
├── Diagramma di Gantt.xlsx       # Pianificazione e gestione tempistiche di progetto
└── NewMonopoly.vpp               # Progetto Visual Paradigm (Diagrammi UML e modellazione)

> **Nota sulla Documentazione:** La relazione completa, l'analisi dei requisiti, gli schemi dell'architettura e tutti i diagrammi UML sono disponibili nel file **`RelazioneNewMonopoly.pdf`**.
```

## Metodologia di Lavoro e CI/CD

* **Gestione del Progetto (Agile/Waterfall):** Il flusso di lavoro è stato pianificato e tracciato tramite **Diagramma di Gantt** per la suddivisione delle milestone, la gestione delle dipendenze e l'assegnazione dei compiti tra Frontend e Backend.
* **Continuous Integration & Deployment (CI/CD):** Utilizzo di **GitHub Actions** (`.github/`) per l'automazione dei processi di build, testing e gestione dei rollback delle release.
