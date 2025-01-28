import React from "react";
import { NyThemeData } from "../NyTheme";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlane } from '@fortawesome/free-solid-svg-icons';
import {pawnColors} from "../../pages/pawnColors.jsx";

export const AirportDisplay = ({ id, players }) => {
    const txt = NyThemeData.get(id)?.name;

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
                <FontAwesomeIcon icon={faPlane} size="3x"/>
            </div>
            <div className="square-name">{txt}</div>
        </React.Fragment>
    );
};

export default AirportDisplay;