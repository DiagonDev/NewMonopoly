import React, { useContext, useState } from 'react';
import { WebSocketContext } from "../WebSocketContext";



const GameChat = () => {
    const { socket, connected, serverMessage } = useContext(WebSocketContext);
    const [userMessage, setUserMessage] = useState('');
    const gameId = serverMessage;
    const handleSubmit = (e) => {
        e.preventDefault();

        if (socket && connected) {
            socket.send(`MessaggioUtente:${userMessage}:${gameId}`);
            console.log('Dati inviati al server:', { userMessage, gameId });

            setUserMessage('');
        } else {
            console.error("Connessione WebSocket non stabilita!");
        }
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <label>
                    Chatta:
                    <input
                        type="text"
                        placeholder="Scrivi un messaggio a tutti"
                        value={userMessage}
                        onChange={(e) => setUserMessage(e.target.value)}
                    />
                </label>
                <button type="submit">Invia</button>
            </form>
        </div>
    );
};

export default GameChat;
