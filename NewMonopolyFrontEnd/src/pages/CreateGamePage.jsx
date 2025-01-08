import React, { useState, useContext } from 'react';
import { WebSocketContext } from "../WebSocketContext";

const CreateGamePage = () => {
   const { socket, connected, serverMessage } = useContext(WebSocketContext);
  const [userName, setUserName] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [randomization, setRandomization] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();

    if (socket && connected) {
      socket.send(`Create:${userName}:${difficulty}:${randomization}`);
      console.log('Dati inviati al server:', { userName, difficulty, randomization });

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
      {/* Mostra il messaggio ricevuto dal server */}
      {serverMessage && <p>Messaggio dal server: {serverMessage}</p>}
    </div>
  );
};

export default CreateGamePage;
