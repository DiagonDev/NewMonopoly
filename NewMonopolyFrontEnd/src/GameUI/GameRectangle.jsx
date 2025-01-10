import React, { useContext } from 'react';
import { WebSocketContext } from "../WebSocketContext";
import PlayersStatsRectangle from './PlayersStatsRectangle';
import ChatFather from './ChatFather';


const GameRectangle = () => {
    const { socket, connected, serverMessage } = useContext(WebSocketContext);
    return (
        <div className="game-rectangle">
            <div class="grid-item">
                <p>ID Partita: {'Va cambiatooo'}</p>
            </div>
            <div class="grid-item">
                <p>alto destra</p>
            </div>
            <div class="grid-item">
                <ChatFather />
            </div>
            <div class="grid-item">
                <PlayersStatsRectangle />
            </div>
            

        </div>
    );
};

export default GameRectangle;
