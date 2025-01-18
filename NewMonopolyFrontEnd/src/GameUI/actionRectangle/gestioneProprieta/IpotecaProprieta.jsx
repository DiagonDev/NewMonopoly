const IpotecaProprieta = () => {
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
            socket.send(`IpotecaProprieta:${selectedProperty}`);
            console.log('Messaggio inviato: IpotecaProprieta');
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
                buildingProperties={buildingProperties}
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