import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faLightbulb } from '@fortawesome/free-solid-svg-icons';
import { faSubway } from '@fortawesome/free-solid-svg-icons';
import { NyThemeData } from "../NyTheme";
import {pawnColors} from "../../../pages/pawnColors.jsx";

export const UtilityDisplay = ({ id, players }) => {
    const txt = NyThemeData.get(id)?.name;
    const icon = NyThemeData.get(id)?.icon;

    const getSubwayCompany = () => {
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
                    <FontAwesomeIcon icon={faSubway} size="3x" color="blue"/>
                </div>
                <div className="square-name">{txt}</div>

            </React.Fragment>
        );
    };

    const getElectricCompany = () => {
        return (
            <React.Fragment>
                <div className="blank"></div>
                <div className="icon">
                    <FontAwesomeIcon icon={faLightbulb} size="3x" color="blue"/>
                </div>
                <div className="square-name">{txt}</div>
            </React.Fragment>
        );
    };

    return icon === "subway" ? getSubwayCompany() : getElectricCompany();
};

export default UtilityDisplay;
