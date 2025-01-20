import React, {useContext, useEffect, useState} from 'react';


import {WebSocketContext} from "../../contexts/WebSocketContext.jsx";


const RiceviScambio = ({ onClose }) => {
    const {socket, connected, excangeRequest} = useContext(WebSocketContext);
    const [flag, setFlag] = useState(0);

    const handleAccetta = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`AccettaScambio:${excangeRequest.playerName}`);
            console.log('Messaggio inviato: AccettaScambio');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
        onClose();
    }

    const handleRifiuta = () => {
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`RifiutaScambio:${excangeRequest.playerName}`);
            console.log('Messaggio inviato: RifiutaScambio');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
        onClose();
    }

    useEffect(() => {
        if(excangeRequest.money==0){
            setFlag(0);
        }else if(excangeRequest.money>0){
            setFlag(1);
        }else{
            setFlag(2);
        }
    },[excangeRequest]);

    
    return (
        <>
            <div>
                <p>Ti è stato offero uno scambio da {excangeRequest.playerName}</p>
                {/* Quando non si aggiungono soldi all offerta */}
                {(flag==0) && (
                    <>
                        <p>Ha offerto {excangeRequest.properties1} per {excangeRequest.properties2}</p>
                    </>
                )}

                {/* Quando ti richiede sodli aggiuntivi */}
                {(flag==1) && (
                    <>
                        <p>Ha offerto {excangeRequest.properties1} per {excangeRequest.properties2} + {Math.abs(excangeRequest.money)}</p>
                    </>
                )}

                {/* Quando ti da sodli aggiuntivi */}
                {(flag==2) && (
                    <>
                        <p>Ha offerto {excangeRequest.properties1} + {Math.abs(excangeRequest.money)} per {excangeRequest.properties2}</p>
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