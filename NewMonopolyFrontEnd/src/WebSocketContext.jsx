import React, { createContext, useEffect, useState } from "react";

export const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [connected, setConnected] = useState(false);
   const [serverMessage, setServerMessage] = useState('');

  useEffect(() => {
    const ws = new WebSocket("wss://f957-84-33-176-173.ngrok-free.app/ws/connection");

    ws.onopen = () => {
      setSocket(ws);
      setConnected(true);
    };

    ws.onmessage = (event) => {
      console.log("Messaggio dal server:", event.data);
      setServerMessage(event.data);
    };

    ws.onerror = (error) => {
      console.error("Errore WebSocket:", error);
    };

    ws.onclose = () => {
      setConnected(false);
    };

    return () => {
      if (ws.readyState === WebSocket.OPEN) {
        ws.close();
      }
    };
  }, []);

  return (
    <WebSocketContext.Provider value={{ socket, connected, serverMessage }}>
      {children}
    </WebSocketContext.Provider>
  );
};
