import java.io.Serializable;
import java.util.*;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Map<String, String> exits; // direction -> roomName
    private List<Item> items;
    private Enemy enemy;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new LinkedHashMap<>();
        this.items = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Map<String,String> getExits() { return exits; }
    public List<Item> getItems() { return items; }

    public void setExit(String direction, String roomName) { exits.put(direction.toLowerCase(), roomName); }
    public void addItem(Item it) { items.add(it); }
    public void removeItem(Item it) { items.remove(it); }
    public Item findItemByName(String name) {
        for (Item it : items) if (it.getName().equalsIgnoreCase(name)) return it;
        return null;
    }

    public Enemy getEnemy() { return enemy; }
    public void setEnemy(Enemy e) { this.enemy = e; }
}
