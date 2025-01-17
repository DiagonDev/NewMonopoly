import React from "react";
import { SquareConfigData } from "./SquareData";
import { SquareType } from "./SquareType";
import { AirportDisplay } from "./squares/AirportDisplay";
import { ChanceDisplay } from "./squares/ChanceDisplay";
import { PropertyDisplay } from "./squares/PropertyDisplay";
import { CentralParkDisplay } from "./squares/CentralParkDisplay";
import { GoDisplay } from "./squares/GoDisplay";
import { UtilityDisplay } from "./squares/UtilityDisplay";

export const SquareInfo = ({ id, players }) => {

    const type = SquareConfigData.get(id)?.type;

    const getInfo = () => {
        if (type === SquareType.Airport) {
            return <AirportDisplay id={id} players={players}/>;
        }
        if (type === SquareType.Chance) {
            return <ChanceDisplay id={id} players={players}/>;
        }
        if (type === SquareType.CentralPark) {
            return <CentralParkDisplay id={id} players={players}/>;
        }
        if (type === SquareType.Go) {
            return <GoDisplay id={id} players={players} />;
        }
        if (type === SquareType.Utility) {
            return <UtilityDisplay id={id} players={players} />;
        }

        if (type === SquareType.Jail || type === SquareType.GoToJail) {
            return null;
        }

        return <PropertyDisplay id={id} players={players}/>;
    };

    return (
        getInfo()
    );

};

export default SquareInfo;
