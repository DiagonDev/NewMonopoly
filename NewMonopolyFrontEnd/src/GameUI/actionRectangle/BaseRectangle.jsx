import {useContext, useState, useEffect, useRef} from 'react';
import RiceviScambio from './gestioneProprieta/RiceviScambio';
import {WebSocketContext} from "../../contexts/WebSocketContext.jsx";
import PropertyOwned from "./gestioneProprieta/PropertyOwned.jsx";

const BaseRectangle = ({playerProperties}) => {
    const {exchangeRequest} = useContext(WebSocketContext);
    const [isPageOpen, setIsPageOpen] = useState(false);

    useEffect(() => {
        if (exchangeRequest.flag) {
            setIsPageOpen(true);
        }
        exchangeRequest.flag = false;
    }, [exchangeRequest]);

    return (
        <div>
            {isPageOpen ? (
                <RiceviScambio onClose={() => setIsPageOpen(false)}/>
            ) : (
                <PropertyOwned playerProperties={playerProperties}/>
            )}

        </div>
    );
};

export default BaseRectangle;