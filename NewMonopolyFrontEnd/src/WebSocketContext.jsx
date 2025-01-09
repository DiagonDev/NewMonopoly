import React, { createContext, useEffect, useState } from "react";

export const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [connected, setConnected] = useState(false);
  const [serverMessage, setServerMessage] = useState('');
  const [userMessages, setUserMessages] = useState([]);

  useEffect(() => {
    const ws = new WebSocket("https://c3cb-84-33-176-173.ngrok-free.app/ws/connection");

    ws.onopen = () => {
      setSocket(ws);
      setConnected(true);
    };

    ws.onmessage = (event) => {
      console.log("Messaggio dal server:", event.data);
      if (event.data.startsWith('!')) {
        setUserMessages((prevMessages) => {
          const updatedMessages = [...prevMessages, event.data];
          console.log("Messaggi aggiornati:", updatedMessages);
          return updatedMessages;
      });
      }
      else
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
    <WebSocketContext.Provider value={{ socket, connected, serverMessage, userMessages }}>
      {children}
    </WebSocketContext.Provider>
  );
};
