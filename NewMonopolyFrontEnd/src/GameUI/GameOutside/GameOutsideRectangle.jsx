import React from 'react';
import GameRectangle from '../GameRectangle.jsx';
import GameSquare from "./GameSquare"; // Assicurati di importare correttamente GameSquare

const GameOutsideRectangle = () => {
    // Creare un array di numeri da 1 a 40
    const num_squares = Array.from({length: 40}, (_, index) => index + 1);

    return (
        <div className="board">
            {num_squares.map((id) => (
                <GameSquare id={id} key={id}/>
            ))}
            <div className="center-square square">
                <GameRectangle/>
            </div>
        </div>
    );
};

export default GameOutsideRectangle;
