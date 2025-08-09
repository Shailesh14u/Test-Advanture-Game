import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private int heal; // heal amount if consumable
    private int attackBonus; // static bonus if carried/equipped

    public Item(String name, String description, int heal, int attackBonus) {
        this.name = name;
        this.description = description;
        this.heal = heal;
        this.attackBonus = attackBonus;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getHeal() { return heal; }
    public int getAttackBonus() { return attackBonus; }
}


/* ----------------------------- Enemy.java ----------------------------- */
