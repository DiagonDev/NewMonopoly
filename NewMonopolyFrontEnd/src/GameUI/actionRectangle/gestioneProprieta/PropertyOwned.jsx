import {translateColor} from "../../../util/itToEnColors.jsx";
import {useContext, useEffect, useState} from "react";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";

const areAllPropertiesDifferent = (playerProperties, updateProperties) => {
    console.log(playerProperties);
    console.log(updateProperties);
    if (!playerProperties || !updateProperties) return true;

    return playerProperties.every(playerProp =>
        !updateProperties.some(updateProp => playerProp.id === updateProp.id)
    );
};

// eslint-disable-next-line react/prop-types
const PropertyOwned = ({playerProperties, onPropertySelect}) => {
    const {updateProperties} = useContext(WebSocketContext);
    const [propertiesToShow, setPropertiesToShow] = useState(playerProperties || []);

    useEffect(() => {
        // Controlla se tutti gli elementi di playerProperties sono diversi da quelli di updateProperties
        if (areAllPropertiesDifferent(playerProperties, updateProperties)) {
            setPropertiesToShow(updateProperties); // Usa updateProperties
        } else {
            setPropertiesToShow(playerProperties); // Usa playerProperties
        }
    }, [playerProperties, updateProperties]);

    const handleSquareClick = (property) => {
        if (onPropertySelect) {
            onPropertySelect(property);
        }
    };

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
