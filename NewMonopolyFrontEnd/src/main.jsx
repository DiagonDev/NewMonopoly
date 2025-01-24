
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import App from './App.jsx';
import {WebSocketProvider} from "./websocket/WebSocketProvider.jsx";

createRoot(document.getElementById('root')).render(
    <BrowserRouter>
    <WebSocketProvider>
    <App />
    </WebSocketProvider>
      
    </BrowserRouter>
  
);
