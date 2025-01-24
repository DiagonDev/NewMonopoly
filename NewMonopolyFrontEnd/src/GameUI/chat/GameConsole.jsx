import {useWebSocket} from "../../websocket/WebSocketProvider.jsx";

const GameConsole = () => {
    const {messages} = useWebSocket();
    const serverMessages = messages
        .filter((msg) => msg.type === "system")
        .map((msg) => msg.payload);
    return (
        <div>
            <textarea className='chat-window'
                      value={Array.isArray(serverMessages) ? serverMessages.join("\n") : ""}
                      readOnly
                      rows={10}
                      cols={10}
            />
        </div>
    );
};

export default GameConsole;