import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import App from './App.jsx';
import { WebSocketProvider } from './eventListener/WebSocketContext.jsx';

createRoot(document.getElementById('root')).render(
  //TODO: togliere Strict mode una volta finito
  
    <BrowserRouter>
    <WebSocketProvider>
    <App />
    </WebSocketProvider>
      
    </BrowserRouter>
  
);
