import { Routes, Route, Link } from 'react-router-dom';
import WelcomePage from './pages/WelcomePage'; // Importa il componente WelcomePage
import CreateGamePage from './pages/CreateGamePage';
import JoinGamePage from './pages/JoinGamePage';
import './App.css';
import React, { useState, useEffect } from 'react';
import { WebSocketProvider } from "./WebSocketContext";


function App() {

  return (
    <WebSocketProvider>
    <>
       {/* Titolo in alto a destra */}
       <h1 className="titolo">NewMonopolyGame</h1>
      {/* Contenitore principale */}
      <div className="main-container">
        {/* Configura le rotte */}
        <Routes>
          <Route path="*" element={<WelcomePage />} /> {/* WelcomePage come rotta predefinita */}
          <Route path="/create" element={<CreateGamePage  />}/> 
          <Route path="/join" element = {<JoinGamePage />}/>
        </Routes>
      </div>
    </>
    </WebSocketProvider>
  );
}

export default App;
