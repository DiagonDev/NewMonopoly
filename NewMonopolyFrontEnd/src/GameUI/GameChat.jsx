import React, { useContext, useState } from 'react';
import { WebSocketContext } from "../WebSocketContext";



const GameChat = () => {
    const { socket, connected, serverMessage, userMessages } = useContext(WebSocketContext);
    const [userMessage, setUserMessage] = useState('');


    const handleSubmit = (e) => {
        e.preventDefault();

        if (socket && connected) {
            socket.send(`MessaggioUtente:${userMessage}`);
            console.log('Dati inviati al server:', { userMessage });

            setUserMessage('');
        } else {
            console.error("Connessione WebSocket non stabilita!");
        }
    };

    return (
        <div>
            <textarea
                value={Array.isArray(userMessages) ? userMessages.join("\n") : ""}
                readOnly
                rows={10}
                cols={10}
            />
            <br />
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
