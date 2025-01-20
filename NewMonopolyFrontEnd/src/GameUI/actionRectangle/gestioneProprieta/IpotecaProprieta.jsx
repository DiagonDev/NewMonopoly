import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";
import {useContext, useState} from "react";
import PropertyOwned from "./PropertyOwned.jsx";

const IpotecaProprieta = ({ playerProperties })  => {
    const {socket, connected} = useContext(WebSocketContext);
    const [selectedProperty, setSelectedProperty] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    // Funzione per gestire la proprietà selezionata
    const handleSelectedProperty = (property) => {
        setIsModalOpen(true);
        console.log("Proprietà selezionata:", property);
    };
    
    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedProperty(null);
    }
    const handleIpoteca = () => {
        if (socket && connected) {
            const message = {
                type: "IpotecaProprieta",
                property: selectedProperty,
            };

            socket.send(JSON.stringify(message));
            console.log('Messaggio inviato:', message);
            setIsModalOpen(false);
            setSelectedProperty(null);
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    };
    

    /**
     * onPropertySelect definisce un metodo callback
     * per passare i dati dal figlio al padre
     */
    return (
        <>
            <PropertyOwned
                playerProperties={playerProperties}
                onPropertySelect={handleSelectedProperty}
            />
            {isModalOpen && (
                <div className="modal">
                    <div className="modalContent">
                        <h2>{selectedProperty.name}</h2>
                        <p>{selectedProperty.rendita}</p>
                        <p>{selectedProperty.costo}</p>
                        
                        <button onClick={handleIpoteca}>Ipoteca</button>
                        <button onClick={closeModal}>Indietro</button>
                    </div>
                </div>
            )}
        </>
    );
};
export default IpotecaProprieta;