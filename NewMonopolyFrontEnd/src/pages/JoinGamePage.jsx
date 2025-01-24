import { useState, useEffect } from 'react';
import {useWebSocket} from "../websocket/WebSocketProvider.jsx";
import { useNavigate } from 'react-router-dom';
import ErrorModal from '../modals/ErrorModal';
const JoinGamePage = () => {
  const { isConnected, sendMessage, messages } = useWebSocket();
  // Stato per i campi del form
  const [name, setName] = useState('');
  const [gameId, setGameId] = useState('');
  const [submit, setSubmit] =useState(false);
  const navigate = useNavigate();
   const [isPageOpen, setIsPageOpen] = useState(false);

  const errorNameMessage = messages.find((msg) => msg.type === "errorName");


  // Gestore per il submit del form
  const handleSubmit = (e) => {
    e.preventDefault();
    if (isConnected) {
      setSubmit(true);
      sendMessage({ type: "joinGame", name, gameId });
      console.log("Nome:", name);
      console.log("ID Partita:", gameId);

      // Reset dei campi
      setName("");
      setGameId("");
    } else {
      console.error("Connessione WebSocket non stabilita!");
    }

  };

    useEffect(() => {
        if (errorNameMessage && submit) {
            if (errorNameMessage.payload === 1) {
                navigate("/play");
            } else if (errorNameMessage.payload === 2) {
                setIsPageOpen(true);
            }
        }
    }, [errorNameMessage, submit, navigate]);

  return (
    <>
      {isPageOpen ? (
        <ErrorModal
          message="player name gia esistente"
          onClose={() => setIsPageOpen(false)}
        />
      ) : (
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
      )}

    </>
  );
};

export default JoinGamePage;
