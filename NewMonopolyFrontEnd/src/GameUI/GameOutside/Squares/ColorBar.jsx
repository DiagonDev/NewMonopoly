import React from "react";
import { SquareConfigData} from "../SquareData";
import {squareGroupColorMap} from "../SquareGroupColorMap";

export const ColorBar = ({ id }) => {
    const groupId = SquareConfigData.get(id)?.groupId;

    const getClassName = () => {
        return "square-color-bar " + squareGroupColorMap.get(groupId);
    };

    return (
        <div className={getClassName()}></div>
    );
};

export default ColorBar;
