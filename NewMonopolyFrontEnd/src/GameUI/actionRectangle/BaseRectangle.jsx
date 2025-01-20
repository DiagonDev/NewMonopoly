import React, { useContext, useState } from 'react';
import RiceviScambio from './gestioneProprieta/RiceviScambio';




const BaseRectangle = () => {
    const {socket, connected, excangeRequest} = useContext(WebSocketContext);
    const [isPageOpen, setIsPageOpen] = useState(false);

    useEffect(() => {
        setIsPageOpen(true);
    },[excangeRequest]);
    
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