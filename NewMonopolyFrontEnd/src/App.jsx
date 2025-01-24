import {Routes, Route} from 'react-router-dom';
import WelcomePage from './pages/WelcomePage';
import CreateGamePage from './pages/CreateGamePage';
import JoinGamePage from './pages/JoinGamePage';
import './App.css';
import {useWebSocket} from "./websocket/WebSocketProvider";
import GameBoard from "./GameUI/GameBoard.jsx";
import WinLoseModal from "./modals/WinLoseModal.jsx";
import {useEffect, useState} from "react";


function App() {
    const {messages} = useWebSocket();
    const [isWin, setIsWin] = useState(null);
    const [modalVisible, setModalVisible] = useState(false);

    const closeModal = () => {
        setModalVisible(false);
    };
    useEffect(() => {
        const endGameMessage = messages.find((msg) => msg.type === "partitaFinita");
        if (endGameMessage) {
            setModalVisible(true);
            if (endGameMessage.flag === "Vittoria") {
                setIsWin(true);
            } else if (endGameMessage.flag === "Sconfitta") {
                setIsWin(false);
            }
        }
    }, [messages]);
    return (
        <>
            {/* Titolo in alto a destra */}
            <h1 className="titolo">NewMonopolyGame</h1>


            {/* Contenitore principale */}
            <div className="main-container">
                {/* Configura le rotte */}
                <Routes>
                    <Route path="*" element={<WelcomePage/>}/> {/* WelcomePage come rotta predefinita */}
                    <Route path="/create" element={<CreateGamePage/>}/>
                    <Route path="/join" element={<JoinGamePage/>}/>
                    <Route path="/play" element={<GameBoard/>}/>
                </Routes>
            </div>
            {/* Modal per vittoria o sconfitta */}
            <WinLoseModal
                visible={modalVisible}
                title={isWin ? "Hai Vinto!" : "Hai Perso!"}
                message={isWin ? "Congratulazioni, sei il vincitore!" : "Peccato, riprova!"}
                onClose={closeModal}
            />

        </>
    );
}

export default App;
