import React from "react";
import "./ErrorModal.css"; // Per uno stile personalizzato

const ErrorModal = ({ message, onClose }) => {
    if (!message) return null; // Non mostra nulla se non c'è un messaggio

    return (
        <div className="error-modal-overlay">
            <div className="error-modal">
                <h2>Errore</h2>
                <p>{message}</p>
                <button onClick={onClose}>Chiudi</button>
            </div>
        </div>
    );
};

export default ErrorModal;
