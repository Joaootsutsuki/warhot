package mundo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mapa {
    private Bloco[][] grade;
    private Random r = new Random();
    private List<Sala> salas;
    private Position northDoorPos;
    private Position southDoorPos;
    private Position eastDoorPos;
    private Position westDoorPos;

    public Mapa(int largura, int altura) {
        grade = new Bloco[altura][largura];
        salas = new ArrayList<>();

        for (int y = 0; y < altura; y++)
            for (int x = 0; x < largura; x++)
                grade[y][x] = Bloco.PAREDE;

        // Note: placeDoors will be called BEFORE gerarSalas in setupRoom()
        // This is handled in Jogo.java
    }

    public Mapa(int largura, int altura, boolean north, boolean south, boolean east, boolean west) {
        grade = new Bloco[altura][largura];
        salas = new ArrayList<>();

        for (int y = 0; y < altura; y++)
            for (int x = 0; x < largura; x++)
                grade[y][x] = Bloco.PAREDE;

        // generate rooms FIRST
        gerarSalas();

        // THEN place doors and force carve paths to them
        placeDoors(north, south, east, west);
    }

    public void placeDoors(boolean north, boolean south, boolean east, boolean west) {
        int centerX = grade[0].length / 2;
        int centerY = grade.length / 2;

        if (north) {
            northDoorPos = new Position(centerX, 0);
            // ´place door
            for (int i = 0; i <= 1; i++) {
                grade[0][centerX + i] = Bloco.DOOR_NORTH;
            }
            // force carve corridor from door to center
            for (int y = 1; y <= centerY; y++) {
                grade[y][centerX] = Bloco.CHAO;
                grade[y][centerX + 1] = Bloco.CHAO;
            }
        }

        if (south) {
            southDoorPos = new Position(centerX, grade.length - 1);
            // place door
            for (int i = 0; i <= 1; i++) {
                grade[grade.length - 1][centerX + i] = Bloco.DOOR_SOUTH;
            }
            // force carve corridor from door to center
            for (int y = grade.length - 2; y >= centerY; y--) {
                grade[y][centerX] = Bloco.CHAO;
                grade[y][centerX + 1] = Bloco.CHAO;
            }
        }

        if (east) {
            eastDoorPos = new Position(grade[0].length - 1, centerY);
            // place door
            for (int i = 0; i <= 1; i++) {
                grade[centerY + i][grade[0].length - 1] = Bloco.DOOR_EAST;
            }
            // force carve corridor from door to center
            for (int x = grade[0].length - 2; x >= centerX; x--) {
                grade[centerY][x] = Bloco.CHAO;
                grade[centerY + 1][x] = Bloco.CHAO;
            }
        }

        if (west) {
            westDoorPos = new Position(0, centerY);
            // place door
            for (int i = 0; i <= 1; i++) {
                grade[centerY + i][0] = Bloco.DOOR_WEST;
            }
            // force carve corridor from door to center
            for (int x = 1; x <= centerX; x++) {
                grade[centerY][x] = Bloco.CHAO;
                grade[centerY + 1][x] = Bloco.CHAO;
            }
        }
    }

    private void gerarSalas() {
        int tentativas = 0;
        int maxTentativas = 50;
        int salasCriadas = 0;
        int salasDesejadas = 6 + r.nextInt(4);

        // get door positions to avoid them
        int centerX = grade[0].length / 2;
        int centerY = grade.length / 2;

        while (salasCriadas < salasDesejadas && tentativas < maxTentativas) {
            int largura = 6 + r.nextInt(8);
            int altura = 5 + r.nextInt(6);
            int x = 1 + r.nextInt(grade[0].length - largura - 2);
            int y = 1 + r.nextInt(grade.length - altura - 2);

            Sala novaSala = new Sala(x, y, largura, altura);

            // check if room overlaps with door areas
            boolean bloqueiaPorta = false;

            // check north door area
            if (novaSala.y() <= 5 && novaSala.x() <= centerX + 3 && novaSala.x() + novaSala.largura() >= centerX - 2) {
                bloqueiaPorta = true;
            }

            // check south door area
            if (novaSala.y() + novaSala.altura() >= grade.length - 5 &&
                    novaSala.x() <= centerX + 3 && novaSala.x() + novaSala.largura() >= centerX - 2) {
                bloqueiaPorta = true;
            }

            // check east door area
            if (novaSala.x() + novaSala.largura() >= grade[0].length - 5 &&
                    novaSala.y() <= centerY + 3 && novaSala.y() + novaSala.altura() >= centerY - 2) {
                bloqueiaPorta = true;
            }

            // check west door area
            if (novaSala.x() <= 5 && novaSala.y() <= centerY + 3 && novaSala.y() + novaSala.altura() >= centerY - 2) {
                bloqueiaPorta = true;
            }

            boolean sobrepoe = false;
            for (Sala sala : salas) {
                if (novaSala.intersecta(sala)) {
                    sobrepoe = true;
                    break;
                }
            }

            if (!sobrepoe && !bloqueiaPorta) {
                criarSala(novaSala);

                if (!salas.isEmpty()) {
                    Sala salaAnterior = salas.get(salas.size() - 1);

                    if (r.nextBoolean()) {
                        conectarSalasLinear(salaAnterior, novaSala);
                    } else {
                        Sala salaAleatoria = salas.get(r.nextInt(salas.size()));
                        conectarSalasLinear(salaAleatoria, novaSala);
                    }
                }

                salas.add(novaSala);
                salasCriadas++;
            }

            tentativas++;
        }
    }

    private void criarSala(Sala sala) {
        for (int y = sala.y(); y < sala.y() + sala.altura(); y++) {
            for (int x = sala.x(); x < sala.x() + sala.largura(); x++) {
                if (dentroDosLimites(x, y)) {
                    // don't overwrite doors or existing floor
                    if (grade[y][x].isDoor() || grade[y][x] == Bloco.CHAO) {
                        continue;
                    }

                    boolean isBorder = (x == sala.x() || x == sala.x() + sala.largura() - 1 ||
                            y == sala.y() || y == sala.y() + sala.altura() - 1);

                    if (isBorder) {
                        grade[y][x] = Bloco.PAREDE;
                    } else {
                        grade[y][x] = Bloco.CHAO;
                    }
                }
            }
        }
    }

    private void conectarSalasLinear(Sala sala1, Sala sala2) {
        int x1 = sala1.centroX();
        int y1 = sala1.centroY();
        int x2 = sala2.centroX();
        int y2 = sala2.centroY();

        if (r.nextBoolean()) {
            criarCorredorHorizontal(x1, x2, y1);
            criarCorredorVertical(y1, y2, x2);
        } else {
            criarCorredorVertical(y1, y2, x1);
            criarCorredorHorizontal(x1, x2, y2);
        }

        breakWallsAtConnection(sala1);
        breakWallsAtConnection(sala2);
    }

    private void breakWallsAtConnection(Sala sala) {
        for (int y = sala.y(); y < sala.y() + sala.altura(); y++) {
            for (int x = sala.x(); x < sala.x() + sala.largura(); x++) {
                if (dentroDosLimites(x, y) && grade[y][x] == Bloco.PAREDE) {
                    boolean nextToFloor = false;
                    int[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
                    for (int[] dir : dirs) {
                        int nx = x + dir[0];
                        int ny = y + dir[1];
                        if (dentroDosLimites(nx, ny) && grade[ny][nx] == Bloco.CHAO) {
                            if (nx < sala.x() || nx >= sala.x() + sala.largura() ||
                                    ny < sala.y() || ny >= sala.y() + sala.altura()) {
                                nextToFloor = true;
                                break;
                            }
                        }
                    }
                    if (nextToFloor) {
                        grade[y][x] = Bloco.CHAO;
                    }
                }
            }
        }
    }

    private void criarCorredorHorizontal(int x1, int x2, int y) {
        int inicio = Math.min(x1, x2);
        int fim = Math.max(x1, x2);

        for (int x = inicio; x <= fim; x++) {
            if (dentroDosLimites(x, y) && !grade[y][x].isDoor()) {
                grade[y][x] = Bloco.CHAO;
            }
            if (dentroDosLimites(x, y + 1) && !grade[y + 1][x].isDoor()) {
                grade[y + 1][x] = Bloco.CHAO;
            }
        }
    }

    private void criarCorredorVertical(int y1, int y2, int x) {
        int inicio = Math.min(y1, y2);
        int fim = Math.max(y1, y2);

        for (int y = inicio; y <= fim; y++) {
            if (dentroDosLimites(x, y) && !grade[y][x].isDoor()) {
                grade[y][x] = Bloco.CHAO;
            }
            if (dentroDosLimites(x + 1, y) && !grade[y][x + 1].isDoor()) {
                grade[y][x + 1] = Bloco.CHAO;
            }
        }
    }

    public void placeStairs(boolean hasUp, boolean hasDown) {
        if (salas.isEmpty())
            return;

        // place stairs in center of last room
        Sala lastRoom = salas.get(salas.size() - 1);
        int centerX = lastRoom.centroX();
        int centerY = lastRoom.centroY();

        if (hasUp) {
            // place stairs up at top-left of center
            grade[centerY][centerX] = Bloco.STAIRS_UP;
            grade[centerY][centerX + 1] = Bloco.STAIRS_UP;
            grade[centerY + 1][centerX] = Bloco.CHAO;
            grade[centerY + 1][centerX + 1] = Bloco.CHAO;
        }

        if (hasDown) {
            // place stairs down at bottom-right of center
            int offsetX = hasUp ? 4 : 0;
            grade[centerY][centerX + offsetX] = Bloco.STAIRS_DOWN;
            grade[centerY][centerX + offsetX + 1] = Bloco.STAIRS_DOWN;
            grade[centerY + 1][centerX + offsetX] = Bloco.CHAO;
            grade[centerY + 1][centerX + offsetX + 1] = Bloco.CHAO;
        }
    }

    public boolean dentroDosLimites(int x, int y) {
        return y >= 0 && y < grade.length && x >= 0 && x < grade[0].length;
    }

    public boolean podeAndar(int x, int y) {
        return dentroDosLimites(x, y) &&
                (grade[y][x] == Bloco.CHAO || grade[y][x].isDoor() || grade[y][x].isStairs());
    }

    public boolean isDoor(int x, int y) {
        return dentroDosLimites(x, y) && grade[y][x].isDoor();
    }

    public boolean isStairs(int x, int y) {
        return dentroDosLimites(x, y) && grade[y][x].isStairs();
    }

    public Bloco getDoorType(int x, int y) {
        if (isDoor(x, y)) {
            return grade[y][x];
        }
        return null;
    }

    public Bloco getStairsType(int x, int y) {
        if (isStairs(x, y)) {
            return grade[y][x];
        }
        return null;
    }

    public Position posicaoAleatoria() {
        if (salas.isEmpty()) {
            return new Position(grade[0].length / 2, grade.length / 2);
        }

        Sala primeiraLala = salas.get(0);
        int margem = 3;
        int larguraUtil = Math.max(1, primeiraLala.largura() - 2 * margem);
        int alturaUtil = Math.max(1, primeiraLala.altura() - 2 * margem);

        int x = primeiraLala.x() + margem + r.nextInt(larguraUtil);
        int y = primeiraLala.y() + margem + r.nextInt(alturaUtil);

        if (podeAndar(x, y) && podeAndar(x + 1, y) && podeAndar(x, y + 1) && podeAndar(x + 1, y + 1)) {
            return new Position(x, y);
        }

        return new Position(primeiraLala.centroX(), primeiraLala.centroY());
    }

    public Position posicaoAleatoriaLongeDoJogador(Position jogadorPos, int distanciaMinima) {
        int tentativas = 0;
        int maxTentativas = 100;

        while (tentativas < maxTentativas) {
            Sala salaAleatoria = salas.get(1 + r.nextInt(Math.max(1, salas.size() - 1)));

            int margem = 3;
            int larguraUtil = Math.max(1, salaAleatoria.largura() - 2 * margem);
            int alturaUtil = Math.max(1, salaAleatoria.altura() - 2 * margem);

            int x = salaAleatoria.x() + margem + r.nextInt(larguraUtil);
            int y = salaAleatoria.y() + margem + r.nextInt(alturaUtil);

            if (podeAndar(x, y) && podeAndar(x + 1, y) && podeAndar(x, y + 1) && podeAndar(x + 1, y + 1)) {
                double distancia = Math.sqrt(Math.pow(x - jogadorPos.x(), 2) + Math.pow(y - jogadorPos.y(), 2));
                if (distancia >= distanciaMinima) {
                    return new Position(x, y);
                }
            }

            tentativas++;
        }

        if (!salas.isEmpty()) {
            Sala ultimaSala = salas.get(salas.size() - 1);
            return new Position(ultimaSala.centroX(), ultimaSala.centroY());
        }

        return new Position(grade[0].length / 2, grade.length / 2);
    }

    public String[][] comoArrayString() {
        String[][] arr = new String[grade.length][grade[0].length];
        for (int y = 0; y < grade.length; y++)
            for (int x = 0; x < grade[0].length; x++)
                arr[y][x] = grade[y][x].simbolo;
        return arr;
    }
}