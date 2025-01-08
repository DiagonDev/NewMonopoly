import React, {useContext } from 'react';
import './GameBoard.css';
import GameOutsideRectangle from './GameOutsideRectangle';
const GameBoard = () => {
  return (
    <div>
      <GameOutsideRectangle />
    </div>
  );
};

export default GameBoard;