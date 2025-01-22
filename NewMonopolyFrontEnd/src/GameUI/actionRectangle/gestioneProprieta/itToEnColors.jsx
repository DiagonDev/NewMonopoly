const italianToEnglishColors = {
    rosso: "red",
    verde: "green",
    blu: "blue",
    giallo: "yellow",
    nero: "black",
    bianco: "white",
    grigio: "gray",
    arancione: "orange",
    viola: "purple",
    marrone: "brown",
    rosa: "pink",
};

export const translateColor = (italianColor) => {
    if (!italianColor) return "transparent"; // Valore di default se `italianColor` è null o undefined
    return italianToEnglishColors[italianColor.toLowerCase()] || "transparent"; // Usa "transparent" se il colore non è mappato
};
