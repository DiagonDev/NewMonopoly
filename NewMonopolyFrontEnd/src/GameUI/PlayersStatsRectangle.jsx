import React, { useState, useEffect, useContext } from "react";

let nextId = 0;
const PlayersStatsRectangle = () => {
    const [playersName, setplayersName] = useState([]);
    return (
        <>
            <div>
                <p>Player1</p>
            </div>
            <div>
                <p>Player2</p>
            </div>
            <div>
                <p>Player3</p>
            </div>
            <div>
                <p>Player4</p>
            </div>
            <div>
                <p>Player5</p>
            </div>
            <div>
                <p>Player6</p>
            </div>
        </>
    );
}
export default PlayersStatsRectangle;