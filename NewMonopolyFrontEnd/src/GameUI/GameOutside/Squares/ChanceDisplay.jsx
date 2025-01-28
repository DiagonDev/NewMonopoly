import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faQuestion } from '@fortawesome/free-solid-svg-icons';
import {pawnColors} from "../../pages/pawnColors.jsx";

export const ChanceDisplay = ({ id, players }) => {
    return (
        <React.Fragment>
            <div className="blank"></div>
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
            <div className="icon">
                <FontAwesomeIcon icon={faQuestion} size="3x" color="orange"/>
            </div>
            <div className="square-name">CHANCE</div>
        </React.Fragment>
    );
};

export default ChanceDisplay;