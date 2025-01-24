import "./ErrorModal.css"; // Per uno stile personalizzato

// eslint-disable-next-line react/prop-types
const ErrorModal = ({message, onClose}) => {
    if (!message) return null;
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
