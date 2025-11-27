package mundo;

public enum Bloco {
    CHAO("."),
    PAREDE("#");

    public final String simbolo;

    Bloco(String simbolo) {
        this.simbolo = simbolo;
    }
}
