import React, { useState, useEffect } from 'react';

const JoinGamePage = () => {
  // Stato per i campi del form
  const [name, setName] = useState('');
  const [gameId, setGameId] = useState('');
  const [connected, setConnected] = useState(false);
  const [socket, setSocket] = useState(null);
  
  // Crea una connessione WebSocket quando il componente viene montato
    useEffect(() => {
      const ws = new WebSocket("ws://localhost:8080/ws/connection");
  
      ws.onopen = () => {
        setSocket(ws);
        setConnected(true);
        console.log("Connessione WebSocket stabilita");
      };
  
      ws.onerror = (error) => {
        console.error("Errore WebSocket:", error);
      };
  
      // Pulizia della connessione quando il componente viene smontato
      return () => {
        if (ws) {
          ws.close();
        }
      };
    }, []); // Solo al primo montaggio del componente

  // Gestore per il submit del form
  const handleSubmit = (e) => {
    e.preventDefault();
    // Se la connessione WebSocket è aperta, invia il messaggio
    if (socket && connected) {
        socket.send(`Create:${name}:${gameId}`);
        console.log('Nome:', name);
        console.log('ID Partita:', gameId);
  
        // Reset dei campi dopo il submit (opzionale)
        setName('');
        setGameId('');
      } else {
        console.error("Connessione WebSocket non stabilita!");
      }
    
  };

  return (
    <div>
      <h1>Partecipa a una partita</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Nome:
          <input
            type="text"
            placeholder="Inserisci il tuo nome"
            value={name}
            onChange={(e) => setName(e.target.value)} // Gestisce il cambio di nome
          />
        </label>
        <br />
        <label>
          ID Partita:
          <input
            type="text"
            placeholder="Inserisci ID partita"
            value={gameId}
            onChange={(e) => setGameId(e.target.value)} // Gestisce il cambio di ID
          />
        </label>
        <br />
        <button type="submit">Partecipa</button>
      </form>
    </div>
  );
};

export default JoinGamePage;
