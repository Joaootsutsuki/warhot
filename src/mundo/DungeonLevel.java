package mundo;

import java.util.Random;


public class DungeonLevel {
    private static final int GRID_SIZE = 5;
    private static final int MIN_ROOMS = 8;
    private static final int MAX_ROOMS = 12;

    private final Room[][] roomGrid;
    private final Random rand;

    private int currentRoomX;
    private int currentRoomY;

    public DungeonLevel() {
        this.roomGrid = new Room[GRID_SIZE][GRID_SIZE];
        this.rand = new Random();

        generateLevel();
    }

    /**
     * Gera o dungeon: salas conectadas e mapas
     */
    private void generateLevel() {
        currentRoomX = GRID_SIZE / 2;
        currentRoomY = GRID_SIZE / 2;

        roomGrid[currentRoomY][currentRoomX] = new Room(Room.RoomType.START);

        generateConnectedRooms();

        generateAllMaps();
    }

    /**
     * Gera salas conectadas usando random walk
     */
    private void generateConnectedRooms() {
        int roomsToGenerate = MIN_ROOMS + rand.nextInt(MAX_ROOMS - MIN_ROOMS + 1);
        int roomsCreated = 1;

        int x = currentRoomX;
        int y = currentRoomY;

        while (roomsCreated < roomsToGenerate) {
            Direction dir = Direction.random(rand);
            int newX = x + dir.dx;
            int newY = y + dir.dy;

            if (isValidPosition(newX, newY)) {
                if (roomGrid[newY][newX] == null) {
                    Room.RoomType type = determineRoomType(roomsCreated, roomsToGenerate);
                    roomGrid[newY][newX] = new Room(type);
                    roomsCreated++;
                }

                connectRooms(x, y, newX, newY);

                x = newX;
                y = newY;
            }
        }
    }

    /**
     * Determina o tipo de sala baseado na progressão
     */
    private Room.RoomType determineRoomType(int roomIndex, int totalRooms) {
        // Última sala é sempre boss
        if (roomIndex == totalRooms - 1) {
            return Room.RoomType.BOSS;
        }

        // 20% de chance de sala de tesouro
        if (rand.nextInt(100) < 20) {
            return Room.RoomType.TREASURE;
        }

        return Room.RoomType.NORMAL;
    }

    /**
     * Conecta duas salas adjacentes com portas bidirecionais
     */
    private void connectRooms(int x1, int y1, int x2, int y2) {
        Room room1 = roomGrid[y1][x1];
        Room room2 = roomGrid[y2][x2];

        if (room1 == null || room2 == null) {
            return;
        }

        if (y2 < y1) { // Norte
            room1.setDoor("north", true);
            room2.setDoor("south", true);
        } else if (y2 > y1) { // Sul
            room1.setDoor("south", true);
            room2.setDoor("north", true);
        } else if (x2 > x1) { // Leste
            room1.setDoor("east", true);
            room2.setDoor("west", true);
        } else if (x2 < x1) { // Oeste
            room1.setDoor("west", true);
            room2.setDoor("east", true);
        }
    }

    /**
     * Gera os mapas para todas as salas
     */
    private void generateAllMaps() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (roomGrid[y][x] != null) {
                    roomGrid[y][x].generateMap();
                }
            }
        }
    }

    public boolean canMoveNorth() {
        return getCurrentRoom().hasNorthDoor() &&
                currentRoomY > 0 &&
                roomGrid[currentRoomY - 1][currentRoomX] != null;
    }

    public boolean canMoveSouth() {
        return getCurrentRoom().hasSouthDoor() &&
                currentRoomY < GRID_SIZE - 1 &&
                roomGrid[currentRoomY + 1][currentRoomX] != null;
    }

    public boolean canMoveEast() {
        return getCurrentRoom().hasEastDoor() &&
                currentRoomX < GRID_SIZE - 1 &&
                roomGrid[currentRoomY][currentRoomX + 1] != null;
    }

    public boolean canMoveWest() {
        return getCurrentRoom().hasWestDoor() &&
                currentRoomX > 0 &&
                roomGrid[currentRoomY][currentRoomX - 1] != null;
    }

    public void moveNorth() {
        if (canMoveNorth()) {
            currentRoomY--;
        }
    }

    public void moveSouth() {
        if (canMoveSouth()) {
            currentRoomY++;
        }
    }

    public void moveEast() {
        if (canMoveEast()) {
            currentRoomX++;
        }
    }

    public void moveWest() {
        if (canMoveWest()) {
            currentRoomX--;
        }
    }


    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }


    public Room getCurrentRoom() {
        return roomGrid[currentRoomY][currentRoomX];
    }

    public Room[][] getRoomGrid() {
        return roomGrid;
    }

    public int getCurrentRoomX() {
        return currentRoomX;
    }

    public int getCurrentRoomY() {
        return currentRoomY;
    }

    public int getGridSize() {
        return GRID_SIZE;
    }


    private enum Direction {
        NORTH(0, -1),
        SOUTH(0, 1),
        EAST(1, 0),
        WEST(-1, 0);

        final int dx, dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        static Direction random(Random rand) {
            Direction[] values = values();
            return values[rand.nextInt(values.length)];
        }
    }
}