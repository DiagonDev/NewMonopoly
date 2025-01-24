import {useState, useEffect} from "react";
import {useWebSocket} from "../websocket/WebSocketProvider.jsx";

const MAX_PLAYERS = 6; // Numero massimo di giocatori
const INITIAL_BALANCE = 0; // Saldo iniziale per ogni giocatore

const PlayersStatsRectangle = () => {

    const [players, setPlayers] = useState(
        Array(MAX_PLAYERS).fill({name: "Player", balance: INITIAL_BALANCE})
    );
    const {messages} = useWebSocket();
    const playerListMessage = messages.find((msg) => msg.type === "playersList");
    const playerBalanceMessage = messages.find((msg) => msg.type === "playerBalance");

    // Aggiunge un nuovo giocatore quando arriva un messaggio di join
    useEffect(() => {
        if (playerListMessage) {
            const {playerName, balance} = playerListMessage.payload;

            setPlayers((prevPlayers) => {
                const nextPlayers = [...prevPlayers];
                const playerIndex = nextPlayers.findIndex((player) => player.name === playerName);

                if (playerIndex === -1) {
                    const emptySlotIndex = nextPlayers.findIndex((player) => player.name === "Player");

                    if (emptySlotIndex !== -1) {
                        nextPlayers[emptySlotIndex] = {name: playerName, balance};
                    }
                }

                return nextPlayers;
            });
        }
    }, [playerListMessage]);

    // Simula la modifica del bilancio quando arriva un messaggio dal server
    useEffect(() => {
        if (playerBalanceMessage) {
            const {playerName, balance} = playerBalanceMessage.payload;
            setPlayers((prevPlayers) => {
                prevPlayers.map((player) =>
                    player.name === playerName
                        ? {...player, balance}
                        : player
                );
            });
        }
    }, [playerBalanceMessage]);

    return (
        <div className="player-container">
            {players.map((player, index) => (
                <div key={index} className="player">
                    <p id={`playerId${index + 1}`}>{player.name}</p>
                    <p id={`playerSaldo${index + 1}`}>{player.balance}€</p>
                </div>
            ))}
        </div>
    );
};

export default PlayersStatsRectangle;
