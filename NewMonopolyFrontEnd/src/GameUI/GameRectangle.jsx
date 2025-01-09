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
            {isPlayerJoined || userRole == "giocatore" ? (
                <>
                    {/* Contenuto normale quando un giocatore è presente */}
                    <div>
                        <div>
                            <p>PLACEHOLDER</p>
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
                    <p>ID Partita: {gameId}</p>
                    <br />
                    <p>In attesa di un giocatore...</p>
                </div>
            )}
        </div>
    );
};

export default GameRectangle;
