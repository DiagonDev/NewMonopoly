import {useState, useEffect} from "react";
import {useWebSocket} from "../websocket/WebSocketProvider";
import {useNavigate} from "react-router-dom";
import {pawnColors} from "./pawnColors.jsx";

// eslint-disable-next-line react/prop-types
const SelectPawn = ({onPawnSelect}) => {
    const {isConnected, sendMessage, messages} = useWebSocket();
    const [availablePawns, setAvailablePawns] = useState([]); // Pedine disponibili
    const [selectedPawn, setSelectedPawn] = useState(null); // Pedina selezionata
    const navigate = useNavigate();

    const pawnsMessage = messages.find((msg) => msg.type === "pawnsAvailable");

    const handleSelectPawn = (pawn) => {
        setSelectedPawn(pawn);
        onPawnSelect();
        console.log("pedina mandata:", pawn.id);
        if (isConnected) {
            sendMessage({type: "selectPawn", pawnId: pawn.id});
            navigate("/play");
        } else {
            console.error("Connessione WebSocket non stabilita!");
        }
    };

    useEffect(() => {
        if (pawnsMessage) {
            const mappedPawns = pawnsMessage.payload.map((id) => ({
                id,
                color: pawnColors[id] || "gray", // Usa "gray" come fallback per ID sconosciuti
            }));
            setAvailablePawns(mappedPawns);
        }
    }, [pawnsMessage]);

    return (
        <div className="pawn-selection">
            <h3>Seleziona la tua pedina</h3>
            <div className="pawns-container">
                {(availablePawns.length !== 0 ? availablePawns : null).map((pawn) => (
                    <div
                        key={pawn.id}
                        className={`pawn ${selectedPawn?.id === pawn.id ? "selected" : ""}`}
                        style={{backgroundColor: pawn.color}}
                        onClick={() => handleSelectPawn(pawn)}
                    >
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
