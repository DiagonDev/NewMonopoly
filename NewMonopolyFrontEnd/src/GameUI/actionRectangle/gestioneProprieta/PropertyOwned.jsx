import {translateColor} from "../../../util/itToEnColors.jsx";
import {useContext, useEffect, useState} from "react";
import {WebSocketContext} from "../../../contexts/WebSocketContext.jsx";
import {mockPlayerProperties} from "./PlayerPropertyMock.jsx";

const areAllPropertiesDifferent = (playerProperties, updateProperties) => {
    console.log(playerProperties);
    console.log(updateProperties);
    if (!playerProperties || !updateProperties) return true;

    return playerProperties.every(playerProp =>
        !updateProperties.some(updateProp => playerProp.id === updateProp.id)
    );
};
const buildingProperties = (updateProperties) => {
    //const groupedProperties = updateProperties;
    const groupedProperties = mockPlayerProperties;
    groupedProperties.sort((a, b) => {
        if (a.colore === null) return 1; // Metti gli elementi con colore null alla fine
        if (b.colore === null) return -1;
        return a.colore.localeCompare(b.colore); // Ordine alfabetico per colore
    });
    // Conta quante proprietà ci sono per ogni colore
    const colorGroups = groupedProperties.reduce((acc, property) => {
        if (property.colore) { // Ignora le proprietà con colore null
            if (!acc[property.colore]) {
                acc[property.colore] = [];
            }
            acc[property.colore].push(property);
        }
        return acc;
    }, {});

    console.log("Color groups:", colorGroups);

// Filtra i colori con almeno 3 proprietà o con 2
    const validColors = Object.keys(colorGroups).filter(color =>
        colorGroups[color].length >= 3 ||
        (colorGroups[color].length === 2 && (color === "Marrone" || color === "Blu"))
    );
    console.log("Valid colors:", validColors);

    const filteredProperties = groupedProperties.filter(property =>
        validColors.includes(property.colore)
    );

    console.log("Filtered properties:", filteredProperties);
    return filteredProperties;
}

// eslint-disable-next-line react/prop-types
const PropertyOwned = ({playerProperties, onPropertySelect, useBuildingProp}) => {
    const {updateProperties} = useContext(WebSocketContext);
    const [propertiesToShow, setPropertiesToShow] = useState(playerProperties || []);

    useEffect(() => {
        if (useBuildingProp === true && updateProperties.length > 0) {
            setPropertiesToShow(buildingProperties(updateProperties));
        }
        // Controlla se tutti gli elementi di playerProperties sono diversi da quelli di updateProperties
        else if (areAllPropertiesDifferent(playerProperties, updateProperties) && (useBuildingProp === false || useBuildingProp === undefined)) {
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
                        <br/>
                        <div className="propertyContent">
                            {property.nome}

                        </div>
                        <br/>
                        {property.colore != null && (<div>
                            <p>Case Possedute: </p>
                            {property.numCasa}
                        </div>)}
                    </div>
                </div>
            ))}
        </div>
    );
};

export default PropertyOwned;
