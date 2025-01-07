import { useParams } from 'react-router-dom';

const GameBoard = () => {
  const { gameId } = useParams();

  return (
    <div>
      <h1>Board di Monopoly</h1>
      <p>ID Partita: {gameId}</p>
      {/* Aggiungi qui la logica per mostrare la board */}
    </div>
  );
};

export default GameBoard;