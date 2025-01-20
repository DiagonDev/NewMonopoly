import React, { useState, useEffect, useContext } from "react";
import { WebSocketContext } from "../contexts/WebSocketContext";

const MAX_PLAYERS = 6; // Numero massimo di giocatori
const INITIAL_BALANCE = 0; // Saldo iniziale per ogni giocatore

const PlayersStatsRectangle = () => {
    const [players, setPlayers] = useState(
        Array(MAX_PLAYERS).fill({ name: "Player", balance: INITIAL_BALANCE })
    );
    const { playerList, playerBalance } = useContext(WebSocketContext);

    // Aggiunge un nuovo giocatore quando arriva un messaggio di join
    useEffect(() => {
        console.log("Nuovo join:", playerList.player);
        if (playerList.player) {
            setPlayers((prevPlayers) => {
                const nextPlayers = [...prevPlayers];
                // Verifica se il giocatore è già presente
                const playerIndex = nextPlayers.findIndex(
                    (player) => player.name === playerList.player
                );

                if (playerIndex === -1) {
                    // Trova il primo slot vuoto (nome "Player")
                    const emptySlotIndex = nextPlayers.findIndex(
                        (player) => player.name === "Player"
                    );

                    if (emptySlotIndex !== -1) {
                        nextPlayers[emptySlotIndex] = {
                            name: playerList.player, // Nome del giocatore dal messaggio
                            balance: playerList.balance,
                        };
                    }
                }
                return nextPlayers;
            });
        }
    }, [playerList]);

    // Simula la modifica del bilancio quando arriva un messaggio dal server
    useEffect(() => {
        if (playerBalance) {
            
            console.log(playerBalance.player, playerBalance.balance);
            setPlayers((prevPlayers) => {
                return prevPlayers.map((player) =>
                    player.name === playerBalance.player
                        ? { ...player, balance: playerBalance.balance }
                        : player
                );
            });
        }
    }, [playerBalance]);

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
