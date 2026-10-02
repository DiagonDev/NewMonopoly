# NewMonopoly 

**NewMonopoly** is a full stack application that recreates and digitizes the classic board game logic of *Monopoly*. The project was developed as a university assignment for the Software Engineering course.

---

##  Technologies and Architecture

The project was developed by applying architectural and behavioral patterns to ensure modularity, testability and scalability:

* **Event-Driven Architecture & WebSockets:** Real-time communication handled through asynchronous sockets for responsive game events.
* **Dispatcher Pattern (`WebSocketConnectorDispatcher`):** Centralized message routing logic to decouple dispatching from processing.
* **Service & Repository Pattern:** Clear separation between business logic and data persistence (PostgreSQL).
* **Manager/Handler Pattern (`TurnManager`, `GameHandler`):** Component-based design with isolated responsibilities for managing turns and matches.
* **Singleton (`GameBoardSingleton`):** Game state managed in a single shared and synchronized instance.
* **Observer Pattern (Frontend):** Reactive synchronization of the UI state in React using lifecycle hooks (`useEffect`).
* **SOLID Principles (SRP and OCP):** Single-responsibility classes and high system extensibility without modifying existing source code.

---

## Main Features

* **Board and Turn Management:** Full simulation of the game board, dice and token movement.
* **Economic System:** Buying and selling properties, managing houses/hotels, mortgages and bank transactions.
* **Chance and Community Chest Cards:** Dynamic handling of random events during the match.
* **Game Logic & Rules:** Automatic checking of bankruptcy conditions, jail and the final winner.

---

## Development Team

University project developed by:
* [Alessandro Messa](https://github.com/DiagonDev)
* [Matteo Ronchi](https://github.com/MatteoRonchiDev)
* [Francesca Tentori](https://github.com/tentorifrancescaDev)
* [Luca Teruzzi](https://github.com/LucaTeruUNIMIB)

---

## Project Structure

```
├── .github/                      # CI/CD workflows (GitHub Actions for rollback and deployment)
├── NewMonopolyBackEnd/           # Server-side development (Spring Boot, PostgreSQL, WebSockets)
├── NewMonopolyFrontEnd/          # Client-side development (React, HTML5, CSS3)
├── RelazioneNewMonopoly.pdf      # Complete and detailed technical documentation
├── Diagramma di Gantt.xlsx       # Project planning and timeline management
└── NewMonopoly.vpp               # Visual Paradigm project (UML diagrams and modeling)

> **Documentation Note:** The full report, requirements analysis, architecture diagrams and all UML diagrams are available in the **`RelazioneNewMonopoly.pdf`** file.
```

## Work Methodology and CI/CD

* **Project Management (Agile/Waterfall):** The workflow was planned and tracked using a **Gantt Chart** to break down milestones, manage dependencies and assign tasks between Frontend and Backend.
* **Continuous Integration & Deployment (CI/CD):** Use of **GitHub Actions** (`.github/`) to automate build, testing and release rollback processes.
