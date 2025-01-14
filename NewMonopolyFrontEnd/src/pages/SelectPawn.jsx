import React, {useContext, useState, useEffect} from "react";
import {WebSocketContext} from "../contexts/WebSocketContext.jsx";
import {useNavigate} from "react-router-dom";
import {pawnColors} from "./pawnColors.jsx";

const SelectPawn = () => {
    const {socket, playerJoin, gameId, pawnsAvailable} = useContext(WebSocketContext);
    const [isPlayerJoined, setIsPlayerJoined] = useState(false);
    const [availablePawns, setAvailablePawns] = useState([]); // Pedine disponibili
    const [selectedPawn, setSelectedPawn] = useState(null); // Pedina selezionata
    const navigate = useNavigate();

    const handleSelectPawn = (pawn) => {
        setSelectedPawn(pawn);
        // Invia la scelta al back-end
        socket.send('Selected Pawn:${pawn.id}');
        navigate('/play')
    };

    useEffect(() => {
        // Aggiorna lo stato quando un giocatore entra
        if (playerJoin.player !== "" && playerJoin.playerRole === "giocatore") {
            setIsPlayerJoined(true);
        }
    }, [playerJoin]);

    useEffect(() => {

        const mappedPawns = pawnsAvailable.map((id) => ({
            id,
            color: pawnColors[id] || "gray", // Usa "gray" come fallback per ID sconosciuti
        }));
        setAvailablePawns(mappedPawns);
    }, [pawnsAvailable]);
    const placeholderPawns = Array.from({ length: 4 }, (_, i) => ({
        id: `#${i + 1}`,
        color: "gray",
    }));
    return (
        <>
            {playerJoin.playerRole === "giocatore" || isPlayerJoined ? (

                    <div className="pawn-selection">
                        <h3>Seleziona la tua pedina</h3>
                        <div className="pawns-container">
                            {(availablePawns.length !== 0 ? availablePawns : placeholderPawns).map((pawn) => (
                                <div
                                    key={pawn.id}
                                    className={`pawn ${selectedPawn?.id === pawn.id ? "selected" : ""}`}
                                    style={{backgroundColor: pawn.color}}
                                    onClick={() => handleSelectPawn(pawn)}
                                >
                                    <span className="pawn-id">{pawn.id}</span>
                                </div>
                            ))}
                        </div>
                        {selectedPawn && (
                            <p>
                                Hai selezionato la pedina <span style={{color: selectedPawn.color}}>{selectedPawn.id}</span>
                            </p>
                        )}
                    </div>
                )
                : (
                    <div className="shimmer-effect">
                        {/* Effetto shimmer o messaggio di attesa */}
                        <p>ID Partita: {gameId}</p>
                        <br/>
                        <p>In attesa di un giocatore...</p>
                    </div>
                )}
        </>
    );
}
export default SelectPawn