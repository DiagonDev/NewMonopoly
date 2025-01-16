import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHandPointLeft } from '@fortawesome/free-solid-svg-icons';
import {invisiblePawns} from './InvisiblePawns.jsx';

export const GoDisplay = ({ id }) => {
    return (
        <React.Fragment>
            <div className="blank"></div>
            <div className="pawns-container">
                {invisiblePawns.map((pawn) => (
                        <span className="pawn-id">{pawn.id}</span>
                ))}
            </div>
            <div className="icon">
                <FontAwesomeIcon icon={faHandPointLeft} color="green" />
            </div>
            <div className="square-name">GO</div>
        </React.Fragment>
    );
};

export default GoDisplay;
