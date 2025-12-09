package engine;

import entidades.Player;
import items.Chest;
import items.Weapon;
import mundo.*;
import java.util.List;

public class GameUI {

    public void showInventory(Player player) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                         INVENTARIO                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        if (player.inventory().size() == 0) {
            System.out.println("  Seu inventario esta vazio.");
        } else {
            System.out.println("  Pressione um numero para equipar a arma, [I] para fechar");
            System.out.println();

            List<Weapon> weapons = player.inventory().getWeapons();
            Weapon equipped = player.inventory().getEquippedWeapon();

            for (int i = 0; i < weapons.size(); i++) {
                Weapon w = weapons.get(i);
                String equipMark = (w == equipped) ? " [Equipado]" : "";
                String color = getRarityColor(w.rarity());

                int actualDamage = w.getDamageWithStats(
                        player.stats().strength(),
                        player.stats().dexterity(),
                        player.stats().intelligence()
                );

                System.out.printf("  [%d] %s%-35s%s Lv.%-2d [%d→%d DMG] <%s> %.1fkg%s\n",
                        i + 1, color, w.name(), "\u001B[0m", w.level(),
                        w.baseDamage(), actualDamage, w.getTypeSymbol(), w.weight(), equipMark);
            }
        }

        System.out.println();
        System.out.println("─".repeat(64));
    }

    public void showChestLoot(Chest chest, Player player) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        String title = chest.corpseName();
        int padding = (62 - title.length()) / 2;
        System.out.printf("║%s%s%s║\n",
                " ".repeat(padding),
                title,
                " ".repeat(62 - padding - title.length())
        );
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Pressione um numero para pegar uma arma, [E] para fechar");
        System.out.println();

        List<Weapon> weapons = chest.weapons();
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            String color = getRarityColor(w.rarity());
            System.out.printf("  [%d] %s%-35s%s Lv.%-2d [%d DMG] <%s> %.1fkg\n",
                    i + 1, color, w.name(), "\u001B[0m",
                    w.level(), w.baseDamage(), w.getTypeSymbol(), w.weight());
        }

        System.out.println();
        System.out.println("─".repeat(64));
        System.out.printf("Inventario: %d/20 | Peso: %.1f/%d\n",
                player.inventory().size(),
                player.inventory().getCurrentWeight(),
                player.stats().maxCarryWeight());
    }

    public void showMinimap(Dungeon dungeon) {
        Render.clearScreen();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      MAPA DO DUNGEON                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        DungeonLevel level = dungeon.getLevel();
        Room[][] grid = level.getRoomGrid();
        int size = level.getGridSize();

        for (int y = 0; y < size; y++) {
            System.out.print("   ");

            for (int x = 0; x < size; x++) {
                Room room = grid[y][x];

                if (x == level.getCurrentRoomX() && y == level.getCurrentRoomY()) {
                    System.out.print(" [\u001B[32m@\u001B[0m]");
                } else if (room == null) {
                    System.out.print(" [ ]");
                } else if (!room.discovered()) {
                    System.out.print(" [?]");
                } else {
                    String symbol = switch (room.type()) {
                        case START -> "S";
                        case BOSS -> "\u001B[31mB\u001B[0m";
                        case TREASURE -> "\u001B[33mT\u001B[0m";
                        default -> room.cleared() ? "-" : "#";
                    };
                    System.out.print(" [" + symbol + "]");
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("─────────────────────────── INFO ─────────────────────────────");
        System.out.println();

        int totalRooms = countTotalRooms(grid, size);
        int discoveredRooms = countDiscoveredRooms(grid, size);
        float explorationPercent = (discoveredRooms * 100.0f) / totalRooms;

        System.out.printf(" Salas exploradas: %d/%d (%.1f%%)\n",
                discoveredRooms, totalRooms, explorationPercent);

        System.out.println();
        System.out.println("──────────────────────── LEGENDA ─────────────────────────────");
        System.out.println(" [\u001B[32m@\u001B[0m] Você");
        System.out.println(" [S] Início");
        System.out.println(" [\u001B[31mB\u001B[0m] Chefe");
        System.out.println(" [\u001B[33mT\u001B[0m] Tesouro");
        System.out.println(" [#] Inimigos");
        System.out.println(" [-] Sala Limpa");
        System.out.println(" [?] Não descoberto");
        System.out.println(" [ ] Sem sala");
        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.println();
        System.out.print(" Pressione qualquer tecla para fechar...");
    }

    public void showGameOver(Player player, Dungeon dungeon) {
        Render.clearScreen();
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     VOCE MORREU                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        DungeonLevel level = dungeon.getLevel();
        int totalRooms = countTotalRooms(level.getRoomGrid(), level.getGridSize());
        int discovered = countDiscoveredRooms(level.getRoomGrid(), level.getGridSize());

        System.out.println(" ──────────────────── ESTATÍSTICAS ─────────────────────");
        System.out.printf(" Salas exploradas: %d/%d\n", discovered, totalRooms);
        System.out.printf(" XP total: %d\n", player.xp());
        System.out.printf(" Nível alcançado: %d\n", player.level());

        boolean bossDefeated = checkBossDefeated(level.getRoomGrid(), level.getGridSize());
        if (bossDefeated) {
            System.out.println(" Status: \u001B[32mBOSS DERROTADO!\u001B[0m 🏆");
        } else {
            System.out.println(" Status: Boss não derrotado");
        }

        System.out.println(" ───────────────────────────────────────────────────────");
        System.out.println();
        System.out.println(" Pressione Q para sair...");
    }

    private int countTotalRooms(Room[][] grid, int size) {
        int count = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (grid[y][x] != null) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countDiscoveredRooms(Room[][] grid, int size) {
        int count = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (grid[y][x] != null && grid[y][x].discovered()) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean checkBossDefeated(Room[][] grid, int size) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Room room = grid[y][x];
                if (room != null && room.type() == Room.RoomType.BOSS) {
                    return room.cleared();
                }
            }
        }
        return false;
    }


    private String getRarityColor(String rarity) {
        return switch (rarity) {
            case "Common" -> "\u001B[37m";
            case "Rare" -> "\u001B[34m";
            case "Epic" -> "\u001B[35m";
            case "Legendary" -> "\u001B[33m";
            default -> "\u001B[0m";
        };
    }
}