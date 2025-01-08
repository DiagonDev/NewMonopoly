import { Routes, Route, Link } from 'react-router-dom';
import WelcomePage from './pages/WelcomePage'; // Importa il componente WelcomePage
import CreateGamePage from './pages/CreateGamePage';
import JoinGamePage from './pages/JoinGamePage';
import './App.css';
import React, { useState, useEffect } from 'react';


function App() {

  const [connected, setConnected] = useState(false);
  const [socket, setSocket] = useState(null);
  const [serverMessage, setServerMessage] = useState('');

// Crea una connessione WebSocket quando il componente viene montato
useEffect(() => {
  const ws = new WebSocket("wss://f957-84-33-176-173.ngrok-free.app/ws/connection");

  ws.onopen = () => {
    setSocket(ws);
    setConnected(true);
    console.log("Connessione WebSocket stabilita");
  };

  ws.onmessage = (event) => {
    console.log("Messaggio dal server:", event.data);
    setServerMessage(event.data);
  };

  ws.onerror = (error) => {
    console.error("Errore WebSocket:", error);
  };

  ws.onclose = (event) => {
    console.warn("Connessione WebSocket chiusa:", event);
    setConnected(false); // Segna che la connessione è chiusa
  };

  return () => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.close();
    }
  };
}, []);

  return (
    <>
       {/* Titolo in alto a destra */}
       <h1 className="titolo">NewMonopolyGame</h1>
      {/* Contenitore principale */}
      <div className="main-container">
        {/* Configura le rotte */}
        <Routes>
          <Route path="*" element={<WelcomePage />} /> {/* WelcomePage come rotta predefinita */}
          {/* Passa socket e connected come props */}
          <Route path="/create" element={<CreateGamePage socket={socket} connected={connected} />}/> 
          <Route path="/join" element = {<JoinGamePage socket= {socket} connected ={connected} />}/>
        </Routes>
        {/* Mostra il messaggio ricevuto dal server */}
      {serverMessage && <p>Messaggio dal server: {serverMessage}</p>}
      </div>
    </>
  );
}

export default App;
