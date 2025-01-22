import {useContext, useEffect, useState} from 'react';
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";



// eslint-disable-next-line react/prop-types
const RiceviScambio = ({ onClose }) => {
    const {socket, connected, exchangeRequest} = useContext(WebSocketContext);
    const [flag, setFlag] = useState(0);

    const handleAccetta = () => {
        sendMessage(true);
    }
    const handleRifiuta =() => {
        sendMessage(false);
    }
    const sendMessage = (exchangeAccepted) => {
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
            console.log('Messaggio inviato RispostaScambio:', message);
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
                        <p>Ha offerto {exchangeRequest.property1.nome} per {exchangeRequest.property2.nome}</p>
                    </>
                )}

                {/* Quando ti richiede soldi aggiuntivi */}
                {(flag===1) && (
                    <>
                        <p>Ha offerto {exchangeRequest.property1.nome} per {exchangeRequest.property2.nome} + {Math.abs(exchangeRequest.money)}</p>
                    </>
                )}

                {/* Quando ti dà soldi aggiuntivi */}
                {(flag===2) && (
                    <>
                        <p>Ha offerto {exchangeRequest.property1.nome} + {Math.abs(exchangeRequest.money)} per {exchangeRequest.property2.nome}</p>
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