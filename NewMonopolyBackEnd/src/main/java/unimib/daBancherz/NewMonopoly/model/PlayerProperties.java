package unimib.daBancherz.NewMonopoly.model;

public class PlayerProperties {
    private Integer prezzoCorrente;
    private Integer idGiocatore;
    private Integer numCasa;
    private Integer prezzoCasaCorrente;
    private String nome;
    private String colore;
    private Integer affitto;
    private Integer ipoteca;
    private Integer affitto1Casa;
    private Integer affitto2Case;
    private Integer affitto3Case;
    private Integer affitto4Case;
    private Integer affittoAlbergo;

    public PlayerProperties() {}

    public PlayerProperties(Integer prezzoCorrente, Integer idGiocatore, Integer numCasa, Integer prezzoCasaCorrente,
                            String nome, String colore, Integer affitto, Integer affitto1Casa, Integer affitto2Case,
                            Integer affitto3Case, Integer affitto4Case, Integer affittoAlbergo, Integer ipoteca) {
        this.prezzoCorrente = prezzoCorrente;
        this.idGiocatore = idGiocatore;
        this.numCasa = numCasa;
        this.prezzoCasaCorrente = prezzoCasaCorrente;
        this.nome = nome;
        this.colore = colore;
        this.affitto = affitto;
        this.affitto1Casa = affitto1Casa;
        this.affitto2Case = affitto2Case;
        this.affitto3Case = affitto3Case;
        this.affitto4Case = affitto4Case;
        this.affittoAlbergo = affittoAlbergo;
        this.ipoteca = ipoteca;
    }

    public Integer getAffittoAlbergo() {
        return affittoAlbergo;
    }

    public void setAffittoAlbergo(Integer affittoAlbergo) {
        this.affittoAlbergo = affittoAlbergo;
    }

    public Integer getAffitto4Case() {
        return affitto4Case;
    }

    public void setAffitto4Case(Integer affitto4Case) {
        this.affitto4Case = affitto4Case;
    }

    public Integer getAffitto3Case() {
        return affitto3Case;
    }

    public void setAffitto3Case(Integer affitto3Case) {
        this.affitto3Case = affitto3Case;
    }

    public Integer getAffitto2Case() {
        return affitto2Case;
    }

    public void setAffitto2Case(Integer affitto2Case) {
        this.affitto2Case = affitto2Case;
    }

    public Integer getAffitto1Casa() {
        return affitto1Casa;
    }

    public void setAffitto1Casa(Integer affitto1Casa) {
        this.affitto1Casa = affitto1Casa;
    }

    public Integer getIpoteca() {
        return ipoteca;
    }

    public void setIpoteca(Integer ipoteca) {
        this.ipoteca = ipoteca;
    }

    public Integer getAffitto() {
        return affitto;
    }

    public void setAffitto(Integer affitto) {
        this.affitto = affitto;
    }

    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getPrezzoCasaCorrente() {
        return prezzoCasaCorrente;
    }

    public void setPrezzoCasaCorrente(Integer prezzoCasaCorrente) {
        this.prezzoCasaCorrente = prezzoCasaCorrente;
    }

    public Integer getNumCasa() {
        return numCasa;
    }

    public void setNumCasa(Integer numCasa) {
        this.numCasa = numCasa;
    }

    public Integer getIdGiocatore() {
        return idGiocatore;
    }

    public void setIdGiocatore(Integer idGiocatore) {
        this.idGiocatore = idGiocatore;
    }

    public Integer getPrezzoCorrente() {
        return prezzoCorrente;
    }

    public void setPrezzoCorrente(Integer prezzoCorrente) {
        this.prezzoCorrente = prezzoCorrente;
    }
}

