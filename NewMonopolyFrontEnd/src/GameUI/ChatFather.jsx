import React, { useState } from "react";
import GameChat from "./chat/GameChat";
import GameConsole from "./chat/GameConsole";

const ChatFather = () => {
    const [activeComponent, setActiveComponent] = useState("GameChat"); // Stato per gestire il componente attivo

    // Mappa dei componenti
    const components = {
        GameChat: <GameChat />,
        GameConsole: <GameConsole />,
    };

    return (
        <div className="chatFatherDiv">
            {/* Bottoni per cambiare componente */}
            <div className="chatFatherBtnDiv">
                <button
                    onClick={() => setActiveComponent("GameChat")}
                    aria-pressed={activeComponent === "GameChat"}
                >
                    Mostra Game Chat
                </button>
                <button
                    onClick={() => setActiveComponent("GameConsole")}
                    aria-pressed={activeComponent === "GameConsole"}
                >
                    Mostra Game Console
                </button>
            </div>

            {/* Contenuto dinamico */}
            <div>{components[activeComponent]}</div>
        </div>
    );
};

export default ChatFather;
