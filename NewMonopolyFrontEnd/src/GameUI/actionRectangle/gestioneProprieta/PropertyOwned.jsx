// eslint-disable-next-line react/prop-types
import {mockPlayerProperties} from "./PlayerPropertyMock.jsx";

// eslint-disable-next-line react/prop-types
const PropertyOwned = ({playerProperties, onPropertySelect}) => {

    // Funzione per aprire il modal e inviare i dati al padre
    const handleSquareClick = (property) => {
        if (onPropertySelect) {
            onPropertySelect(property); // Passa il valore al padre
        }
    };
    // eslint-disable-next-line react/prop-types
    const propertiesToUse = playerProperties.length > 0 ? playerProperties : mockPlayerProperties;
    return (
        <>
            <div className="propertyGrid">
                {propertiesToUse.map((property, index) => (
                    <div
                        key={index}
                        className="propertySquare"
                        onClick={() => handleSquareClick(property)}
                    >
                        {property.nome}
                    </div>
                ))}

            </div>
        </>
    );
};

export default PropertyOwned;
