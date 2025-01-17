import React, {useContext, useEffect, useState} from 'react';
import { WebSocketContext } from '../../contexts/WebSocketContext';
import {pawnColors} from "../../pages/pawnColors.jsx";

const RollDice = () => {
    const { socket, connected, diceResult, diceRolled, payment, draw, buy , nameBox} = useContext(WebSocketContext); // Accesso al WebSocket
    const [isRolled, setIsRolled] = useState(diceRolled);
    //const [currentPosition, setCurrentPosition] = useState('Parco della vittoria'); // Posizione iniziale
   
    const [diceValue1, setDiceValue1] = useState(null); // Valore del dado
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
        
    };
    
    //Da implementare che la schermata si blocca quando posso acquistare finche non scelgo un opzione
    const handleAcquista = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`AcquistaProprieta:${nameBox}`);
            console.log(`Messaggio inviato: AcquistaProprieta:${nameBox}`);
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };

    //Quello che fa quando non acquista la proprietà, per ora nulla
   

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

    return (
        <div className='rollDiceDiv'>
            <p id='posizioneAttuale'>Posizione Attuale: Pos1</p>
            {/* Se isRolled è true, mostra la scritta con il risultato, altrimenti mostra il bottone */}
            {isRolled ? (
                <div>
                    <p>Hai lanciato i dadi!</p>
                    <p>Hai ottenuto un {diceValue1} con il primo dado e {diceValue2} con il secondo dado</p>
                    <p>Totale: {diceValue1+diceValue2}</p>
                    <p>Sei arrivato sulla casella: {nameBox}</p>
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
                    <p>Hai pescato una carta {draw.card} che dice:</p>
                    <p> {card.description}</p>
                </div>
            )}
            {isRolled && isBuy && (
                <div>
                    <p>Questa proprietà è libera e costa {buy.price} </p>
                    <button onClick={handleAcquista}>
                        Acquista
                    </button>
                   
                </div>
                
            )}
        </div>
    );
};

export default RollDice;
