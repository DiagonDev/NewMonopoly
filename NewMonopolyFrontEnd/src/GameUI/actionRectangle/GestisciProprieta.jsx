import React, {useContext, useEffect, useState} from 'react';

import IpotecaProprieta from "./gestioneProprieta/IpotecaProprieta.jsx";
import CostruisciCasa from "./gestioneProprieta/CostruisciCasa.jsx";
import ScambiaProprieta from "./gestioneProprieta/ScambiaProprieta.jsx";
import {WebSocketContext} from "../../contexts/WebSocketContext.jsx";


const GestisciProprieta = () => {
    const {playerProperties} = useContext(WebSocketContext);
    const [activeComponent, setActiveComponent] = useState('');
    const [buildingProperties, setBuildingProperties] = useState([]);
    /**
     * .reduce itera sull' array
     *  acc è inizialmente vuota, se possiede il colore corrente pusho
     */
    useEffect(() => {
        const groupedProperties = playerProperties.reduce((acc, property) => {
            if (!acc[property.color]) {
                acc[property.color] = [];
            }
            acc[property.color].push(property);
            return acc;
        }, {});

        // Filtra i gruppi con almeno 3 proprietà dello stesso colore
        const result = Object.values(groupedProperties).filter(group => group.length >= 3);
        //TODO: cambiare result2
        //const result2 = Object.values(result).filter(group => group.length === 2 && (group.color === 'coloreVicoloStretto' || group.color === 'coloreParcoVittoria'));
        //result.concat(result2);
        setBuildingProperties(result.flat());
    },[playerProperties]);
    return (
        <>
            <div className="gestisciProprieta-container">
                <div className="gestisciProprieta-buttons">
                    <button
                        onClick={() => setActiveComponent("CostruisciCasa")}
                    >
                        Costruisci Casa
                    </button>
                    <button
                        onClick={() => setActiveComponent("ScambiaProprieta")}
                    >
                        ScambiaProprietà
                    </button>
                    <button
                        onClick={() => setActiveComponent("IpotecaProprieta")}
                    >
                        Ipoteca Proprietà
                    </button>
                </div>
                <div className="gestisciProprieta-components">
                    {activeComponent === "CostruisciCasa" && <CostruisciCasa buildingProperties={buildingProperties} />}
                    {activeComponent === "ScambiaProprieta" && <ScambiaProprieta playerProperties={playerProperties}/>}
                    {activeComponent === "IpotecaProprieta" && <IpotecaProprieta playerProperties={playerProperties}/>}
                </div>
            </div>
        </>
    );
};

export default GestisciProprieta;