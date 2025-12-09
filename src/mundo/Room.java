package mundo;

import entidades.Monster;
import items.Chest;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private static final int DEFAULT_WIDTH = 60;
    private static final int DEFAULT_HEIGHT = 30;

    private Map mapa;
    private final List<Monster> monstros;
    private final List<Chest> chests;
    private final RoomType type;

    // Estado
    private boolean discovered;
    private boolean cleared;

    // Conectividade (portas)
    private boolean hasNorthDoor;
    private boolean hasSouthDoor;
    private boolean hasEastDoor;
    private boolean hasWestDoor;

    public Room(RoomType type) {
        this.type = type;
        this.monstros = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.discovered = false;
        this.cleared = false;
        this.hasNorthDoor = false;
        this.hasSouthDoor = false;
        this.hasEastDoor = false;
        this.hasWestDoor = false;
    }

    /**
     * Gera o mapa da sala baseado nas portas configuradas
     */
    public void generateMap() {
        mapa = new Map(
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                hasNorthDoor,
                hasSouthDoor,
                hasEastDoor,
                hasWestDoor
        );
    }

    /**
     * Verifica se todos os monstros foram derrotados
     */
    public void checkCleared() {
        cleared = monstros.stream().allMatch(m -> !m.alive());
    }

    public void setDoor(String direction, boolean value) {
        switch (direction.toLowerCase()) {
            case "north" -> hasNorthDoor = value;
            case "south" -> hasSouthDoor = value;
            case "east" -> hasEastDoor = value;
            case "west" -> hasWestDoor = value;
        }
    }

    public boolean hasNorthDoor() { return hasNorthDoor; }
    public boolean hasSouthDoor() { return hasSouthDoor; }
    public boolean hasEastDoor() { return hasEastDoor; }
    public boolean hasWestDoor() { return hasWestDoor; }

    public void setDiscovered(boolean value) {
        this.discovered = value;
    }

    public boolean discovered() { return discovered; }
    public boolean cleared() { return cleared; }

    public Map mapa() { return mapa; }
    public List<Monster> monstros() { return monstros; }
    public List<Chest> chests() { return chests; }
    public RoomType type() { return type; }


    /**
     * Tipos de sala disponíveis
     */
    public enum RoomType {
        NORMAL("Normal Room"),
        START("Starting Room"),
        BOSS("Boss Room"),
        TREASURE("Treasure Room");

        private final String displayName;

        RoomType(String displayName) {
            this.displayName = displayName;
        }

    }
}