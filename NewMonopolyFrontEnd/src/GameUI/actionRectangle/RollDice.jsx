import React, {useContext, useEffect, useState} from 'react';
import { WebSocketContext } from '../../contexts/WebSocketContext';
import {pawnColors} from "../../pages/pawnColors.jsx";

const RollDice = () => {
    const { socket, connected, diceResult } = useContext(WebSocketContext); // Accesso al WebSocket
    const [isRolled, setIsRolled] = useState(false);
    //const [currentPosition, setCurrentPosition] = useState('Parco della vittoria'); // Posizione iniziale
   
    const [diceValue, setDiceValue1] = useState(null); // Valore del dado
    const [diceValue2, setDiceValue2] = useState(null); // Valore del dado2

    //const [finalPosition, setFinalPosition] = useState(''); // Posizione dopo il lancio

    // Funzione per gestire il click sul bottone
    const handleRoll = () => {

        if (socket && connected) {
            // Invia un messaggio al server
            socket.send('LanciaDadi:');
            console.log('Messaggio inviato: LanciaDadi');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
        setIsRolled(true);
    };
    useEffect(() => {
        setDiceValue1(diceResult.dice1);
        setDiceValue2(diceResult.dice2);
    }, [diceResult]);


   /* // Ascolta i messaggi dalla WebSocket
    if (socket) {
        socket.onmessage = (message) => {
            try {
                const data = JSON.parse(message.data); // Analizza il messaggio JSON

                // Verifica il tipo di messaggio e aggiorna i valori dei dadi
                if (data.type === 'diceRolled') {
                    setDiceValue1(data.dice1);
                    setDiceValue2(data.dice2);
                    setIsRolled(true); // Aggiorna lo stato per mostrare il risultato
                } else {
                    console.warn('Messaggio non riconosciuto:', data);
                }
            } catch (error) {
                console.error('Errore nell\'analisi del messaggio JSON:', error);
            }
        };
       
    }
*/
    return (
        <div className='rollDiceDiv'>
            <p id='posizioneAttuale'>Posizione Attuale: Pos1</p>
            {/* Se isRolled è true, mostra la scritta con il risultato, altrimenti mostra il bottone */}
            {isRolled ? (
                <div>
                    <p>Hai lanciato i dadi! Hai ottenuto un {diceValue} con il primo dado.</p>
                    <p>Hai lanciato i dadi! Hai ottenuto un {diceValue2} con il secondo dado.</p>
                    <p>Totale: {diceValue+diceValue2}</p>
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
