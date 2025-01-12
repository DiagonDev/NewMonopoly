import React, {useContext } from 'react';
//import './GameBoard.css';
import GameOutsideRectangle from './GameOutside/GameOutsideRectangle.jsx';
const GameBoard = () => {
  return (
    <div id='gameBoardDiv'>
      <GameOutsideRectangle />
    </div>
  );
};

export default GameBoard;