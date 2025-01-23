import React, {createContext, useEffect, useState} from "react";

export const WebSocketContext = createContext();

// eslint-disable-next-line react/prop-types
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
    const [errorName, setErrorName] = useState(0);
    const [playerBalance, setPlayerBalance] = useState({
        player: '',
        balance: 0,
    });
    const [playerList, setPlayerList] = useState({
        player: '',
        balance: 0,
        pawn: 0,
    });
    
    const [pawnsAvailable, setPawnsAvailable] = useState([]);
    const [diceResult, setdiceResult] = useState({
        dice1: 0,
        dice2: 0,
    });
    const [diceRolled, setDiceRolled] = useState(false);
    const [diceRolled2, setDiceRolled2] = useState(false);

    const [startTurn, setStartTurn] = useState({
        flag: false,
        playername: ''
    });
    const [prison, setPrison] = useState(false);
    const [exitPrison, setExitPrison] = useState(0);

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
    const [buyReturn, setBuyReturn] = useState({
        flag:false,
        success: false,
    })
    //Casella in cui finisco dopo il tiro dei dadi
    const [nameBox, setNameBox] = useState('');
    const [playerProperties, setPlayerProperties] = useState([]);
    const [allProperties, setAllProperties] = useState([]);
    const [exchangeRequest, setExchangeRequest] = useState({
        flag: false,
        property1: '',
        property2: '',
        playerName: '',
        money: 0,
    });
    const [partitaFinita, setPartitaFinita] = useState('');
    const [updateProperties, setUpdateProperties] = useState([]);
    useEffect(() => {
        const ws = new WebSocket("https://c2be-84-33-128-253.ngrok-free.app/ws/gameNewMonopoly");
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
                    setErrorName(1);
                    console.log("joinevent" + JSON.stringify(message));
                    setPlayerJoin((prevState) => ({
                        ...prevState, // Mantieni le altre proprietà, se esistono
                        player: message.playerName, // Aggiorna il nome del giocatore
                        playerRole: message.userRole,
                    }));

                } else if (message.type === 'gameId') {
                    setGameId(message.content);
                }

                else if (message.type === 'playersList') {
                    console.log("playerList", message.playerName, message.balance);
                    setPlayerList((prevState) => ({
                        ...prevState, // Mantieni le altre proprietà, se esistono
                        player: message.playerName, // Aggiorna il nome del giocatore
                        balance: message.balance, // Aggiorna il bilancio
                        pawn: message.pawn,
                    }));
                }

                /**
                 * type: balance
                 * content: newBalance (int/long)
                 */
                else if (message.type === 'playerBalance') {
                    

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
                    setBuy((prevState) => ({
                        ...prevState, // Copia il vecchio stato
                        flag: false, // Aggiorna solo dice1  
                    }));
                    setPayment((prevState) => ({
                        ...prevState, // Copia il vecchio stato
                        flag: false, // Aggiorna solo dice1  
                    }));
                    setDraw((prevState) => ({
                        ...prevState, // Copia il vecchio stato
                        flag: false, // Aggiorna solo dice1  
                    }));
                    if((message.dice1===message.dice2)&&(!prison)){
                        setDiceRolled2(false);
                    }else {
                        setDiceRolled2(true);
                    }
                    setDiceRolled(true);
                    if(message.dice1===message.dice2){
                        setPrison(false);
                    }
                    console.log("diceRolled", message.dice1, message.dice2);
                    setdiceResult((prevState) => ({
                        ...prevState, // Copia il vecchio stato
                        dice1: message.dice1, // Aggiorna solo dice1
                        dice2: message.dice2, // Aggiorna solo dice2
                    }));
                    
                } else if (message.type === 'turn') {
                    setExitPrison(0);
                    setBuyReturn((prevState) => ({
                        ...prevState,
                        flag: false,
                        success: false,
                    }));
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
                }else if (message.type === 'errorName') {
                    console.log("errore nome gia esistente");
                    setErrorName(2);
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
                    console.log("Acquisto: ", message.type, message.price,);
                    setBuy((prevState) => ({
                        ...prevState,
                        flag: true,
                        price: message.price,
                    }));

                } else if (message.type === 'nameBox') {

                    setNameBox(message.name);
                } else if (message.type === 'propertiesOwned') {
                    setPlayerProperties(message.properties);

                }else if (message.type === 'allProperties') {
                    setAllProperties(message.properties);
                }else if (message.type === 'RispostaAggiornaProprieta'){
                    setPlayerProperties(message.properties);
                }else if (message.type === 'exchangeRequest') {
                    console.log("exchangeRequest", message);
                    setExchangeRequest((prevState) => ({
                        ...prevState,
                        flag : true,
                        property1: message.property1,
                        property2: message.property2,
                        playerName: message.playerName,
                        money: message.money,
                    }));
                }
                else if (message.type === 'acquistoRiuscito') {
                    setBuyReturn((prevState) => ({
                        ...prevState,
                        flag: true,
                        success: true,
                    }));
                    setBuy((prevState) => ({
                        ...prevState,
                        flag: false,
                    }));
                    
                }
                else if (message.type === 'acquistoFallito') {
                    setBuyReturn((prevState) => ({
                        ...prevState,
                        flag: true,
                        success: false,
                    }));
                    setBuy((prevState) => ({
                        ...prevState,
                        flag: false,
                    }));
                }else if (message.type === 'prison') {
                    setPrison(true);
                    setExitPrison(0);
                }else if (message.type === 'exitPrison') {
                    if(message.flag){
                        setPrison(false);
                        setExitPrison(1);
                    }else{
                        setExitPrison(2);
                    }
                }else if(message.type === 'partitaFinita'){
                    setPartitaFinita(message.flag);
                }else if(message.type === 'updateProperties'){
                    setUpdateProperties(message.properties);
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
                diceRolled2,
                playerPawn,
                payment,
                draw,
                buy,
                nameBox,
                playerProperties,
                allProperties,
                exchangeRequest,
                playerList,
                buyReturn,
                prison,
                exitPrison,
                partitaFinita,
                updateProperties,
                errorName
            }}>
            {children}
        </WebSocketContext.Provider>
    );
};
