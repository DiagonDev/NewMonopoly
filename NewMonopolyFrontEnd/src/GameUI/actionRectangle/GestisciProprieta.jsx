import React, {useContext, useState} from 'react';

import IpotecaProprieta from "./gestioneProprieta/IpotecaProprieta.jsx";
import CostruisciCasa from "./gestioneProprieta/CostruisciCasa.jsx";
import ScambiaProprieta from "./gestioneProprieta/ScambiaProprieta.jsx";
import {WebSocketContext} from "../../contexts/WebSocketContext.jsx";


const GestisciProprieta = () => {
    const {playerProperties} = useContext(WebSocketContext);
    const [activeComponent, setActiveComponent] = useState('');
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
                    {activeComponent === "CostruisciCasa" && <CostruisciCasa playerProperties={playerProperties} />}
                    {activeComponent === "ScambiaProprieta" && <ScambiaProprieta playerProperties={playerProperties}/>}
                    {activeComponent === "IpotecaProprieta" && <IpotecaProprieta playerProperties={playerProperties}/>}
                </div>
            </div>
        </>
    );
};

export default GestisciProprieta;