import "./WinLoseModal.css"; // Stile per il modal

// eslint-disable-next-line react/prop-types
const WinLoseModal = ({visible, title, message, onClose}) => {
    if (!visible) return null;

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
