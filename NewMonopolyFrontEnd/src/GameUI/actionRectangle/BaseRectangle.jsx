import { useContext, useState, useEffect } from 'react';
import RiceviScambio from './gestioneProprieta/RiceviScambio';
import {WebSocketContext} from "../../contexts/WebSocketContext.jsx";

const BaseRectangle = () => {
    const {exchangeRequest} = useContext(WebSocketContext);
    const [isPageOpen, setIsPageOpen] = useState(false);

    useEffect(() => {
        if(exchangeRequest.flag){
            setIsPageOpen(true);
        }
        
    },[exchangeRequest]);
    
    return (
        <div>
            {isPageOpen ? (
                <RiceviScambio onClose={() => setIsPageOpen(false)} />
            ) : (
                <h1>Base Rectangle</h1>
            )}
        </div>
    );
};

export default BaseRectangle;