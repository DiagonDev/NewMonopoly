import React, { useState } from 'react';

const CreateGamePage = () => {
  // Stato per i campi del form
  const [difficulty, setDifficulty] = useState('');
  const [randomization, setRandomization] = useState('');

  // Gestore per il submit del form
  const handleSubmit = (e) => {
    e.preventDefault();
    // Qui puoi aggiungere la logica per creare una nuova partita, come una richiesta API.
    console.log('Difficoltà:', difficulty);
    console.log('Randomizzazione:', randomization);
    // Reset dei campi dopo il submit (opzionale)
    setDifficulty('');
    setRandomization('');
  };

  return (
    <div>
      <h1>Crea una nuova partita</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Difficoltà:
          <input
            type="text"
            placeholder="Inserisci difficoltà"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)} // Gestisce il cambio della difficoltà
          />
        </label>
        <br />
        <label>
          Randomizzazione caselle:
          <input
            type="text"
            placeholder="Inserisci randomizzazione"
            value={randomization}
            onChange={(e) => setRandomization(e.target.value)} // Gestisce il cambio della randomizzazione
          />
        </label>
        <br />
        <button type="submit">Crea</button>
      </form>
    </div>
  );
};

export default CreateGamePage;
