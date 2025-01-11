import React, { useContext, useEffect, useState } from 'react';
import { WebSocketContext } from "../contexts/WebSocketContext";
import PlayersStatsRectangle from './PlayersStatsRectangle';
import ChatFather from './ChatFather';

const GameRectangle = () => {
    const { serverMessages, joinMessage, gameId, userRole } = useContext(WebSocketContext);
    const [isPlayerJoined, setIsPlayerJoined] = useState(false);

    useEffect(() => {
        // Aggiorna lo stato quando un giocatore entra
        if (joinMessage !== "") {
            setIsPlayerJoined(true);
        }
    }, [joinMessage]);

    return (
        <div className="game-rectangle">
            {userRole === "giocatore" || isPlayerJoined ? (
                <>
                    <div className="grid-item">
                        <p>ID Partita: {gameId}</p>
                    </div>
                    <div className="grid-item">
                        <p>alto destra</p>
                    </div>
                    <div className="grid-item">
                        <ChatFather />
                    </div>
                    <div className="grid-item">
                        <PlayersStatsRectangle />
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
