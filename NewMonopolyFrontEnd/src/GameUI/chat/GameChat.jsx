import { useState } from "react";
import { useWebSocket } from "../../websocket/WebSocketProvider.jsx";

const GameChat = () => {
    const { isConnected, sendMessage, messages } = useWebSocket();
    const [userMessage, setUserMessage] = useState("");

    // Deriva i messaggi utente dai messaggi WebSocket
    const userMessages = messages
        .filter((msg) => msg.type === "chat")
        .map((msg) => msg.payload);

    const handleSubmit = (e) => {
        e.preventDefault();

        if (isConnected) {
            sendMessage({ type: "chat", message: userMessage });
            console.log("Dati inviati al server:", { userMessage });

            setUserMessage("");
        } else {
            console.error("Connessione WebSocket non stabilita!");
        }
    };

    return (
        <div>
      <textarea
          className="chat-window"
          value={userMessages.join("\n")}
          readOnly
          rows={10}
          cols={10}
      />
            <br />
            <form onSubmit={handleSubmit}>
                <label>
                    Chatta:
                    <input
                        type="text"
                        placeholder="Scrivi un messaggio a tutti"
                        value={userMessage}
                        onChange={(e) => setUserMessage(e.target.value)}
                    />
                    <button type="submit">Invia</button>
                </label>
            </form>
        </div>
    );
};

export default GameChat;
