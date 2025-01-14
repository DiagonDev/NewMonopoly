import React, {useContext, useState, useEffect} from "react";
import {WebSocketContext} from "../contexts/WebSocketContext.jsx";
import {useNavigate} from "react-router-dom";
import {pawnColors} from "./pawnColors.jsx";

// eslint-disable-next-line react/prop-types
const SelectPawn = ({ onPawnSelect }) => {
    const {socket, pawnsAvailable} = useContext(WebSocketContext);
    const [availablePawns, setAvailablePawns] = useState([]); // Pedine disponibili
    const [selectedPawn, setSelectedPawn] = useState(null); // Pedina selezionata
    const navigate = useNavigate();

    const handleSelectPawn = (pawn) => {
        setSelectedPawn(pawn);
        onPawnSelect(); // Notifica il genitore che una pedina è stata selezionata
        console.log("pedina mandata:", pawn.id);
        // Invia la scelta al back-end
        socket.send(`SceltaPedina:${pawn.id}`);
        navigate('/play');
    };

    useEffect(() => {
        const mappedPawns = pawnsAvailable.map((id) => ({
            id,
            color: pawnColors[id] || "gray", // Usa "gray" come fallback per ID sconosciuti
        }));
        setAvailablePawns(mappedPawns);
    }, [pawnsAvailable]);

    const placeholderPawns = Array.from({length: 4}, (_, i) => ({
        id: i + 1,
        color: "gray",
    }));

    return (
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
    );
};

export default SelectPawn;
