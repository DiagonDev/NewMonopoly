import { Routes, Route, Link } from 'react-router-dom';
import CreateGamePage from './CreateGamePage';
import JoinGamePage from './JoinGamePage';
import './WelcomePage.css';

function WelcomePage() {
    return (
        <>
            <div className="sections">
                {/* Link alla pagina "Crea Partita" */}
                <div className="section">
                    <h2>Crea Partita</h2>
                    <p>
                        Inserisci i dettagli per creare una nuova partita.
                    </p>
                    <Link to="/create">
                        <button>Crea Partita</button>
                    </Link>
                </div>

                {/* Link alla pagina "Partecipa a Partita" */}
                <div className="section">
                    <h2>Partecipa a Partita</h2>
                    <p>
                        Inserisci il tuo nome e l'ID della partita per partecipare.
                    </p>
                    <Link to="/join">
                        <button>Partecipa a Partita</button>
                    </Link>
                </div>
            </div>

            {/* Configura le rotte */}
            <Routes>
                <Route path="/create" element={<CreateGamePage />} />
                <Route path="/join" element={<JoinGamePage />} />
            </Routes>
        </>
    );
}
export default WelcomePage;