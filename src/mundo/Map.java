package mundo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private Tile[][] grid;
    private final List<RoomBounds> rooms;
    private final Random rand;

    private final int width;
    private final int height;

    /**
     * Construtor com portas específicas
     */
    public Map(int width, int height, boolean north, boolean south, boolean east, boolean west) {
        this.width = width;
        this.height = height;
        this.rand = new Random();
        this.rooms = new ArrayList<>();

        this.grid = createEmptyGrid();

        generateRooms();

        connectRooms();

        placeDoors(north, south, east, west);
    }

    /**
     * Cria grid vazio (todas paredes)
     */
    private Tile[][] createEmptyGrid() {
        Tile[][] emptyGrid = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                emptyGrid[y][x] = Tile.WALL;
            }
        }
        return emptyGrid;
    }

    /**
     * Gera salas aleatórias sem sobreposição
     */
    private void generateRooms() {
        int maxAttempts = 100;
        int roomCount = 5 + rand.nextInt(6); // 5-10 rooms

        for (int i = 0; i < maxAttempts && rooms.size() < roomCount; i++) {
            int w = 6 + rand.nextInt(12);
            int h = 4 + rand.nextInt(8);
            int x = 2 + rand.nextInt(width - w - 4);
            int y = 2 + rand.nextInt(height - h - 4);

            RoomBounds newRoom = new RoomBounds(x, y, w, h);

            boolean overlaps = false;
            for (RoomBounds room : rooms) {
                if (newRoom.intersects(room)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                rooms.add(newRoom);
                carveRoom(newRoom);
            }
        }
    }

    /**
     * Cava uma sala no grid
     */
    private void carveRoom(RoomBounds room) {
        for (int y = room.y(); y < room.y() + room.height(); y++) {
            for (int x = room.x(); x < room.x() + room.width(); x++) {
                grid[y][x] = Tile.FLOOR;
            }
        }
    }

    /**
     * Conecta todas as salas com corredores
     */
    private void connectRooms() {
        if (rooms.isEmpty()) return;

        // Conecta salas adjacentes
        for (int i = 1; i < rooms.size(); i++) {
            RoomBounds a = rooms.get(i - 1);
            RoomBounds b = rooms.get(i);
            carveCorridor(a.centerX(), a.centerY(), b.centerX(), b.centerY());
        }

        RoomBounds first = rooms.get(0);
        carveCorridor(first.centerX(), first.centerY(), width / 2, height / 2);
    }

    /**
     * Cava um corredor em L entre dois pontos
     */
    private void carveCorridor(int x1, int y1, int x2, int y2) {
        int x = x1;
        int y = y1;

        // Horizontal primeiro
        while (x != x2) {
            grid[y][x] = Tile.FLOOR;
            x += (x < x2) ? 1 : -1;
        }

        // Depois vertical
        while (y != y2) {
            grid[y][x] = Tile.FLOOR;
            y += (y < y2) ? 1 : -1;
        }

        grid[y][x] = Tile.FLOOR;
    }

    /**
     * Coloca portas nas bordas e cria caminhos até elas
     */
    public void placeDoors(boolean north, boolean south, boolean east, boolean west) {
        int centerX = width / 2;
        int centerY = height / 2;

        if (north) {
            // Coloca porta norte
            grid[0][centerX] = Tile.DOOR_NORTH;
            grid[0][centerX + 1] = Tile.DOOR_NORTH;

            // Cria caminho até o centro
            for (int y = 1; y <= centerY; y++) {
                grid[y][centerX] = Tile.FLOOR;
                grid[y][centerX + 1] = Tile.FLOOR;
            }
        }

        if (south) {
            // Coloca porta sul
            grid[height - 1][centerX] = Tile.DOOR_SOUTH;
            grid[height - 1][centerX + 1] = Tile.DOOR_SOUTH;

            // Cria caminho até o centro
            for (int y = height - 2; y >= centerY; y--) {
                grid[y][centerX] = Tile.FLOOR;
                grid[y][centerX + 1] = Tile.FLOOR;
            }
        }

        if (east) {
            // Coloca porta leste
            grid[centerY][width - 1] = Tile.DOOR_EAST;
            grid[centerY + 1][width - 1] = Tile.DOOR_EAST;

            // Cria caminho até o centro
            for (int x = width - 2; x >= centerX; x--) {
                grid[centerY][x] = Tile.FLOOR;
                grid[centerY + 1][x] = Tile.FLOOR;
            }
        }

        if (west) {
            // Coloca porta oeste
            grid[centerY][0] = Tile.DOOR_WEST;
            grid[centerY + 1][0] = Tile.DOOR_WEST;

            // Cria caminho até o centro
            for (int x = 1; x <= centerX; x++) {
                grid[centerY][x] = Tile.FLOOR;
                grid[centerY + 1][x] = Tile.FLOOR;
            }
        }
    }

    // ========== Consultas de Tiles ==========

    public Tile getTile(int y, int x) {
        if (!isInBounds(x, y)) {
            return Tile.WALL;
        }
        return grid[y][x];
    }

    public boolean canWalk(int x, int y) {
        if (!isInBounds(x, y)) {
            return false;
        }
        Tile tile = grid[y][x];
        return tile == Tile.FLOOR || tile.isDoor();
    }

    public boolean isDoor(int x, int y) {
        return isInBounds(x, y) && grid[y][x].isDoor();
    }

    public Tile getDoorType(int x, int y) {
        return isDoor(x, y) ? grid[y][x] : null;
    }

    /**
     * Retorna posição aleatória válida
     */
    public Position randomPosition() {
        if (rooms.isEmpty()) {
            return new Position(width / 2, height / 2);
        }

        // Usa primeira sala
        RoomBounds firstRoom = rooms.get(0);
        return findValidPositionInRoom(firstRoom, 3);
    }

    /**
     * Encontra posição válida dentro de uma sala
     */
    private Position findValidPositionInRoom(RoomBounds room, int margin) {
        int usableWidth = Math.max(1, room.width() - 2 * margin);
        int usableHeight = Math.max(1, room.height() - 2 * margin);

        for (int attempt = 0; attempt < 50; attempt++) {
            int x = room.x() + margin + rand.nextInt(usableWidth);
            int y = room.y() + margin + rand.nextInt(usableHeight);

            // Verifica se área 2x2 é válida
            if (is2x2Walkable(x, y)) {
                return new Position(x, y);
            }
        }

        // Fallback: centro da sala
        return new Position(room.centerX(), room.centerY());
    }

    /**
     * Verifica se área 2x2 é caminhável
     */
    private boolean is2x2Walkable(int x, int y) {
        return canWalk(x, y) &&
                canWalk(x + 1, y) &&
                canWalk(x, y + 1) &&
                canWalk(x + 1, y + 1);
    }


    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Converte grid para array de strings (para renderização)
     */
    public String[][] asStringArray() {
        String[][] arr = new String[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                arr[y][x] = grid[y][x].symbol;
            }
        }
        return arr;
    }
}