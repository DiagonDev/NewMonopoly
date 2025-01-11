import React, {createContext, useEffect, useState} from "react";

export const WebSocketContext = createContext();

export const WebSocketProvider = ({children}) => {
    const [socket, setSocket] = useState(null);
    const [connected, setConnected] = useState(false);
    const [serverMessages, setServerMessages] = useState([]);
    const [userMessages, setUserMessages] = useState([]);
    const [gameId, setGameId] = useState('');
    const [playerJoin, setPlayerJoin] = useState({
        player: '',
        playerRole: '',
    });
    const [playerBalance, setPlayerBalance] = useState({
        player: '',
        balance: 0,
    });

    useEffect(() => {
        const ws = new WebSocket("https://c3cb-84-33-176-173.ngrok-free.app/ws/gameNewMonopoly");
        //const ws = new WebSocket("ws://localhost:8080/ws/gameNewMonopoly");
        ws.onopen = () => {
            setSocket(ws);
            setConnected(true);
        };

        ws.onmessage = (event) => {
            //console.log("Messaggio dal server:", event.data);
            const message = JSON.parse(event.data);
            /**
             *  Ricevo dal back end un json con:
             *  type: chat / system
             *  content: "messaggio effettivo"
             */
            if (message.type === 'chat') {
                setUserMessages((prevMessages) => {
                    const updatedMessages = [...prevMessages, message.content];
                    return updatedMessages;
                });
            } else if (message.type === 'system') {
                setServerMessages((prevMessages) => {
                    const updatedServerMessages = [...prevMessages, message.content];
                    return updatedServerMessages;
                });
            }
            /**
             * type: join
             * content:playerName
             */
            else if (message.type === 'join') {
                console.log("joinevent" + JSON.stringify(message));
                setPlayerJoin((prevState) => ({
                    ...prevState, // Mantieni le altre proprietà, se esistono
                    player: message.playerName, // Aggiorna il nome del giocatore
                    playerRole: message.userRole, // Aggiorna il bilancio
                }));
            } else if (message.type === 'gameId') {
                setGameId(message.content);
            }
            /**
             * type: balance
             * content: newBalance (int/long)
             */
            else if (message.type === 'balance') {
                setPlayerBalance((prevState) => ({
                    ...prevState, // Mantieni le altre proprietà, se esistono
                    player: message.playerName, // Aggiorna il nome del giocatore
                    balance: message.balance, // Aggiorna il bilancio
                }));
            }
        };

        ws.onerror = (error) => {
            console.error("Errore WebSocket:", error);
        };

        ws.onclose = () => {
            setConnected(false);
        };

        return () => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.close();
            }
        };
    }, []);

    return (
        <WebSocketContext.Provider
            value={{socket, connected, serverMessages, userMessages, gameId, playerBalance, playerJoin}}>
            {children}
        </WebSocketContext.Provider>
    );
};
