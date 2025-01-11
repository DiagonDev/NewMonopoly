import React, { useContext, useEffect, useState } from 'react';
import { WebSocketContext } from "../contexts/WebSocketContext";
import PlayersStatsRectangle from './PlayersStatsRectangle';
import ChatFather from './ChatFather';
import RollDice from './actionRectangle/rollDice';
import Scambia from './actionRectangle/scambia';
import IpotecaProprieta from './actionRectangle/ipotecaProrieta';
import BaseRectangle from './actionRectangle/baseRectangle';


const GameRectangle = () => {
    const { joinMessage, gameId, userRole } = useContext(WebSocketContext);
    const [isPlayerJoined, setIsPlayerJoined] = useState(true);
    const [activeComponent, setActiveComponent] = useState("BaseRectangle"); // Stato per gestire il componente attivo

    useEffect(() => {
        // Aggiorna lo stato quando un giocatore entra
        if (joinMessage !== "" && userRole === "giocatore") {
            setIsPlayerJoined(true);
        }
    }, [joinMessage]);

    return (
        <div className="game-rectangle">
            {userRole === "giocatore" || isPlayerJoined ? (
                <>
                    <div className="grid-item">
                        <p>ID Partita: {gameId}</p>
                        <div className='actionDiv'>
                            <button onClick={() => setActiveComponent("RollDice")}>
                                Roll
                            </button>
                            <button onClick={() => setActiveComponent("Scambia")}>
                                Scambia
                            </button>
                            <button onClick={() => setActiveComponent("IpotecaProprieta")}>
                                Ipoteca Proprieta
                            </button>
                        </div>
                    </div>
                    <div className="grid-item">
                        
                        {activeComponent === "BaseRectangle" && <BaseRectangle />}
                        {activeComponent === "RollDice" && <RollDice />}
                        {activeComponent === "Scambia" && <Scambia />}
                        {activeComponent === "IpotecaProprieta" && <IpotecaProprieta />}
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
