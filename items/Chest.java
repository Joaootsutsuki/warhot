package items;

import mundo.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chest {
    private Position position;
    private List<Weapon> weapons;
    private boolean opened;
    private String[][] sprite;
    private String corpseName;

    public Chest(Position position, int floorLevel, int luckBonus, String monsterName) {
        this.position = position;
        this.opened = false;
        this.weapons = new ArrayList<>();
        this.corpseName = monsterName + "'s Corpse";

        // gerar 3-5 armas random (afetado por luck)
        Random rand = new Random();
        int numWeapons = 3 + rand.nextInt(3) + (luckBonus > 5 ? 1 : 0);

        for (int i = 0; i < numWeapons; i++) {
            weapons.add(Weapon.generateRandom(floorLevel, luckBonus, rand));
        }

        // sprite do corpo
        String GRAY = "\u001B[90m";
        String RESET = "\u001B[0m";
        this.sprite = new String[][] {
                { GRAY + "☠" + RESET, GRAY + "☠" + RESET },
                { GRAY + "☠" + RESET, GRAY + "☠" + RESET }
        };
    }

    public Position position() {
        return position;
    }

    public List<Weapon> weapons() {
        return weapons;
    }

    public boolean isOpened() {
        return opened;
    }

    public void open() {
        this.opened = true;
    }

    public String[][] sprite() {
        return sprite;
    }

    public String corpseName() {
        return corpseName;
    }
}