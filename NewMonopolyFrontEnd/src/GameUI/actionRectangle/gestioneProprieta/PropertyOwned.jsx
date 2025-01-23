import {translateColor} from "../../../util/itToEnColors.jsx";
import {useContext, useEffect, useState} from "react";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";

// eslint-disable-next-line react/prop-types
const PropertyOwned = ({playerProperties, onPropertySelect}) => {
    const {updateProperties} = useContext(WebSocketContext);
    const [propertiesToShow, setPropertiesToShow] = useState(playerProperties || []); // Inizializza con le proprietà iniziali

    // Aggiorna le proprietà visualizzate quando updateProperties cambia
    useEffect(() => {
        if (updateProperties) {
            setPropertiesToShow(updateProperties);
        }
    }, [updateProperties]);

    // Funzione per aprire il modal e inviare i dati al padre
    const handleSquareClick = (property) => {
        if (onPropertySelect) {
            onPropertySelect(property); // Passa il valore al padre
        }
    };

    //non renderizzo se entrambe nulle
    if (!playerProperties && !updateProperties) {
        return null;
    }

    return (
        <div className="propertyGrid">
            {propertiesToShow.map((property, index) => (
                <div
                    key={index}
                    className="propertySquare"
                    onClick={() => handleSquareClick(property)}
                >
                    <div
                        className="propertyColorBar"
                        style={{backgroundColor: translateColor(property.colore)}}
                    >
                        <br />
                        <div className="propertyContent">
                            {property.nome}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default PropertyOwned;
