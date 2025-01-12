import React, { useContext, useState } from 'react';




const RollDice = () => {

    const [isRolled, setIsRolled] = useState(false);

    // Funzione per gestire il click sul bottone
    const handleRoll = () => {
        setIsRolled(true); // Quando il bottone viene premuto, cambia lo stato
    };

    return (
        <div className='rollDiceDiv'>
            <p id='posizioneAttuale'>Posizione Attuale: Parco della vittoria</p>
            {/* Se isRolled è true, mostra la scritta, altrimenti mostra il bottone */}
            {isRolled ? (
                <div>
                    <p>Hai lanciato i dadi!</p>
                    <p id='posizioneFinale'>Posizione Attuale: Probabilità</p>
                </div>
            ) : (
                <button onClick={handleRoll}>
                    Roll 
                </button>
            )}
        </div>
    );
};

export default RollDice;