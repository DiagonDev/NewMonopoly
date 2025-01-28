import React, { useState, useContext, useEffect } from 'react';
import { WebSocketContext } from "../../eventListener/WebSocketContext.jsx";
import { useNavigate } from 'react-router-dom';
import ErrorModal from '../modals/ErrorModal.jsx';
const JoinGamePage = () => {
  const { socket, connected, serverMessage, errorName} = useContext(WebSocketContext);
  // Stato per i campi del form
  const [name, setName] = useState('');
  const [gameId, setGameId] = useState('');
  const [submit, setSubmit] =useState(false);
  const navigate = useNavigate();
   const [isPageOpen, setIsPageOpen] = useState(false);
  
      
              

  // Gestore per il submit del form
  const handleSubmit = (e) => {
    e.preventDefault();
    // Se la connessione WebSocket è aperta, invia il messaggio
    if (socket && connected) {
      setSubmit(true);
      socket.send(`Partecipa:${name}:${gameId}`);
      console.log('Nome:', name);
      console.log('ID Partita:', gameId);
      // Reset dei campi dopo il submit (opzionale)
      setName('');
      setGameId('');
      
    } else {
      console.error("Connessione WebSocket non stabilita!");
    }
    
  };
  
  useEffect(() => {
    
    if(errorName===1 && submit){
     
      navigate('/play');
    }else  if(errorName===2 && submit){
      
      setIsPageOpen(true);
    }
    
  }, [errorName]);

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
          {/* Mostra il messaggio ricevuto dal server */}
          {serverMessage && <p>Messaggio dal server: {serverMessage}</p>}
        </div>
      )}
      
    </>
  );
};

export default JoinGamePage;
