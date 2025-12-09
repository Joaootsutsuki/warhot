package mundo;

public class Dungeon {
    private final DungeonLevel level;

    public Dungeon() {
        this.level = new DungeonLevel();

        level.getCurrentRoom().setDiscovered(true);
    }

    public DungeonLevel getLevel() {
        return level;
    }

    public Room getCurrentRoom() {
        return level.getCurrentRoom();
    }
}