import React, { useContext, useState,useRef, useEffect } from 'react';
import { WebSocketContext } from "../../contexts/WebSocketContext";



const GameChat = () => {
    const { socket, connected, serverMessage, userMessages } = useContext(WebSocketContext);
    const [userMessage, setUserMessage] = useState('');
    const chatWindowRef = useRef(null); // Creazione del riferimento

    // Funzione per scrollare in basso
    const scrollToBottom = () => {
        if (chatWindowRef.current) {
            chatWindowRef.current.scrollTop = chatWindowRef.current.scrollHeight+10;
        }
    };
    scrollToBottom();
    const handleSubmit = (e) => {
        e.preventDefault();

        if (socket && connected) {
            socket.send(`MessaggioUtente:${userMessage}`);
            console.log('Dati inviati al server:', { userMessage });

            setUserMessage('');
            scrollToBottom();
        } else {
            console.error("Connessione WebSocket non stabilita!");
        }
    };
    useEffect(() => {
        scrollToBottom();        
    }, [userMessage]);
    

    return (
        <div>
            <textarea className='chat-window'
                ref={chatWindowRef} // Collegamento del riferimento
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
                    <button type="submit">Invia</button>
                </label>
                
            </form>
        </div>
    );
};

export default GameChat;
