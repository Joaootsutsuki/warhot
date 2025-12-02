package engine;

import mundo.Mapa;
import entidades.Entidade;
import entidades.Jogador;
import items.Chest;
import java.util.List;

public class Render {
    private static final String ANSI_CLEAR = "\033[2J\033[H";
    private static final String ANSI_HIDE_CURSOR = "\033[?25l";
    private static final String ANSI_SHOW_CURSOR = "\033[?25h";

    private static StringBuilder frameBuffer = new StringBuilder(5000);

    public static void render(Mapa mapa, Jogador player, MessageLog log, List<Chest> chests, Entidade... others) {
        frameBuffer.setLength(0);

        frameBuffer.append(ANSI_HIDE_CURSOR);
        frameBuffer.append(ANSI_CLEAR);

        String[][] screen = mapa.comoArrayString();

        renderChests(screen, chests);

        renderEntities(screen, player, others);

        String sidebar = generateSidebar(player, others);
        String[] sidebarLines = sidebar.split("\n");

        for (int i = 0; i < screen.length; i++) {
            for (String cell : screen[i]) {
                frameBuffer.append(cell);
            }

            frameBuffer.append(" | ");
            if (i < sidebarLines.length) {
                frameBuffer.append(sidebarLines[i]);
            }
            frameBuffer.append("\n");
        }

        frameBuffer.append("=".repeat(90)).append("\n");

        renderLog(log);

        System.out.print(frameBuffer.toString());
        System.out.flush();
    }

    private static String generateSidebar(Jogador player, Entidade... enemies) {
        StringBuilder sb = new StringBuilder();

        String CYAN = "\u001B[36m";
        String YELLOW = "\u001B[33m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String RESET = "\u001B[0m";

        // Player info
        sb.append(String.format("%s%s%s\n", CYAN, player.nome(), RESET));
        sb.append(String.format("Level: %d\n", player.nivel()));
        sb.append("─".repeat(25)).append("\n");

        // HP Bar
        String hpBar = generateBar(player.hpAtual(), player.hpMax(), 15, RED);
        sb.append(String.format("HP: %s %d/%d\n", hpBar, player.hpAtual(), player.hpMax()));

        // Mana Bar
        String manaBar = generateBar(player.manaAtual(), player.manaMax(), 15, CYAN);
        sb.append(String.format("MP: %s %d/%d\n", manaBar, player.manaAtual(), player.manaMax()));

        sb.append("─".repeat(25)).append("\n");

        // Stats
        sb.append(String.format("%sSTATS:%s\n", YELLOW, RESET));
        sb.append(String.format("VIT:%d END:%d STR:%d\n",
                player.stats().vitality(), player.stats().endurance(), player.stats().strength()));
        sb.append(String.format("DEX:%d INT:%d LCK:%d\n",
                player.stats().dexterity(), player.stats().intelligence(), player.stats().luck()));

        sb.append("─".repeat(25)).append("\n");

        // Weight
        double currentWeight = player.inventory().getCurrentWeight();
        int maxWeight = player.stats().maxCarryWeight();
        sb.append(String.format("Weight: %.1f/%d\n", currentWeight, maxWeight));
        sb.append(String.format("XP: %d\n", player.xp()));

        // Equipped weapon
        if (player.inventory().getEquippedWeapon() != null) {
            sb.append("\n");
            sb.append(String.format("%sEquipped:%s\n", YELLOW, RESET));
            sb.append(String.format("%s\n", player.inventory().getEquippedWeapon().name()));
        }

        sb.append("\n");

        // Enemies
        sb.append(String.format("%s=== ENEMIES ===%s\n", YELLOW, RESET));
        boolean hasEnemy = false;

        for (Entidade enemy : enemies) {
            if (enemy.vivo()) {
                String enemyHpBar = generateBar(enemy.hpAtual(), enemy.hpMax(), 12, RED);
                sb.append(String.format("%s%s%s\n", GREEN, enemy.nome(), RESET));
                sb.append(String.format("HP: %s %d/%d\n", enemyHpBar, enemy.hpAtual(), enemy.hpMax()));
                sb.append("\n");
                hasEnemy = true;
            }
        }

        if (!hasEnemy) {
            sb.append("No living enemies\n");
        }

        return sb.toString();
    }

    private static String generateBar(int current, int max, int width, String color) {
        int filled = max > 0 ? (int) ((double) current / max * width) : 0;
        String RESET = "\u001B[0m";

        StringBuilder bar = new StringBuilder(color);
        for (int i = 0; i < width; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append(RESET);

        return bar.toString();
    }

    private static void renderChests(String[][] screen, List<Chest> chests) {
        for (Chest chest : chests) {
            if (!chest.isOpened()) {
                String[][] sprite = chest.sprite();
                int baseX = chest.position().x();
                int baseY = chest.position().y();

                for (int sy = 0; sy < sprite.length; sy++) {
                    for (int sx = 0; sx < sprite[0].length; sx++) {
                        int tx = baseX + sx;
                        int ty = baseY + sy;

                        if (isValidPosition(tx, ty, screen)) {
                            screen[ty][tx] = sprite[sy][sx];
                        }
                    }
                }
            }
        }
    }

    private static void renderEntities(String[][] screen, Jogador player, Entidade... others) {
        Entidade[] allEntities = new Entidade[others.length + 1];
        allEntities[0] = player;
        System.arraycopy(others, 0, allEntities, 1, others.length);

        for (Entidade entity : allEntities) {
            if (!entity.vivo()) continue;

            String[][] sprite = entity.sprite();
            int baseX = entity.position().x();
            int baseY = entity.position().y();

            for (int sy = 0; sy < sprite.length; sy++) {
                for (int sx = 0; sx < sprite[0].length; sx++) {
                    int tx = baseX + sx;
                    int ty = baseY + sy;

                    if (isValidPosition(tx, ty, screen)) {
                        screen[ty][tx] = sprite[sy][sx];
                    }
                }
            }
        }
    }

    private static void renderLog(MessageLog log) {
        for (String msg : log.getMessages()) {
            frameBuffer.append(msg).append("\n"); // ← adiciona \n no final de cada linha
        }
        frameBuffer.append("\n"); // linha em branco extra pra ficar bonito
    }

    private static boolean isValidPosition(int x, int y, String[][] screen) {
        return x >= 0 && y >= 0 && y < screen.length && x < screen[0].length;
    }

    public static void clearScreen() {
        System.out.print(ANSI_CLEAR);
        System.out.flush();
    }

    public static void showCursor() {
        System.out.print(ANSI_SHOW_CURSOR);
        System.out.flush();
    }

    public static void hideCursor() {
        System.out.print(ANSI_HIDE_CURSOR);
        System.out.flush();
    }
}