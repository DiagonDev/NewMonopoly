import React, {useContext, useEffect, useState} from 'react';
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";



// eslint-disable-next-line react/prop-types
const RiceviScambio = ({ onClose }) => {
    const {socket, connected, exchangeRequest} = useContext(WebSocketContext);
    const [flag, setFlag] = useState(0);
    const [exchangeAccepted, setExchangeAccepted] = useState(false);

    const handleAccetta = () => {
        setExchangeAccepted(true);
        sendMessage();
    }

    const sendMessage = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            const message = {
                type: "!RispostaScambio",
                property1:  exchangeRequest.property1,
                property2: exchangeRequest.property2,
                offertaMonetaria: exchangeRequest.money,
                exchangeAccepted: exchangeAccepted,
            };

            socket.send(JSON.stringify(message));
            setExchangeAccepted(false);
            console.log('Messaggio inviato:', message);
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
                        <p>Ha offerto {exchangeRequest.property1} per {exchangeRequest.property2}</p>
                    </>
                )}

                {/* Quando ti richiede sodli aggiuntivi */}
                {(flag===1) && (
                    <>
                        <p>Ha offerto {exchangeRequest.property1} per {exchangeRequest.property2} + {Math.abs(exchangeRequest.money)}</p>
                    </>
                )}

                {/* Quando ti da sodli aggiuntivi */}
                {(flag===2) && (
                    <>
                        <p>Ha offerto {exchangeRequest.property1} + {Math.abs(exchangeRequest.money)} per {exchangeRequest.property2}</p>
                    </>
                )}

                <div>
                    <button onClick={handleAccetta}>
                        Accetta
                    </button>
                    <button onClick={sendMessage}>
                        Rifiuta
                    </button>
                </div>
            </div>
        </>
    );
};

export default RiceviScambio;