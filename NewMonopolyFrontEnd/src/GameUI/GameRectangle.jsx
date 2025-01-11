import React, { useContext, useEffect, useState } from 'react';
import { WebSocketContext } from "../contexts/WebSocketContext";
import PlayersStatsRectangle from './PlayersStatsRectangle';
import ChatFather from './ChatFather';


const GameRectangle = () => {
    const { serverMessages, joinMessage, gameId, userRole } = useContext(WebSocketContext);
    const [isPlayerJoined, setIsPlayerJoined] = useState(false);
    useEffect(() => {
        // Simula l'ascolto di un messaggio WebSocket che indica l'ingresso di un giocatore
        if (joinMessage !== "") {
            setIsPlayerJoined(true); // Aggiorna lo stato quando un giocatore entra
        }
    }, [joinMessage]);
    return (
        <div className="game-rectangle">
            <div class="grid-item">
                <p>ID Partita: {'Va cambiatooo'}</p>
            </div>
            <div class="grid-item">
                <p>alto destra</p>
            </div>
            <div class="grid-item">
                <ChatFather />
            </div>
            <div class="grid-item">
                <PlayersStatsRectangle />
            </div>
            

        </div>
    );
};

export default GameRectangle;
