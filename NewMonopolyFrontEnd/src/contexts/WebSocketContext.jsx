import React, { createContext, useEffect, useState } from "react";

export const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [connected, setConnected] = useState(false);
  const [serverMessages, setServerMessages] = useState([]);
  const [userMessages, setUserMessages] = useState([]);
  const [joinMessage, setJoinMessage] = useState('');
  const [gameId, setGameId] = useState('');
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    const ws = new WebSocket("https://c3cb-84-33-176-173.ngrok-free.app/ws/gameNewMonopoly");
    //const ws = new WebSocket("ws://localhost:8080/ws/connection");
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
      else if (message.type === 'system'){
        setServerMessages((prevMessages) => {
          const updatedServerMessages = [...prevMessages, message.content];
          return updatedServerMessages;
        });
      }
      else if (message.type === 'join'){
        setJoinMessage(message.content);
      }
      else if (message.type === 'gameId'){
        setGameId(message.content);
      }
      else if (message.type === 'user'){
        setUserRole(message.content);
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
    <WebSocketContext.Provider value={{ socket, connected, serverMessages, userMessages, joinMessage, gameId, userRole}}>
      {children}
    </WebSocketContext.Provider>
  );
};
