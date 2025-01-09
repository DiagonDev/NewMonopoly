import React, { useContext, useEffect, useState } from 'react';
import { WebSocketContext } from "../WebSocketContext";
import PlayersStatsRectangle from './PlayersStatsRectangle';
import ChatFather from './ChatFather';


const GameRectangle = () => {
    const { joinMessage } = useContext(WebSocketContext);
    const [isPlayerJoined, setIsPlayerJoined] = useState(false);
    const [gameId, setGameId] = useState('CAMBIARE');

    useEffect(() => {
        // Simula l'ascolto di un messaggio WebSocket che indica l'ingresso di un giocatore
        if (joinMessage !== "") {
          setIsPlayerJoined(true); // Aggiorna lo stato quando un giocatore entra
        }
      }, [joinMessage]);
    return (
    <div className="game-rectangle">
      {!isPlayerJoined ? (
        <>
          {/* Contenuto normale quando un giocatore è presente */}
          <div>
            <div>
              <p>ID Partita: {gameId}</p>
            </div>
            <div>
              <ChatFather />
            </div>
          </div>
          <br />
          <div>
            <div>
              <p>alto destra</p>
            </div>
            <div>
              <PlayersStatsRectangle />
            </div>
          </div>
        </>
      ) : (
        <div className="shimmer-effect">
          {/* Effetto shimmer o messaggio di attesa */}
          <p>In attesa di un giocatore...</p>
        </div>
      )}
    </div>
  );
};

export default GameRectangle;
