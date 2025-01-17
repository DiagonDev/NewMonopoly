import React from "react";
import {pawnColors} from "../../../pages/pawnColors.jsx";

export const CentralParkDisplay = ({ id, players }) => {
    return (
        <React.Fragment>
            <div className="icon"></div>
            <div className="pawn-onMove">
                {players.map((player) => (
                    <span
                        key={player}
                        className={`pawn-move`}
                        style={{backgroundColor: pawnColors[player + 1]}}
                    >
                    </span>
                ))}
            </div>
            <div className="square-name">central park</div>
        </React.Fragment>
    );
};

export default CentralParkDisplay;