import React, {useContext, useEffect, useState} from 'react';
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";



// eslint-disable-next-line react/prop-types
const RiceviScambio = ({ onClose }) => {
    const {socket, connected, exchangeRequest} = useContext(WebSocketContext);
    const [flag, setFlag] = useState(0);

    const handleAccetta = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`AccettaScambio:${exchangeRequest.playerName}`);
            console.log('Messaggio inviato: AccettaScambio');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
        onClose();
    }

    const handleRifiuta = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`RifiutaScambio:${exchangeRequest.playerName}`);
            console.log('Messaggio inviato: RifiutaScambio');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
        onClose();
    }

    useEffect(() => {
        if(exchangeRequest.money===0){
            setFlag(0);
        }else if(exchangeRequest.money>0){
            setFlag(1);
        }else{
            setFlag(2);
        }
    },[exchangeRequest]);

    
    return (
        <>
            <div>
                <p>Ti è stato offerto uno scambio da {exchangeRequest.playerName}</p>
                {/* Quando non si aggiungono soldi all offerta */}
                {(flag===0) && (
                    <>
                        <p>Ha offerto {exchangeRequest.properties1} per {exchangeRequest.properties2}</p>
                    </>
                )}

                {/* Quando ti richiede sodli aggiuntivi */}
                {(flag===1) && (
                    <>
                        <p>Ha offerto {exchangeRequest.properties1} per {exchangeRequest.properties2} + {Math.abs(exchangeRequest.money)}</p>
                    </>
                )}

                {/* Quando ti da sodli aggiuntivi */}
                {(flag===2) && (
                    <>
                        <p>Ha offerto {exchangeRequest.properties1} + {Math.abs(exchangeRequest.money)} per {exchangeRequest.properties2}</p>
                    </>
                )}

                <div>
                    <button onClick={handleAccetta}>
                        Accetta
                    </button>
                    <button onClick={handleRifiuta}>
                        Rifiuta
                    </button>
                </div>
            </div>
        </>
    );
};

export default RiceviScambio;