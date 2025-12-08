package engine;

import entidades.Player;
import items.Chest;
import items.Weapon;
import mundo.*;
import java.util.List;

public class GameUI {

    public void showInventory(Player player) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                         INVENTORY                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        if (player.inventory().size() == 0) {
            System.out.println("  Your inventory is empty.");
        } else {
            System.out.println("  Press number to equip weapon, [I] to close");
            System.out.println();

            List<Weapon> weapons = player.inventory().getWeapons();
            Weapon equipped = player.inventory().getEquippedWeapon();

            for (int i = 0; i < weapons.size(); i++) {
                Weapon w = weapons.get(i);
                String equipMark = (w == equipped) ? " [EQUIPPED]" : "";
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
        System.out.println("  Press number to take weapon, [E] to close");
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
        System.out.printf("Inventory: %d/20 | Weight: %.1f/%d\n",
                player.inventory().size(),
                player.inventory().getCurrentWeight(),
                player.stats().maxCarryWeight());
    }

    public void showMinimap(Dungeon dungeon) {
        Render.clearScreen();
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║   MINIMAP - Floor " + dungeon.getCurrentFloorNumber() + "          ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.println();

        DungeonLevel level = dungeon.getCurrentLevel();
        Room[][] grid = level.roomGrid();
        int size = level.gridSize();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Room room = grid[y][x];

                if (x == level.currentRoomX() && y == level.currentRoomY()) {
                    System.out.print(" @@ ");
                } else if (room == null) {
                    System.out.print(" .. ");
                } else if (!room.discovered()) {
                    System.out.print(" ?? ");
                } else {
                    String symbol = switch (room.type()) {
                        case START -> " SS ";
                        case BOSS -> " BB ";
                        case TREASURE -> " TT ";
                        default -> room.cleared() ? " -- " : " ## ";
                    };
                    System.out.print(symbol);
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Floor: " + dungeon.getCurrentFloorNumber() +
                "/" + dungeon.getTotalFloorsGenerated());
        System.out.println();
        System.out.println("Legend: @@ You | SS Start | BB Boss | TT Treasure");
        System.out.println("        ## Enemies | -- Cleared | ?? Undiscovered");
        System.out.println("        ↑ Stairs Up | ↓ Stairs Down");
        System.out.println();
        System.out.println("Press any key to close...");
    }

    public void showGameOver(Player player, int currentFloor) {
        Render.clearScreen();
        System.out.println();
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║        YOU DIED!               ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.println();
        System.out.println("Floor reached: " + currentFloor);
        System.out.println("Total XP: " + player.xp());
        System.out.println();
        System.out.println("Press Q to quit...");
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