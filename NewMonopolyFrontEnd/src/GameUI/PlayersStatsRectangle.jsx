import React, { useState, useEffect, useContext } from "react";

let nextId = 0;
const PlayersStatsRectangle = () => {
    const [playersName, setplayersName] = useState([]);
    return (
        
        <div className="player-container">
            <div className="player">
                <p id="playerId1">Player 1</p>
                <p id="playerSaldo1">1000€</p>
            </div>
            <div className="player">
                <p id="playerId2">Player 2</p>
                <p id="playerSaldo2">1000€</p>
            </div>
            <div className="player">
                <p id="playerId3">Player 3</p>
                <p id="playerSaldo3">1000€</p>
            </div>
            <div className="player">
                <p id="playerId4">Player 4</p>
                <p id="playerSaldo2">1000€</p>
            </div>
            <div className="player">
                <p id="playerId5">Player 5</p>
                <p id="playerSaldo5">1000€</p>
            </div>
            <div className="player">
                <p id="playerId6">Player 6</p>
                <p id="playerSaldo6">1000€</p>
            </div>
        </div>
        
    );
}
export default PlayersStatsRectangle;