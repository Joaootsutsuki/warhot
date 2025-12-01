package mundo;

public enum Bloco {
    CHAO("."),
    PAREDE("#"),
    STAIRS_DOWN("↓"),
    STAIRS_UP("↑"),
    DOOR_NORTH("^"),
    DOOR_SOUTH("v"),
    DOOR_EAST(">"),
    DOOR_WEST("<");

    public final String simbolo;

    Bloco(String simbolo) {
        this.simbolo = simbolo;
    }

    public boolean isDoor() {
        return this == DOOR_NORTH || this == DOOR_SOUTH ||
                this == DOOR_EAST || this == DOOR_WEST;
    }

    public boolean isStairs() {
        return this == STAIRS_UP || this == STAIRS_DOWN;
    }
}