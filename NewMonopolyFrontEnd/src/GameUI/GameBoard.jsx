import React, {useContext, useEffect, useState} from 'react';
import GameSquare from "./GameOutside/GameSquare.jsx";
import BaseRectangle from "./actionRectangle/BaseRectangle.jsx";
import RollDice from "./actionRectangle/RollDice.jsx";
import Scambia from "./actionRectangle/Scambia.jsx";
import IpotecaProprieta from "./actionRectangle/IpotecaProrieta.jsx";
import ChatFather from "./ChatFather.jsx";
import PlayersStatsRectangle from "./PlayersStatsRectangle.jsx";
import {WebSocketContext} from "../contexts/WebSocketContext.jsx"; // Assicurati di importare correttamente GameSquare

const GameBoard = () => {

    const {playerJoin, gameId} = useContext(WebSocketContext);
    const [isPlayerJoined, setIsPlayerJoined] = useState(true);
    const [activeComponent, setActiveComponent] = useState("BaseRectangle"); // Stato per gestire il componente attivo
    useEffect(() => {
        // Aggiorna lo stato quando un giocatore entra
        if (playerJoin.player !== "" && playerJoin.playerRole === "giocatore") {
            setIsPlayerJoined(true);
        }
    }, [playerJoin]);
    // Creare un array di numeri da 1 a 40
    const num_squares = Array.from({length: 40}, (_, index) => index + 1);

    return (
        <div className="board">
            {num_squares.map((id) => (
                <GameSquare id={id} key={id}/>
            ))}
            <div className="center-square ">
                {playerJoin.playerRole === "giocatore" || isPlayerJoined ? (
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

                            {activeComponent === "BaseRectangle" && <BaseRectangle/>}
                            {activeComponent === "RollDice" && <RollDice/>}
                            {activeComponent === "Scambia" && <Scambia/>}
                            {activeComponent === "IpotecaProprieta" && <IpotecaProprieta/>}
                        </div>

                        <div className="grid-item">
                            <ChatFather/>
                        </div>
                        <div className="grid-item">
                            <PlayersStatsRectangle/>
                        </div>
                    </>
                ) : (
                    <div className="shimmer-effect">
                        {/* Effetto shimmer o messaggio di attesa */}
                        <p>ID Partita: {gameId}</p>
                        <br/>
                        <p>In attesa di un giocatore...</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default GameBoard;
