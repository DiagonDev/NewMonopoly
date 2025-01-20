import PropertyOwned from "./PropertyOwned.jsx";
import React, {useContext, useState} from "react";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";

const ScambiaProprieta = ({playerProperties}) => {
    const {socket, connected, allProperties} = useContext(WebSocketContext);
    const [selectedProperty, setSelectedProperty] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [flag, setFlag] = useState(true);
    const [offertaMonetaria, setOffertaMonetaria] = useState(0);
    const [tempProperty, setTempProperty] = useState(null)
    const [step, setStep] = useState(0);
    const handleSelectedProperty = (property) => {
        setIsModalOpen(true);
        setSelectedProperty(property);
        console.log("Proprietà selezionata:", property);
    };
    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);
    };
    const handleScambia = () => {

        setFlag(false);
        setIsModalOpen(false);
        if (step === 0) {
            setTempProperty(selectedProperty);
            setStep(1);
        } else if (step === 1) {
            if (socket && connected) {
                const message = {
                    type: "EffettuaScambio",
                    property1: tempProperty,
                    property2: selectedProperty,
                    offertaMonetaria: offertaMonetaria,
                };

                socket.send(JSON.stringify(message));
                console.log('Messaggio inviato:', message);
            } else {
                console.error('Connessione WebSocket non stabilita!');
            }
            // Reset dello stato
            setStep(0);
            setTempProperty(null);
            setSelectedProperty(null)
            setOffertaMonetaria(0);
        }
        console.log("Contatore: ", step);
    };

    return (
        <>
            <PropertyOwned
                playerProperties={flag ? playerProperties : allProperties}
                onPropertySelect={handleSelectedProperty}
            />
            {isModalOpen && (
                <div className="modal">
                    <div className="modalContent">
                        {step === 0 && (
                            <>
                                <h2>{selectedProperty.name}</h2>
                                <p>Rendita: {selectedProperty.rendita}</p>
                                <p>Costo: {selectedProperty.costo}</p>
                                <button onClick={handleScambia}>Seleziona</button>
                                <button onClick={closeModal}>Indietro</button>
                            </>
                        )}
                        {step === 1 && (
                            <>
                                <h2>Proprietà selezionate:</h2>
                                <p>{tempProperty.name} (Rendita: {tempProperty.rendita}, Costo: {tempProperty.costo})</p>
                                <p>{selectedProperty.name} (Rendita: {selectedProperty.rendita}, Costo: {selectedProperty.costo})</p>
                                <label>
                                    Offerta monetaria:
                                    <input
                                        type="number"
                                        placeholder="Danari"
                                        value={offertaMonetaria}
                                        onChange={(e) => setOffertaMonetaria(e.target.value)}
                                    />
                                </label>
                                <button onClick={handleScambia}>Conferma Scambio</button>
                                <button onClick={closeModal}>Indietro</button>
                            </>
                        )}
                    </div>
                </div>
            )}
        </>
    );

};
export default ScambiaProprieta;
