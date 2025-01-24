import { useState, useEffect } from "react";
import RiceviScambio from "./gestioneProprieta/RiceviScambio";
import { useWebSocket } from "../../websocket/WebSocketProvider.jsx";
import PropertyOwned from "./gestioneProprieta/PropertyOwned.jsx";

const BaseRectangle = ({ playerProperties }) => {
    const { messages } = useWebSocket();
    const [isPageOpen, setIsPageOpen] = useState(false);

    // Trova il messaggio exchangeRequest
    const exchangeRequestMessage = messages.find((msg) => msg.type === "exchangeRequest");

    useEffect(() => {
        if (exchangeRequestMessage?.payload.flag) {
            setIsPageOpen(true);
        }
    }, [exchangeRequestMessage]);

    return (
        <div>
            {isPageOpen ? (
                <RiceviScambio onClose={() => setIsPageOpen(false)} />
            ) : (
                <PropertyOwned playerProperties={playerProperties} />
            )}
        </div>
    );
};

export default BaseRectangle;
