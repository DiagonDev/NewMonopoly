import React, {createContext, useEffect, useState} from "react";

export const WebSocketContext = createContext();

// eslint-disable-next-line react/prop-types
export const WebSocketProvider = ({children}) => {
    const [socket, setSocket] = useState(null);
    const [connected, setConnected] = useState(false);
    const [serverMessages, setServerMessages] = useState([]);
    const [userMessages, setUserMessages] = useState([]);
    const [gameId, setGameId] = useState( '');
    const [playerJoin, setPlayerJoin] = useState({
        player: '',
        playerRole: '',
    });
    const [statsPlayer, setStatsPlayer]= useState({
        playerName: '',
        balance: '',
    });
    const [playerBalance, setPlayerBalance] = useState({
        player: '',
        balance: 0,
    });
    const [pawnsAvailable, setPawnsAvailable] = useState([]);
    const [diceResult, setdiceResult] = useState({
        dice1: 0,
        dice2: 0,
    });
    const [diceRolled, setDiceRolled] = useState(false);

    const [startTurn, setStartTurn] = useState({
        flag: false,
        playername: ''
    });

    const [playerPawn, setPlayerPawn] = useState({
        pawnId: 0,
        playerName: 0,
        offset: 0,
    })
    const [payment, setPayment] = useState({
        flag: false,
        description: '',
        destination: '',
        payment: 0,
    })
    const [draw, setDraw] = useState({
        flag: false,
        card: '',
        description: '',
    })
    const [buy, setBuy] = useState({
        flag: false,
        price: 0,
    })
    //Casella in cui finisco dopo il tiro dei dadi
    const [nameBox, setNameBox] = useState('');
    const [playerProperties, setPlayerProperties] = useState([])
    useEffect(() => {
        const ws = new WebSocket("https://7559-84-33-128-253.ngrok-free.app/ws/gameNewMonopoly");
        //const ws = new WebSocket("ws://localhost:8080/ws/gameNewMonopoly");
        ws.onopen = () => {
            setSocket(ws);
            setConnected(true);

            // if (gameId && playerJoin) {
            //     ws.send(`RECONNECT:${playerJoin}:${gameId}`);
            //     console.log(`Tentativo di riconnessione per ${playerJoin} nel gioco ${gameId}`);
            // }

            const pingInterval = setInterval(() => {
                if (ws.readyState === WebSocket.OPEN) {
                    ws.send("Ping:");

                }
            }, 30000);  // Ogni 30 secondi

            // Pulizia dell'intervallo al momento della chiusura del WebSocket
            ws.onclose = () => {
                console.log("Connessione WebSocket chiusa");
                clearInterval(pingInterval); // Pulisci l'intervallo quando il socket si chiude
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
                } else if (message.type === 'pong') {
                    console.log("Ricevuto Pong dal server");
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
                        playerRole: message.userRole,
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
                /**
                 * type: pawnsAvailable
                 * content : array di int[6]
                 */
                else if (message.type === 'pawnsAvailable') {
                    console.log("pawns available", message.content);
                    setPawnsAvailable(message.content);
                } else if (message.type === 'diceRolled') {
                    console.log("diceRolled", message.dice1, message.dice2);
                    setdiceResult((prevState) => ({
                        ...prevState, // Copia il vecchio stato
                        dice1: message.dice1, // Aggiorna solo dice1
                        dice2: message.dice2, // Aggiorna solo dice2
                    }));
                    setDiceRolled(true);
                } else if (message.type === 'turn') {

                    setStartTurn((prevState) => ({
                        ...prevState, // Copia il vecchio stato
                        flag: message.content, // Aggiorna solo content
                        playername: message.playername, // Aggiorna solo playername
                    }));
                    setDiceRolled(false);
                    //Imposta tutte le azioni a false 
                    //Cosi quando termino il turno mi scompare dalla schermata roll la casella in cui ero capitato e le sue informazioni
                    setPayment((prevState) => ({
                        ...prevState,
                        flag: false,
                    }));
                    setDraw((prevState) => ({
                        ...prevState,
                        flag: false,
                    }));
                    setBuy((prevState) => ({
                        ...prevState,
                        flag: false,
                    }));
                } else if (message.type === 'pawnMove') {
                    console.log("pawnMove", message.offset);
                    setPlayerPawn((prevState) => ({
                        ...prevState,
                        pawnId: message.pawnId,
                        playerName: message.playerName,
                        offset: message.offset,
                    }));
                } else if (message.type === 'payment') {
                    console.log(message.type);
                    setPayment((prevState) => ({
                        ...prevState,
                        flag: true,
                        description: message.description,
                        destination: message.destination,
                        payment: message.payment,
                    }));

                } else if (message.type === 'draw') {
                    console.log(message.type);
                    setDraw((prevState) => ({
                        ...prevState,
                        flag: true,
                        card: message.card,
                        description: message.description,
                    }));
                } else if (message.type === 'buy') {
                    console.log("Acquisto: ", message.type," price",);
                    setBuy((prevState) => ({
                        ...prevState,
                        flag: true,
                        price: message.price,
                    }));

                } else if (message.type === 'nameBox') {
                    
                    setNameBox(message.name);
                } else if (message.type === 'propertiesOwned'){
                    setPlayerProperties(message.properties);

                } else if (message.type === 'statsPlayer'){
                    console.log("statsPlayer",message.playerName,message.balance);
                    setBuy((prevState) => ({
                        ...prevState,
                        playerName: message.playerName,
                        balance: message.balance,
                    }));
                }
            };

            ws.onerror = (error) => {
                console.error("Errore WebSocket:", error);
            };

            ws.onclose = () => {
                setConnected(false);
            };
        }
        return () => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.close();
            }
        };
    }, []);


    return (
        <WebSocketContext.Provider
            value={{
                socket,
                connected,
                serverMessages,
                userMessages,
                gameId,
                playerBalance,
                playerJoin,
                pawnsAvailable,
                diceResult,
                startTurn,
                diceRolled,
                playerPawn,
                payment,
                draw,
                buy,
                nameBox,
                playerProperties
            }}>
            {children}
        </WebSocketContext.Provider>
    );
};
