
// eslint-disable-next-line react/prop-types
const PropertyOwned = ({playerProperties, onPropertySelect}) => {

    // Funzione per aprire il modal e inviare i dati al padre
    const handleSquareClick = (property) => {
        if (onPropertySelect) {
            onPropertySelect(property); // Passa il valore al padre
        }
    };

    return (
        <>
            <div className="propertyGrid">
                {/* eslint-disable-next-line react/prop-types */}
                {playerProperties.map((property) => (
                    <div
                        key={property.id}
                        className="propertySquare"
                        onClick={() => handleSquareClick(property)}
                    >
                        {property.name}
                    </div>
                ))}

            </div>
        </>
    );
};

export default PropertyOwned;
