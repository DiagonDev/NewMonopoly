import React from "react";
import "./WinLoseModal.css"; // Stile per il modal

const WinLoseModal = ({ visible, title, message, onClose }) => {
    if (!visible) return null; // Rendi il modal invisibile se `visible` è false

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>{title}</h2>
                <p>{message}</p>
                <button className="close-button" onClick={onClose}>
                    Chiudi
                </button>
            </div>
        </div>
    );
};

export default WinLoseModal;
