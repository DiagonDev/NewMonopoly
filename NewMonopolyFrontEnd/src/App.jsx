import {Routes, Route, Link} from 'react-router-dom';
import WelcomePage from './pages/WelcomePage'; // Importa il componente WelcomePage
import CreateGamePage from './pages/CreateGamePage';
import JoinGamePage from './pages/JoinGamePage';
import './App.css';
import {WebSocketProvider} from "./contexts/WebSocketContext";
import GameBoard from "./GameUI/GameBoard.jsx";
import WinLoseModal from "./modals/WinLoseModal.jsx";
import {useContext, useEffect, useState} from "react";


function App() {
    const [isWin, setIsWin] = useState(null);
    const [modalVisible, setModalVisible] = useState(false);
    const {partitaFinita} = useContext(WebSocketProvider);
    const closeModal = () => {
        setModalVisible(false);
    };

    useEffect(() => {
        if (partitaFinita.equals("Vittoria")) {
            setIsWin(true);
        }
        else if (partitaFinita.equals("Sconfita")) {
            setIsWin(false);
        }
    }, [partitaFinita]);
    return (
        <WebSocketProvider>
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
        </WebSocketProvider>
    );
}

export default App;
