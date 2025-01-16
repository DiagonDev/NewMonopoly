import React, { useContext, useEffect, useState } from 'react';
import GameSquare from "./GameOutside/GameSquare.jsx";
import BaseRectangle from "./actionRectangle/BaseRectangle.jsx";
import RollDice from "./actionRectangle/RollDice.jsx";
import Scambia from "./actionRectangle/Scambia.jsx";
import IpotecaProprieta from "./actionRectangle/IpotecaProrieta.jsx";
import ChatFather from "./ChatFather.jsx";
import PlayersStatsRectangle from "./PlayersStatsRectangle.jsx";
import { WebSocketContext } from "../contexts/WebSocketContext.jsx";
import SelectPawn from "../pages/SelectPawn.jsx";
import {pawnColors} from "../pages/pawnColors.jsx"; // Assicurati di importare correttamente GameSquare

const GameBoard = () => {
    const { socket, connected, diceResult, playerJoin, gameId, startTurn, playerPawn, pawnsAvailable} = useContext(WebSocketContext); // Accesso al WebSocket
    const [isPlayerJoined, setIsPlayerJoined] = useState(false);
    const [activeComponent, setActiveComponent] = useState("BaseRectangle");
    const [pawnSelected, setSelectedPawn] = useState(false);
    const [isMyTurn, setIsMyTurn] = useState(false); // Stato per il turno del giocatore
    const [isGameStarted, setIsGameStarted] = useState(false); // Stato per la partita

    const placeholderPawns = Array.from({length: 6}, (_, i) => ({
        id: i + 1,
        color: pawnColors[i+1] || "gray",
    }));

    const handlePawnSelection = () => {
        setSelectedPawn(true);
    };

    const handleStartGame = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send('InizioPartita:');
            console.log('Messaggio inviato: InizioPartita');

        console.log("Partita avviata.");
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }

    };
    const handleEnd = () => {

        if (socket && connected) {
            // Invia un messaggio al server
            socket.send('FineTurno:');
            console.log('Messaggio inviato: FineTurno');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }

    };

    useEffect(() => {
        // Aggiorna lo stato quando un giocatore entra
        if (playerJoin.player !== "" && playerJoin.playerRole === "giocatore") {
            setIsPlayerJoined(true);
        }
        console.log(isGameStarted);
    }, [playerJoin]);

    //Riceve il messaggio di inizio di un turno
    useEffect(() => {

        if (startTurn && startTurn.flag !== undefined && startTurn.playername.length >0) {
            setActiveComponent("BaseRectangle");
            setIsGameStarted(true);
            setIsMyTurn(startTurn.flag);
        }
    }, [startTurn]);

    // Creare un array di numeri da 1 a 40
    const num_squares = Array.from({ length: 40 }, (_, index) => index + 1);

    return (
        <div className="board">
            {num_squares.map((id) => (
                <GameSquare
                    id={id}
                    key={id}
                    tokens={placeholderPawns} // Passa le pedine alle caselle
                />
            ))}
            <div className="rectangle-top-left">
                {playerJoin.playerRole === "ADMIN" || !isPlayerJoined ? (
                    <div className="shimmer-effect">
                        <p>ID Partita: {gameId}</p>
                        <br />
                        <p>In attesa di un giocatore...</p>
                    </div>
                ) : !pawnSelected ? (
                    <div>
                        <SelectPawn onPawnSelect={handlePawnSelection} />
                    </div>
                ) : (
                    <div className="grid-item">
                        <p>ID Partita: {gameId}</p>
                        <div className="actionDiv">
                            {/* Pulsante "Avvia Partita" visibile solo se la partita non è iniziata */}
                            {!isGameStarted && gameId.length>0 && (
                                <button onClick={handleStartGame}>
                                    Avvia Partita
                                </button>
                            )}

                            {/* Pulsanti di azione disabilitati di default */}
                            {isGameStarted && isMyTurn && (
                                <>
                                    <button
                                        onClick={() => setActiveComponent("RollDice")}
                                        disabled={!isMyTurn} // Disabilitato di default
                                    >
                                        Roll
                                    </button>
                                    <button
                                        onClick={() => setActiveComponent("Scambia")}
                                        disabled={!isMyTurn} // Disabilitato di default
                                    >
                                        Scambia
                                    </button>
                                    <button
                                        onClick={() => setActiveComponent("IpotecaProprieta")}
                                        disabled={!isMyTurn} // Disabilitato di default
                                    >
                                        Ipoteca Proprieta
                                    </button>
                                    <button
                                        onClick={handleEnd}
                                        disabled={!isMyTurn} // Disabilitato di default
                                    >
                                        Termina il turno
                                    </button>
                                </>
                            )}{isGameStarted && !isMyTurn && (
                                <>
                                    <p>
                                        E il turno di {startTurn.playername}
                                    </p>
                                </>
                            )}
                        </div>
                    </div>
                )}
            </div>
            <div className="vertical-line"></div>
            <div className="horizontal-line"></div>
            <div className="rectangle-top-right">
                <div className="grid-item">
                    {activeComponent === "BaseRectangle" && <BaseRectangle />}
                    {activeComponent === "RollDice" && <RollDice />}
                    {activeComponent === "Scambia" && <Scambia />}
                    {activeComponent === "IpotecaProprieta" && <IpotecaProprieta />}
                </div>
            </div>
            <div className="rectangle-bot-left">
                <div className="grid-item">
                    <ChatFather />
                </div>
            </div>
            <div className="rectangle-bot-right">
                <div className="grid-item">
                    <PlayersStatsRectangle />
                </div>
            </div>
        </div>
    );
};

export default GameBoard;
