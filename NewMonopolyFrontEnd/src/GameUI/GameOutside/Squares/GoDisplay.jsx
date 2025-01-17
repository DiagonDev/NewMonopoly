import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHandPointLeft } from '@fortawesome/free-solid-svg-icons';
import {pawnColors} from "../../../pages/pawnColors.jsx";

// eslint-disable-next-line react/prop-types
export const GoDisplay = ({id, players}) => {
    return (
        <React.Fragment>
            <div className="blank"></div>
            <div className="pawns-onMove">
                {players.map((player) => (
                    <span
                        key={player}
                        className={`pawn-move`}
                        style={{ backgroundColor: pawnColors[player+1] }}
                    >
                    </span>
                ))}
            </div>
            <div className="icon">
                <FontAwesomeIcon icon={faHandPointLeft} color="green"/>
            </div>
            <div className="square-name">GO</div>
        </React.Fragment>
    );
};

export default GoDisplay;
