import {pawnColors} from "../../../pages/pawnColors.jsx";

export const invisiblePawns = Array.from({length: 6}, (_, i) => ({
    id: i + 1,
    color: pawnColors[i+1] || "gray",
}));
