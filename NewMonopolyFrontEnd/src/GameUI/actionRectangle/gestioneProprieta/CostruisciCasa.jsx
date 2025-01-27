import React, {useContext, useState} from "react";
import PropertyOwned from "./PropertyOwned.jsx";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";

const CostruisciCasa = ({buildingProperties}) => {
    const {socket, connected} = useContext(WebSocketContext);
    const [selectedProperty, setSelectedProperty] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [casine, setCasine] = useState(1);

    const MAX_CASE = 5; // Numero massimo di case consentite

    const handleSelectedProperty = (property) => {
        setIsModalOpen(true);
        setSelectedProperty(property);
        console.log("Proprietà selezionata:", property);
    };

    const handleCostruisci = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);

        if (socket && connected) {
            const message = {
                type: "!CostruisciCasa",
                property: selectedProperty,
                casine: casine,
            };

            socket.send(JSON.stringify(message));
            console.log('Messaggio inviato:', message);
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);
    };

    const generateOptions = () => {
        const currentHouses = selectedProperty?.numCasa || 0; // Case già costruite
        const maxHousesAvailable = MAX_CASE - currentHouses; // Case rimanenti da costruire
        const availableOptions = [];

        for (let i = 1; i <= maxHousesAvailable; i++) {
            availableOptions.push(
                <option key={i} value={i}>
                    {i}
                </option>
            );
        }

        return availableOptions;
    };


    return (
        <>
            <PropertyOwned
                playerProperties={buildingProperties}
                onPropertySelect={handleSelectedProperty}
                useBuildingProp={true}
            />
            {isModalOpen && (
                <div className="modal">
                    <div className="modalContent">
                        <h2>{selectedProperty?.nome}</h2>
                        <p>Rendita: {selectedProperty?.affitto}</p>
                        <br/>
                        <p>con 1 casa: {selectedProperty?.affitto1Casa}</p>
                        <br/>
                        <p>con 2 case: {selectedProperty?.affitto2Case}</p>
                        <br/>
                        <p>con 3 case: {selectedProperty?.affitto3Case}</p>
                        <br/>
                        <p>con 4 case: {selectedProperty?.affitto4Case}</p>
                        <br/>
                        <p>con Albergo: {selectedProperty?.affittoAlbergo}</p>
                        <br/>
                        <p>Costo per ogni casa (1 casa per ogni proprietà): {selectedProperty?.prezzoCasaCorrente}</p>
                        <label>
                            Case su ogni proprietà (metti 5 per l&#39;albergo):
                            <select
                                value={casine}
                                onChange={(e) => {
                                    const value = parseInt(e.target.value, 10);
                                    setCasine(value);
                                }}
                            >
                                {generateOptions()}
                            </select>
                        </label>
                        <button onClick={handleCostruisci} disabled={!casine}>
                            Compra
                        </button>
                        <button onClick={closeModal}>Indietro</button>
                    </div>
                </div>
            )}
        </>
    );
};

export default CostruisciCasa;
