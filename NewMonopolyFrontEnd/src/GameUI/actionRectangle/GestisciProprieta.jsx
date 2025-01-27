import React, {useContext, useEffect, useState} from 'react';

import IpotecaProprieta from "./gestioneProprieta/IpotecaProprieta.jsx";
import CostruisciCasa from "./gestioneProprieta/CostruisciCasa.jsx";
import ScambiaProprieta from "./gestioneProprieta/ScambiaProprieta.jsx";
import {WebSocketContext} from "../../eventListener/WebSocketContext.jsx";


const GestisciProprieta = ({playerProperties}) => {
    const {socket, connected} = useContext(WebSocketContext);
    const [activeComponent, setActiveComponent] = useState('');
    const [buildingProperties, setBuildingProperties] = useState([]);
    /**
     * .reduce itera sull' array
     *  acc è inizialmente vuota, se possiede il colore corrente pusho
     */
    useEffect(() => {
        const groupedProperties = playerProperties.reduce((acc, property) => {
            if (property.color !== null) {
                if (!acc[property.color]) {
                    acc[property.color] = [];
                }
                acc[property.color].push(property);
                return acc;
            }

        }, {});

        // Filtra i gruppi con almeno 3 proprietà dello stesso colore
        const result = Object.values(groupedProperties).filter(group => group.length >= 3);
        const result2 = Object.values(groupedProperties).filter(group =>
            group.length === 2 && (group[0].color === 'Marrone' || group[0].color === 'Blu')
        );

        setBuildingProperties(result.concat(result2).flat());
    }, [playerProperties]);

    /**
     * quando clicco su scambiaProprietà mando un "ping" al server, gli chiedo di prepararsi
     * a inviarmi le proprietà possedute dagli altri giocatori
     *
     */
    const handleScambiaProprieta = () => {
        setActiveComponent("ScambiaProprieta")
        if (socket && connected) {
            // Invia un messaggio al server
            socket.send(`PingScambiaProprieta:`);
            console.log('Messaggio inviato: PingScambiaProprieta');
        } else {
            console.error('Connessione WebSocket non stabilita!');
        }
    }
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
                        onClick={handleScambiaProprieta}
                    >
                        Scambia Proprietà
                    </button>
                    <button
                        onClick={() => setActiveComponent("IpotecaProprieta")}
                    >
                        Ipoteca Proprietà
                    </button>
                </div>
                <div className="gestisciProprieta-components">
                    {activeComponent === "CostruisciCasa" && <CostruisciCasa buildingProperties={buildingProperties}/>}
                    {activeComponent === "ScambiaProprieta" && <ScambiaProprieta playerProperties={playerProperties}/>}
                    {activeComponent === "IpotecaProprieta" && <IpotecaProprieta playerProperties={playerProperties}/>}
                </div>
            </div>
        </>
    );
};

export default GestisciProprieta;