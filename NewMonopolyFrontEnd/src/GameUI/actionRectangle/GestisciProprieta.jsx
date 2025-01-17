import React, {useContext, useState} from 'react';

import IpotecaProprieta from "./gestioneProprieta/IpotecaProprieta.jsx";
import CostruisciCasa from "./gestioneProprieta/CostruisciCasa.jsx";
import ScambiaProprieta from "./gestioneProprieta/ScambiaProprieta.jsx";


const GestisciProprieta = () => {
    const [activeComponent, setActiveComponent] = useState('');
    return (
        <>
            <button
                onClick={() => setActiveComponent("CostruisciCasa")}

            >
                Costruisci Casa
            </button>
            <button
                onClick={() => setActiveComponent("ScambiaProrieta")}
            >
                ScambiaProprietà
            </button>
            <button
                onClick={() => setActiveComponent("IpotecaProprieta")}
            >
                Ipoteca Proprietà
            </button>
            <div className="grid-item">
                {activeComponent === "RollDice" && <CostruisciCasa/>}
                {activeComponent === "GestisciProprieta" && <ScambiaProprieta/>}
                {activeComponent === "IpotecaProprieta" && <IpotecaProprieta/>}
                <button
                onClick={setActiveComponent('') && <GestisciProprieta/>}>
                    Indietro
                </button>
            </div>
        </>
    );
};

export default GestisciProprieta;