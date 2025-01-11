import React, { useState } from "react";
import GameChat from './chat/GameChat';
import GameConsole from './chat/GameConsole';

const ChatFather = () => {
    const [activeComponent, setActiveComponent] = useState("GameChat"); // Stato per gestire il componente attivo

    return (
        <div className="chatFatherDiv">
            {/* Bottoni per cambiare componente */}
            <div className="chatFatherBtnDiv">
                <button onClick={() => setActiveComponent("GameChat")}>
                    Mostra Game Chat
                </button>
                <button onClick={() => setActiveComponent("GameConsole")}>
                    Mostra Game Console
                </button>
            </div>

            {/* Contenuto che cambia in base allo stato */}
            <div>
                {activeComponent === "GameChat" && <GameChat />}
                {activeComponent === "GameConsole" && <GameConsole />}
            </div>
        </div>
    );
};

export default ChatFather;
