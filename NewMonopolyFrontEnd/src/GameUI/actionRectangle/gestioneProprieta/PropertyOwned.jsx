import {useContext, useState} from "react";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";


// eslint-disable-next-line react/prop-types
const PropertyOwned = ({playerProperties}) => {
    const [selectedProperty, setSelectedProperty] = useState(null); // Stato per il property selezionato
    const [isModalOpen, setIsModalOpen] = useState(false); // Stato per aprire/chiudere il modal
    // Esempio di dati dei quadrati
    const properties = [
        {id: 1, name: "Proprietà 1", description: "Descrizione della Proprietà 1"},
        {id: 2, name: "Proprietà 2", description: "Descrizione della Proprietà 2"},
        {id: 3, name: "Proprietà 3", description: "Descrizione della Proprietà 3"},
        {id: 4, name: "Proprietà 4", description: "Descrizione della Proprietà 4"},
        {id: 5, name: "Proprietà 5", description: "Descrizione della Proprietà 5"},
        {id: 6, name: "Proprietà 6", description: "Descrizione della Proprietà 6"},
        // Aggiungi altre proprietà come necessario
    ];

    // Funzione per aprire il modal con informazioni specifiche
    const handleSquareClick = (property) => {
        setSelectedProperty(property);
        setIsModalOpen(true);
    };

    // Funzione per chiudere il modal
    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);
    };

    return (
        <>
        <div className="propertyGrid">
            {/* eslint-disable-next-line react/prop-types *//*playerProperties è un array di proprietà*/}
            {playerProperties.map((property) => (
                <div
                    key={property.id}
                    className="propertySquare"
                    onClick={() => handleSquareClick(property)}
                >
                    {property.name}
                </div>

            ))}
            {isModalOpen && (
                <div className="modal">
                    <div className="modalContent">
                        <h2>{selectedProperty.name}</h2>
                        <p>{selectedProperty.description}</p>
                        <button onClick={closeModal}>Chiudi</button>
                    </div>
                </div>
            )}
        </div>
        </>
    );
};

export default PropertyOwned;
