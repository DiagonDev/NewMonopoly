import React, {useState, useContext} from 'react';
import {WebSocketContext} from "../contexts/WebSocketContext";
import {useNavigate} from 'react-router-dom';
import {useWebSocket} from "../websocket/WebSocketProvider.jsx";


const CreateGamePage = () => {
    const {isConnected, sendMessage} = useWebSocket();
    const [userName, setUserName] = useState('');
    const [difficulty, setDifficulty] = useState('Facile');
    const [randomization, setRandomization] = useState('false');
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        if (isConnected) {
            // Invia il messaggio al server
            sendMessage({
                type: "createGame",
                userName,
                difficulty,
                randomization,
            });
            console.log("Dati inviati al server:", {userName, difficulty, randomization});

            setUserName('');
            setDifficulty('');
            setRandomization('');
            navigate('/play')
        } else {
            console.error("Connessione WebSocket non stabilita!");
        }
    };

    return (
        <div>
            <h1>Crea una nuova partita</h1>
            <form onSubmit={handleSubmit}>
                <label>
                    Nome Utente:
                    <input
                        type="text"
                        placeholder="Inserisci Nome"
                        value={userName}
                        onChange={(e) => setUserName(e.target.value)}
                    />
                </label>
                <br/>
                <label>
                    Difficoltà:
                    <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
                        <option value="Facile">Facile</option>
                        <option value="Medio">Medio</option>
                        <option value="Difficile">Difficile</option>
                    </select>
                </label>
                <br/>
                <label>
                    Randomizzazione caselle:
                    <select value={randomization} onChange={(e) => setRandomization(e.target.value)}>
                        <option value="false">No</option>
                        <option value="true">Sì</option>
                    </select>
                </label>
                <br/>
                <button type="submit">Crea</button>
            </form>
            {/* Mostra il messaggio ricevuto dal server */}
            {serverMessage && <p>Messaggio dal server: {serverMessage}</p>}
        </div>
    );
};

export default CreateGamePage;
