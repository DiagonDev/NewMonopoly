import React, { useState, useEffect } from 'react';

const CreateGamePage = () => {
  // Stato per i campi del form
  const [userName, setUserName] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [randomization, setRandomization] = useState('');
  const [connected, setConnected] = useState(false);
  const [socket, setSocket] = useState(null);
   const [serverMessage, setServerMessage] = useState('');

  // Crea una connessione WebSocket quando il componente viene montato
  useEffect(() => {
    //const ws = new WebSocket("ws://localhost:8080/ws/connection");
    const ws = new WebSocket("https://89d1-84-33-176-173.ngrok-free.app/ws/connection");
    ws.onopen = () => {
      setSocket(ws);
      setConnected(true);
      console.log("Connessione WebSocket stabilita");
    };

    // Gestore per ricevere i messaggi
    ws.onmessage = (event) => {
      console.log(event.data)
      // Imposta il messaggio ricevuto dallo server nello stato
      setServerMessage(event.data);
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
      socket.send(`Create:${userName}:${difficulty}:${randomization}`);
      console.log('Difficoltà:', difficulty);
      console.log('Randomizzazione:', randomization);

      // Reset dei campi dopo il submit (opzionale)
      setUserName('');
      setDifficulty('');
      setRandomization('');
    } else {
      console.error("Connessione WebSocket non stabilita!");
    }
  };

  return (
    <div>
      <h1>Crea una nuova partita</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Nome Utente:
          <input
            type="text"
            placeholder="Inserisci Nome"
            value={userName}
            onChange={(e) => setUserName(e.target.value)}
          />
        </label>
        <br />
        <label>
          Difficoltà:
          <input
            type="text"
            placeholder="Inserisci difficoltà"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)}
          />
        </label>
        <br />
        <label>
          Randomizzazione caselle:
          <input
            type="text"
            placeholder="Inserisci randomizzazione"
            value={randomization}
            onChange={(e) => setRandomization(e.target.value)}
          />
        </label>
        <br />
        <button type="submit">Crea</button>
      </form>
      {serverMessage && <p>Messaggio dal server: {serverMessage}</p>}
    </div>
  );
};

export default CreateGamePage;
