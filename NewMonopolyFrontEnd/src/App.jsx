import {Routes, Route} from 'react-router-dom';
import WelcomePage from './GameUI/pages/WelcomePage'; // Importa il componente WelcomePage
import CreateGamePage from './GameUI/pages/CreateGamePage';
import JoinGamePage from './GameUI/pages/JoinGamePage';
import './App.css';
import {WebSocketContext} from "./eventListener/WebSocketContext";
import GameBoard from "./GameUI/GameBoard.jsx";
import WinLoseModal from "./GameUI/modals/WinLoseModal.jsx";
import {useContext, useEffect, useState} from "react";

function App() {
    const [isWin, setIsWin] = useState(null);
    const [modalVisible, setModalVisible] = useState(false);
    const {partitaFinita} = useContext(WebSocketContext);
    const closeModal = () => {
        setModalVisible(false);
        
    };

    useEffect(() => {
        if (partitaFinita === "vittoria") {
            setIsWin(true);
            setModalVisible(true);
        }
        else if (partitaFinita === "sconfitta") {
            setIsWin(false);
            setModalVisible(true);
        }
    }, [partitaFinita]);
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
