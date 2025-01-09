import React, { useContext } from 'react';
import { WebSocketContext } from "../WebSocketContext";
import GameChat from './GameChat';
import PlayersStatsRectangle from './PlayersStatsRectangle';


const GameRectangle = () => {
    const { socket, connected, serverMessage } = useContext(WebSocketContext);
    return (
        <div className="game-rectangle">
            <div>
                <div>
                    <p>ID Partita: {'Va cambiatooo'}</p>
                </div>
                <div>
                    <GameChat />
                </div>
            </div>
            <br />
            <div>
                <div>
                    <p>alto destra</p>
                </div>
                <div>
                    <PlayersStatsRectangle />
                </div>
            </div>

        </div>
    );
};

export default GameRectangle;
