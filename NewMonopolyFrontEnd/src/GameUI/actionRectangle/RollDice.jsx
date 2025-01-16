import React, {useContext, useEffect, useState} from 'react';
import { WebSocketContext } from '../../contexts/WebSocketContext';
import {pawnColors} from "../../pages/pawnColors.jsx";

const RollDice = () => {
    const { socket, connected, diceResult, diceRolled, payment, draw, buy } = useContext(WebSocketContext); // Accesso al WebSocket
    const [isRolled, setIsRolled] = useState(diceRolled);
    //const [currentPosition, setCurrentPosition] = useState('Parco della vittoria'); // Posizione iniziale
   
    const [diceValue, setDiceValue1] = useState(null); // Valore del dado
    const [diceValue2, setDiceValue2] = useState(null); // Valore del dado2
    
    //
    const [isPayment, setIsPayment] = useState(payment.flag);
    const [isDraw, setIsDraw] = useState(draw.flag);
    const [isBuy, setIsBuy] = useState(buy.flag);
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
    
    //Da implementare che la schermata si blocca quando posso acquistare finche non scelgo un opzione
    const handleAcquista = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send('AcquistaProprieta:');
            console.log('Messaggio inviato: Proprieta acquistata');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };

    //Quello che fa quando non acquista la proprietà, per ora nulla
    const handleNoAcquista = () => {

    };

    useEffect(() => {
        setDiceValue1(diceResult.dice1);
        setDiceValue2(diceResult.dice2);
    }, [diceResult]);

    useEffect(() => {
        setIsRolled(diceRolled);
    }, [diceRolled]);

    useEffect(() => {
        setIsPayment(payment.flag);

    }, [payment]);

    useEffect(() => {
        setIsDraw(draw.flag);

    }, [draw]);

    useEffect(() => {
        setIsBuy(buy.flag);
    }, [buy]);

    


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
            {isRolled && isPayment && (
                <div>
                    <p>Hai pagato: {payment.payment}€ a {payment.destination} per {payment.description}</p>
                </div>
            )}
            {isRolled && isDraw && (
                <div>
                    <p>Hai pescato un {draw.card} che dice: {card.description}</p>
                </div>
            )}
            {isRolled && isBuy && (
                <div>
                    <p>Sei finito su una proprietà libera che costa {buy.price} </p>
                    <p>Cosa vuoi fare: </p>
                    <button onClick={handleAcquista}>
                        Acquista
                    </button>
                    <button onClick={handleNoAcquista}>
                        Non Acquistare
                    </button>
                </div>
                
            )}
        </div>
    );
};

export default RollDice;
