import React from "react";
import { NyThemeData } from "../NyTheme";
import { ColorBar } from "./ColorBar";
import {pawnColors} from "../../pages/pawnColors.jsx";

export const PropertyDisplay = ({ id, players }) => {
    const txt = NyThemeData.get(id)?.name;

    return (
        <React.Fragment>
            <ColorBar id={id}/>
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
            <div className="square-name">{txt}</div>
        </React.Fragment>
    );
};

export default PropertyDisplay;
