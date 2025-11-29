package entidades;

// sistema de stats igual dark souls xd

public class Stats {
    private int vitality; // HP
    private int endurance; // peso
    private int strength; // escala com armas pesadas
    private int dexterity; // escala com armas leves
    private int intelligence; // escala com armas magicas (cajado)
    private int luck; // afeta chance-based stats

    public Stats(int vit, int end, int str, int dex, int intel, int lck) {
        this.vitality = vit;
        this.endurance = end;
        this.strength = str;
        this.dexterity = dex;
        this.intelligence = intel;
        this.luck = lck;
    }

    // Getters
    public int vitality() {
        return vitality;
    }

    public int endurance() {
        return endurance;
    }

    public int strength() {
        return strength;
    }

    public int dexterity() {
        return dexterity;
    }

    public int intelligence() {
        return intelligence;
    }

    public int luck() {
        return luck;
    }

    // setters pra level up
    public void addVitality(int amount) {
        vitality += amount;
    }

    public void addEndurance(int amount) {
        endurance += amount;
    }

    public void addStrength(int amount) {
        strength += amount;
    }

    public void addDexterity(int amount) {
        dexterity += amount;
    }

    public void addIntelligence(int amount) {
        intelligence += amount;
    }

    public void addLuck(int amount) {
        luck += amount;
    }

    // derived stats
    public int maxHP() {
        return 30 + (vitality * 5);
    }

    public int maxCarryWeight() {
        return 20 + (endurance * 5);
    }

    public double evasionChance() {
        return Math.min(0.3, luck * 0.02); // max 30% evasion
    }

    public double criticalChance() {
        return Math.min(0.25, luck * 0.015); // max 25% crit
    }

    public int lootBonus() {
        return luck / 5; // better loot quality
    }
}