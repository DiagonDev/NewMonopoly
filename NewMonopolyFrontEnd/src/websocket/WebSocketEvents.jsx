export const createWebSocketEvents = (socketRef, setMessages, setIsConnected) => {
    const onOpen = () => setIsConnected(true);
    const onClose = () => setIsConnected(false);
    const onMessage = (event) => {
        try {
            const data = JSON.parse(event.data);
            if (data.type) {
                handleCustomEvent(data.type, data.payload, setMessages);
            } else {
                setMessages((prev) => [...prev, data]);
            }
        } catch (error) {
            console.error("Error parsing WebSocket message", error);
        }
    };

    return { onOpen, onClose, onMessage };
};

const handleChatMessage = (payload, setMessages) => {
    setMessages((prev) => [...prev, { type: "chat", payload }]);
};

const handleSystemMessage = (payload, setMessages) => {
    console.log("System message:", payload);
    setMessages((prev) => [...prev, { type: "system", payload }]);
};
const handleJoinMessage = (payload, setMessages, setPlayerJoin) => {
    setMessages((prev) => [...prev, { type: "join", payload }]);
    setPlayerJoin((prevState) => ({
        ...prevState,
        player: payload.playerName,
        playerRole: payload.userRole,
    }));
};

const handlePongMessage = () => {
    console.log("Received Pong from server");
};

const handleNameBoxMessage = (payload, setNameBox) => {
    setNameBox(payload.name);
};

const handleAllPropertiesMessage = (payload, setAllProperties) => {
    setAllProperties(payload.properties);
};

const handleExchangeRequestMessage = (payload, setExchangeRequest) => {
    setExchangeRequest((prevState) => ({
        ...prevState,
        flag: true,
        property1: payload.property1,
        property2: payload.property2,
        playerName: payload.playerName,
        money: payload.money,
    }));
};

const handleError = (payload) => {
    console.error("WebSocket Error:", payload);
};

const handleGameIdMessage = (payload, setMessages, setGameId) => {
    setGameId(payload.content);
};

const handlePlayersListMessage = (payload, setMessages, setPlayerList) => {
    setPlayerList((prevState) => ({
        ...prevState,
        player: payload.playerName,
        balance: payload.balance,
        pawn: payload.pawn,
    }));
};

const handlePlayerBalanceMessage = (payload, setMessages, setPlayerBalance) => {
    setPlayerBalance((prevState) => ({
        ...prevState,
        player: payload.playerName,
        balance: payload.balance,
    }));
};

const handlePawnsAvailableMessage = (payload, setMessages, setPawnsAvailable) => {
    setPawnsAvailable(payload.content);
};

const handleDiceRolledMessage = (payload, setMessages, setDiceRolled, setDiceRolled2, setPrison, setDiceResult) => {
    if ((payload.dice1 === payload.dice2) && !setPrison) {
        setDiceRolled2(false);
    } else {
        setDiceRolled2(true);
    }
    setDiceRolled(true);
    if (payload.dice1 === payload.dice2) {
        setPrison(false);
    }
    setDiceResult((prevState) => ({
        ...prevState,
        dice1: payload.dice1,
        dice2: payload.dice2,
    }));
};

const handleTurnMessage = (payload, setMessages, setStartTurn, setPayment, setDraw, setBuy) => {
    setStartTurn((prevState) => ({
        ...prevState,
        flag: payload.content,
        playername: payload.playername,
    }));
    setPayment((prevState) => ({ flag: false }));
    setDraw((prevState) => ({ flag: false }));
    setBuy((prevState) => ({ flag: false }));
};

const handlePawnMoveMessage = (payload, setMessages, setPlayerPawn) => {
    console.log("pawnMove", payload.offset);
    setPlayerPawn((prevState) => ({
        ...prevState,
        pawnId: payload.pawnId,
        playerName: payload.playerName,
        offset: payload.offset,
    }));
};

const handleErrorNameMessage = (payload, setMessages, setErrorName) => {
    console.log("errore nome gia esistente");
    setErrorName(2);
};

const handlePaymentMessage = (payload, setMessages, setPayment) => {
    console.log(payload.type);
    setPayment((prevState) => ({
        ...prevState,
        flag: true,
        description: payload.description,
        destination: payload.destination,
        payment: payload.payment,
    }));
};

const handleDrawMessage = (payload, setMessages, setDraw) => {
    console.log(payload.type);
    setDraw((prevState) => ({
        ...prevState,
        flag: true,
        card: payload.card,
        description: payload.description,
    }));
};

const handleBuyMessage = (payload, setMessages, setBuy) => {
    console.log("Acquisto:", payload.type, payload.price);
    setBuy((prevState) => ({
        ...prevState,
        flag: true,
        price: payload.price,
    }));
};

const handleAcquistoRiuscitoMessage = (payload, setMessages, setBuyReturn, setBuy) => {
    setBuyReturn((prevState) => ({
        ...prevState,
        flag: true,
        success: true,
    }));
    setBuy((prevState) => ({
        ...prevState,
        flag: false,
    }));
};

const handleAcquistoFallitoMessage = (payload, setMessages, setBuyReturn, setBuy) => {
    setBuyReturn((prevState) => ({
        ...prevState,
        flag: true,
        success: false,
    }));
    setBuy((prevState) => ({
        ...prevState,
        flag: false,
    }));
};

const handlePrisonMessage = (payload, setMessages, setPrison, setExitPrison) => {
    setPrison(true);
    setExitPrison(0);
};

const handleExitPrisonMessage = (payload, setMessages, setPrison, setExitPrison) => {
    if (payload.flag) {
        setPrison(false);
        setExitPrison(1);
    } else {
        setExitPrison(2);
    }
};

const handlePartitaFinitaMessage = (payload, setMessages, setPartitaFinita) => {
    setPartitaFinita(payload.flag);
};

const handleUpdatePropertiesMessage = (payload, setMessages, setUpdateProperties) => {
    setUpdateProperties(payload.properties);
};

const handleRispostaAggiornaProprietaMessage = (payload, setMessages, setPlayerProperties) => {
    setPlayerProperties(payload.properties);
};

const eventHandlers = {
    chat: handleChatMessage,
    system: handleSystemMessage,
    join: handleJoinMessage,
    pong: handlePongMessage,
    nameBox: handleNameBoxMessage,
    allProperties: handleAllPropertiesMessage,
    exchangeRequest: handleExchangeRequestMessage,
    error: handleError,
    gameId: handleGameIdMessage,
    playersList: handlePlayersListMessage,
    playerBalance: handlePlayerBalanceMessage,
    pawnsAvailable: handlePawnsAvailableMessage,
    diceRolled: handleDiceRolledMessage,
    turn: handleTurnMessage,
    pawnMove: handlePawnMoveMessage,
    errorName: handleErrorNameMessage,
    payment: handlePaymentMessage,
    draw: handleDrawMessage,
    buy: handleBuyMessage,
    acquistoRiuscito: handleAcquistoRiuscitoMessage,
    acquistoFallito: handleAcquistoFallitoMessage,
    prison: handlePrisonMessage,
    exitPrison: handleExitPrisonMessage,
    partitaFinita: handlePartitaFinitaMessage,
    updateProperties: handleUpdatePropertiesMessage,
    RispostaAggiornaProprieta: handleRispostaAggiornaProprietaMessage,
};

export const handleCustomEvent = (type, payload, ...handlers) => {
    const handler = eventHandlers[type];
    if (handler) {
        handler(payload, ...handlers);
    } else {
        console.warn("Unhandled WebSocket event type:", type);
    }
};
