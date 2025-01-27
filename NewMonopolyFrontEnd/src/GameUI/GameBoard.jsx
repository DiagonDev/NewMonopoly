import React, {useContext, useEffect, useState} from 'react';
import GameSquare from "./GameOutside/GameSquare.jsx";
import BaseRectangle from "./actionRectangle/BaseRectangle.jsx";
import RollDice from "./actionRectangle/RollDice.jsx";
import GestisciProprieta from "./actionRectangle/GestisciProprieta.jsx";
import ChatFather from "./ChatFather.jsx";
import PlayersStatsRectangle from "./PlayersStatsRectangle.jsx";
import {WebSocketContext} from "../contexts/WebSocketContext.jsx";
import SelectPawn from "../pages/SelectPawn.jsx";
import {BoardSection} from "./GameOutside/BoardSection.jsx";
import {boxPositionMock} from "./boxPositionMock.jsx";

const GameBoard = () => {
    const {
        socket,
        connected,
        playerJoin,
        gameId,
        startTurn,
        playerPawn,
        diceRolled,
        diceRolled2,
        playerProperties,
        boxPosition
    } = useContext(WebSocketContext); // Accesso al WebSocket
    const [isPlayerJoined, setIsPlayerJoined] = useState(false);
    const [activeComponent, setActiveComponent] = useState("BaseRectangle");
    const [pawnSelected, setSelectedPawn] = useState(false);
    const [isMyTurn, setIsMyTurn] = useState(false); // Stato per il turno del giocatore
    const [isGameStarted, setIsGameStarted] = useState(false); // Stato per la partita
    const [isRolled, setIsRolled] = useState(diceRolled);
    const [isRolled2, setIsRolled2] = useState(diceRolled2);

    /**
     * tiene la posizione dei player, quando scelgo una pedina, setto la posizione a 1. Ma dove?
     * Ad esempio se player sceglie pedina green(id 3) playerPositions sarà = [0,0,1,0,0,0]
     */

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
    const handleGestisciProprieta = () => {
        setActiveComponent("GestisciProprieta")
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send('RichiestaUpdateProperties:');
            console.log('Messaggio inviato: GestisciProprieta');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    }
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

    useEffect(() => {
        setIsRolled(diceRolled);
    }, [diceRolled]);

    useEffect(() => {
        setIsRolled2(diceRolled2);
    }, [diceRolled2]);

    //Riceve il messaggio di inizio di un turno
    useEffect(() => {

        if (startTurn && startTurn.flag !== undefined && startTurn.playername.length > 0) {
            setActiveComponent("BaseRectangle");
            setIsGameStarted(true);
            setIsMyTurn(startTurn.flag);
        }
    }, [startTurn]);

    const [shuffled_squares, setShuffled_squares] = useState([]);
    useEffect(() => {
       setShuffled_squares(boxPosition);
    }, [boxPosition]);

    const [playerPositions, setPlayerPositions] = useState([0, 0, 0, 0, 0, 0]); // max 6 giocatori
    // LASCIARE WARNING
    useEffect(() => {
        if (playerPawn) {
            setPlayerPositions((prevPositions) => {
                const newPositions = [...prevPositions];
                newPositions[playerPawn.pawnId - 1] = boxPosition[playerPawn.offset-1];
                return newPositions;
            });
        }
        console.log("playerPositions", playerPositions);
    }, [playerPawn]);

    /**
     * players={playerPositions.map((pos, index) => (pos === id ? index : null))
     *                         .filter((p) => p !== null)}
     * questa funzione itera su playerPositions e controlla che playerPosition[pos] === id(casella)
     * se si ritorna index se no ritorna null
     * .filter poi filtra laddove io ho valori null
     * quindi se io ho [0,0,1,0,0,0] e id = 1 avrò [null, null, 2, null, null, null] che filtrato mi dà [2]
     */
    return (
        <div className="board">
            {shuffled_squares.map((id, index) => (
                <GameSquare
                    id={id}
                    key={id}
                    section={
                        index < 10 ? BoardSection.Bottom :
                            index < 20 ? BoardSection.Left :
                                index < 30 ? BoardSection.Top : BoardSection.Right
                    }
                    players={playerPositions.map((pos, index) => (pos === id ? index : null))
                        .filter((p) => p !== null)}

                />
            ))}
            <div className="rectangle-top-left">
                {playerJoin.playerRole === "ADMIN" || !isPlayerJoined ? (
                    <div className="shimmer-effect">
                        <p>ID Partita: {gameId}</p>
                        <br/>
                        <p>In attesa di un giocatore...</p>
                    </div>
                ) : !pawnSelected ? (
                    <div>
                        <SelectPawn onPawnSelect={handlePawnSelection}/>
                    </div>
                ) : (
                    <div className="grid-item">
                        <p>ID Partita: {gameId}</p>
                        <div className="actionDiv">
                            {/* Pulsante "Avvia Partita" visibile solo se la partita non è iniziata */}
                            {!isGameStarted && gameId.length > 0 && (
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
                                        onClick={handleGestisciProprieta}
                                        disabled={!isMyTurn} // Disabilitato di default
                                    >
                                        Gestisci proprietà
                                    </button>
                                    <button
                                        onClick={handleEnd}
                                        disabled={(!isMyTurn || !isRolled || !isRolled2)} // Disabilitato di default
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
                    {activeComponent === "BaseRectangle" && <BaseRectangle playerProperties={playerProperties}/>}
                    {activeComponent === "RollDice" && <RollDice/>}
                    {activeComponent === "GestisciProprieta" &&
                        <GestisciProprieta playerProperties={playerProperties}/>}
                </div>
            </div>
            <div className="rectangle-bot-left">
                <div className="grid-item">
                    <ChatFather/>
                </div>
            </div>
            <div className="rectangle-bot-right">
                <div className="grid-item">
                    <PlayersStatsRectangle/>
                </div>
            </div>
        </div>
    );
};

export default GameBoard;
