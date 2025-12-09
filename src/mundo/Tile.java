package mundo;

public enum Tile {
    FLOOR("."),
    WALL("#"),
    DOOR_NORTH("^"),
    DOOR_SOUTH("v"),
    DOOR_EAST(">"),
    DOOR_WEST("<");

    public final String symbol;

    Tile(String symbol) {
        this.symbol = symbol;
    }

    public boolean isDoor() {
        return this == DOOR_NORTH ||
                this == DOOR_SOUTH ||
                this == DOOR_EAST ||
                this == DOOR_WEST;
    }
}