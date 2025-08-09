import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int health;
    private int maxHealth;
    private int baseAttack;
    private List<Item> inventory;
    private String currentRoom;

    public Player(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.maxHealth = 100;
        this.health = maxHealth;
        this.baseAttack = 5;
    }

    public void addItem(Item it) { inventory.add(it); }
    public void removeItem(Item it) { inventory.remove(it); }
    public Item findItemByName(String name) {
        for (Item it : inventory) if (it.getName().equalsIgnoreCase(name)) return it;
        return null;
    }

    public int getAttackDamage() {
        int bonus = 0;
        for (Item it : inventory) { bonus += it.getAttackBonus(); }
        return baseAttack + bonus;
    }

    public void takeDamage(int d) { health -= d; if (health < 0) health = 0; }
    public void heal(int amount) { health += amount; if (health > maxHealth) health = maxHealth; }
    public boolean isDead() { return health <= 0; }

    public void listInventory() {
        if (inventory.isEmpty()) { System.out.println("Inventory is empty."); return; }
        System.out.println("Inventory:");
        for (Item it : inventory) System.out.println(" - " + it.getName() + (it.getHeal()>0?" (heal: "+it.getHeal()+")":""));
    }

    public void printStatus() {
        System.out.println("Player: " + name);
        System.out.println("HP: " + health + "/" + maxHealth);
        System.out.println("Attack: " + getAttackDamage());
    }
    public void printStatusShort() {
        System.out.println("HP: " + health + "/" + maxHealth + " | Attack: " + getAttackDamage());
    }

    // Getters and setters
    public String getName() { return name; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getBaseAttack() { return baseAttack; }
    public void setBaseAttack(int baseAttack) { this.baseAttack = baseAttack; }
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String room) { this.currentRoom = room; }
}