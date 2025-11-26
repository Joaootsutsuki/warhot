package mundo;

public enum Bloco {

    // tipos de colisao

    CHAO("."),
    PAREDE("#");

    public final String simbolo;

    Bloco(String simbolo) {
        this.simbolo = simbolo;
    }
}
