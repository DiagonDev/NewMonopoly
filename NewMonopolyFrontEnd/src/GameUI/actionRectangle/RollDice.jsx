import React, {useContext, useEffect, useState} from 'react';
import { WebSocketContext } from '../../eventListener/WebSocketContext';
import {pawnColors} from "../../pages/pawnColors.jsx";

const RollDice = () => {
    const { socket, connected, diceResult, diceRolled, diceRolled2, payment, draw, buy , nameBox, buyReturn, prison, exitPrison} = useContext(WebSocketContext); // Accesso al WebSocket
    const [isRolled, setIsRolled] = useState(diceRolled);
    const [isRolled2, setIsRolled2] = useState(diceRolled);
    //const [currentPosition, setCurrentPosition] = useState('Parco della vittoria'); // Posizione iniziale
   
    const [diceValue1, setDiceValue1] = useState(null); // Valore del dado
    const [diceValue2, setDiceValue2] = useState(null); // Valore del dado2
    
    //
    const [purchased, setPurchased] = useState(false);
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
            setPurchased(true);
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };
    const handleAcquistaPunti = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`AcquistaProprietaPunti:${nameBox}`);
            console.log(`Messaggio inviato: AcquistaProprietaPunti:${nameBox}`);
            setPurchased(true);
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };
    
    const handleUscitaPrigione = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`PagaUscitaPrigione:`);
            console.log(`Esci pagando`);
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
        setIsRolled2(diceRolled2);
        
    }, [diceRolled2]);
    
    
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
            {/* Se isRolled è true, mostra la scritta con il risultato, altrimenti mostra il bottone */}
            {isRolled && (
                <div>
                    <p>Hai lanciato i dadi! Totale: {diceValue1+diceValue2}</p>
                    <p>Hai ottenuto un {diceValue1} con il primo dado e {diceValue2} con il secondo dado</p>
                    {((prison && (diceValue1===diceValue2)) || !prison) &&(
                        <p>Sei arrivato sulla casella: {nameBox}</p>
                    )}
                    {(prison) &&(
                        <p>Sei ancora in prigione</p>
                    )}
                    
                    
                </div>
            )}
            {(!isRolled || (!isRolled2&&!prison)) && (
                <div>
                    <button onClick={handleRoll}>
                        Roll
                    </button>
                    {(prison) &&(
                        <button onClick={handleUscitaPrigione}>
                            Paga 50$ per uscire di prigione
                        </button>
                    )}
                    {(exitPrison===1) &&(
                        <p>
                            Sei uscito
                        </p>
                    )}
                    {(exitPrison===2) &&(
                        <p>
                            Non hai piu soldi non lo puoi fare
                        </p>
                    )}
                </div>
            )}
            {(isRolled && !isRolled2&&!prison) && (
                <div>
                    <p>Hai fatto doppi dadi, rigioca un altro turno</p>
                </div>
            )}
            {isRolled && isDraw && (
                <div>
                    <p>Hai pescato una carta {draw.card} che dice:</p>
                    <p> {draw.description}</p>
                </div>
            )}
            {isRolled && isPayment && (
                <div>
                    <p>Hai pagato: {payment.payment}€ a {payment.destination} per {payment.description}</p>
                </div>
            )}
            {isRolled && isBuy &&(
                <div>
                    <p>Questa proprietà è libera e costa {buy.price}€ o {buy.points} punti</p>
                    <button onClick={handleAcquista}>
                        Acquista coi soldi
                    </button>
                    <button onClick={handleAcquistaPunti}>
                        Acquista coi punti
                    </button>
                   
                </div>
                
            )}
            {(buyReturn.flag && buyReturn.success) &&(
                <div>
                    <p>Hai acquistato la proprieta {nameBox} </p>
                </div>
                
            )}
            {(buyReturn.flag && !buyReturn.success) &&(
                <div>
                    <p>Non hai abbastanza soldi/punti </p>
                   
                </div>
                
            )}
        </div>
    );
};

export default RollDice;
