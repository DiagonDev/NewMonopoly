import React, { useContext, useState } from 'react';
import { WebSocketContext } from "../../contexts/WebSocketContext";



const GameConsole = () => {
    const { serverMessages} = useContext(WebSocketContext);

    return (
        <div>
            <textarea
                value={Array.isArray(serverMessages) ? serverMessages.join("\n") : ""}
                readOnly
                rows={10}
                cols={10}
            />
        </div>
    );
};

export default GameConsole;