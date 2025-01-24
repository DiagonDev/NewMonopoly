import React, { createContext, useContext, useEffect, useRef, useState } from "react";
import { createWebSocketEvents } from "./WebSocketEvents";
import { WEBSOCKET_URL } from "./WebSocketConfig";

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
    const socketRef = useRef(null);
    const [isConnected, setIsConnected] = useState(false);
    const [messages, setMessages] = useState([]);
    const pingIntervalRef = useRef(null);

    useEffect(() => {
        socketRef.current = new WebSocket(WEBSOCKET_URL);

        const { onOpen, onClose, onMessage } = createWebSocketEvents(socketRef, setMessages, setIsConnected);

        const socket = socketRef.current;
        socket.addEventListener("open", () => {
            onOpen();
            // Start ping interval when connected
            pingIntervalRef.current = setInterval(() => {
                if (socket.readyState === WebSocket.OPEN) {
                    socket.send(JSON.stringify({ type: "ping" }));
                }
            }, 30000); // Ping every 30 seconds
        });

        socket.addEventListener("close", () => {
            onClose();
            // Clear the ping interval on close
            if (pingIntervalRef.current) {
                clearInterval(pingIntervalRef.current);
                pingIntervalRef.current = null;
            }
        });

        socket.addEventListener("message", onMessage);

        return () => {
            socket.removeEventListener("open", onOpen);
            socket.removeEventListener("close", onClose);
            socket.removeEventListener("message", onMessage);
            if (pingIntervalRef.current) {
                clearInterval(pingIntervalRef.current);
            }
            socket.close();
        };
    }, []);

    const sendMessage = (message) => {
        if (socketRef.current && isConnected) {
            socketRef.current.send(JSON.stringify(message));
        }
    };

    return (
        <WebSocketContext.Provider value={{ isConnected, messages, sendMessage }}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = () => useContext(WebSocketContext);
