import React, {useContext, useEffect, useState} from "react";
import PropertyOwned from "./PropertyOwned.jsx";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";

const CostruisciCasa = ({ buildingProperties }) => {
    const {socket, connected} = useContext(WebSocketContext);
    const [selectedProperty, setSelectedProperty] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [casine, setCasine] = useState(0);
    // Funzione per gestire la proprietà selezionata
    const handleSelectedProperty = (property) => {
        setIsModalOpen(true);
        console.log("Proprietà selezionata:", property);
    };
    const handleCostruisci = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`CostruisciCase:${selectedProperty}:${casine}`);
            console.log('Messaggio inviato: GestisciProprieta');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };
    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);
    }

    /**
     * onPropertySelect definisce un metodo callback
     * per passare i dati dal figlio al padre
     */
    //TODO: controllo se case gia costruite
    return (
        <>
            <PropertyOwned
                buildingProperties={buildingProperties}
                onPropertySelect={handleSelectedProperty}
            />
            {isModalOpen && (
                <div className="modal">
                    <div className="modalContent">
                        <h2>{selectedProperty.name}</h2>
                        <p>{selectedProperty.rendita}</p>
                        <p>{selectedProperty.costo}</p>
                        <label>
                            Case su ogni proprietà (metti 5 per l'albergo):
                            <select value={casine} onChange={(e) => setCasine(e.target.value)}>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                            </select>
                        </label>
                        <button onClick={handleCostruisci}>Compra</button>
                        <button onClick={closeModal}>Indietro</button>
                    </div>
                </div>
            )}
        </>
    );
};

export default CostruisciCasa;
