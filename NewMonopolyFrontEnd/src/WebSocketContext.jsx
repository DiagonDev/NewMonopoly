import React, { createContext, useEffect, useState } from "react";

export const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [connected, setConnected] = useState(false);
  const [serverMessages, setServerMessages] = useState([]);
  const [userMessages, setUserMessages] = useState([]);

  useEffect(() => {
    //const ws = new WebSocket("https://c3cb-84-33-176-173.ngrok-free.app/ws/connection");
    const ws = new WebSocket("ws://localhost:8080/ws/connection");

    ws.onopen = () => {
      setSocket(ws);
      setConnected(true);
    };

    ws.onmessage = (event) => {
      console.log("Messaggio dal server:", event.data);
      const message = JSON.parse(event.data);
      /**
       *  Ricevo dal back end un json con:
       *  type: chat / system
       *  content: "messaggio effettivo"
       */
      if (message.type === 'chat') {
        setUserMessages((prevMessages) => {
          const updatedMessages = [...prevMessages, message.content];
          return updatedMessages;
        });
      }
      else if (message.type === 'system') {
        setServerMessages((prevMessages) => {
          const updatedServerMessages = [...prevMessages, message.content];
          return updatedServerMessages;
        });
      }
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
    <WebSocketContext.Provider value={{ socket, connected, serverMessages, userMessages }}>
      {children}
    </WebSocketContext.Provider>
  );
};
