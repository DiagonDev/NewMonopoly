import React, { useContext } from 'react';
import { WebSocketContext } from "../WebSocketContext";
import PlayersStatsRectangle from './PlayersStatsRectangle';
import ChatFather from './ChatFather';
import './GameBoard.css';

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
            {/* <div>
                <div id="altoASinistra">
                    <p>ID Partita: {'Va cambiatooo'}</p>
                </div>
                <div id="bassoASinistra">
                    <ChatFather />
                </div>
            </div>
            <br />
            <div>
                <div id="altoADestra">
                    
                </div>
                <div id="bassoADestra">
                    
                    
                </div>
            </div> */}

        </div>
    );
};

export default GameRectangle;
