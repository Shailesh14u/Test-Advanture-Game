import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient Scanner scanner = new Scanner(System.in);
    private Map<String, Room> rooms;
    private Player player;
    private boolean running = true;

    public Game(String playerName) {
        this.player = new Player(playerName);
        this.rooms = new HashMap<>();
    }

    // Create a default world map
    public void setupDefaultWorld() {
        rooms.clear();

        Room clearing = new Room("Forest Clearing", "A quiet forest clearing with sunlight filtering through the trees.");
        Room cave = new Room("Dark Cave", "A damp cave — it smells musty and something moves in the dark.");
        Room river = new Room("River Bank", "A flowing river blocks the way east. The water sparkles.");
        Room hill = new Room("Hilltop", "A windy hilltop with a view of the entire region. A strange monument stands here.");
        Room victory = new Room("Ancient Shrine", "A shrine glowing with light — this feels like a place of great power.\nYou sense victory here.");

        // Set exits
        clearing.setExit("north", hill.getName());
        clearing.setExit("east", river.getName());
        clearing.setExit("south", cave.getName());

        cave.setExit("north", clearing.getName());

        river.setExit("west", clearing.getName());
        river.setExit("north", victory.getName());

        hill.setExit("south", clearing.getName());

        victory.setExit("south", river.getName());

        // Add items
        clearing.addItem(new Item("Stick", "A sturdy stick. Could be used as a simple weapon.", 0, 2));
        cave.addItem(new Item("Small Potion", "Restores a bit of health.", 20, 0));
        hill.addItem(new Item("Ancient Sword", "A sword with an old rune. Stronger than a stick.", 0, 8));

        // Add enemies
        cave.setEnemy(new Enemy("Cave Goblin", 30, 6));
        river.setEnemy(new Enemy("River Serpent", 40, 8));
        hill.setEnemy(new Enemy("Hill Warden", 60, 10));

        // Put rooms into map
        rooms.put(clearing.getName(), clearing);
        rooms.put(cave.getName(), cave);
        rooms.put(river.getName(), river);
        rooms.put(hill.getName(), hill);
        rooms.put(victory.getName(), victory);

        // Starting player stats
        player.setCurrentRoom(clearing.getName());
        player.setMaxHealth(100);
        player.setHealth(100);
        player.setBaseAttack(5);
        // Give a starting item
        player.addItem(new Item("Rations", "Some dried food to stave off hunger.", 0, 0));

        System.out.println("New game started. Type 'help' for commands.");
    }

    public void start() {
        scanner = new Scanner(System.in); // reinit transient scanner
        while (running) {
            Room current = rooms.get(player.getCurrentRoom());
            if (current == null) {
                System.out.println("ERROR: You are stuck in a non-existent room. Exiting.");
                return;
            }

            if (current.getName().equals("Ancient Shrine")) {
                System.out.println("You step into the Ancient Shrine — light surrounds you. You win!");
                running = false;
                break;
            }

            if (player.isDead()) {
                System.out.println("You have died. Game over.");
                running = false;
                break;
            }

            System.out.print("\n> ");
            String line = scanner.nextLine().trim();
            processCommand(line);
        }
        System.out.println("Thank you for playing!");
    }

    private void processCommand(String input) {
        if (input.isEmpty()) return;
        String[] parts = input.split(" ", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1].trim() : "";

        switch (cmd) {
            case "help":
                showHelp();
                break;
            case "look":
                look();
                break;
            case "go":
                move(arg);
                break;
            case "take":
                take(arg);
                break;
            case "drop":
                drop(arg);
                break;
            case "inventory":
            case "inv":
                player.listInventory();
                break;
            case "attack":
                attack(arg);
                break;
            case "use":
                useItem(arg);
                break;
            case "status":
                player.printStatus();
                break;
            case "save":
                saveGame(arg);
                break;
            case "load":
                loadGame(arg);
                break;
            case "quit":
            case "exit":
                running = false;
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
    }

    private void showHelp() {
        System.out.println("Commands:");
        System.out.println("  look                - Look around the current room");
        System.out.println("  go <direction>      - Move north/south/east/west");
        System.out.println("  take <item>         - Take an item from the room");
        System.out.println("  drop <item>         - Drop an item from inventory");
        System.out.println("  attack              - Attack the enemy in the room");
        System.out.println("  use <item>          - Use a consumable item (e.g., potion)");
        System.out.println("  inventory / inv     - Show your inventory");
        System.out.println("  status              - Show player health and stats");
        System.out.println("  save <filename>     - Save the game to a file (e.g., mysave.sav)");
        System.out.println("  load <filename>     - Load the game from a file");
        System.out.println("  quit / exit         - Quit the game");
    }

    private void look() {
        Room room = rooms.get(player.getCurrentRoom());
        System.out.println(room.getDescription());
        if (!room.getItems().isEmpty()) {
            System.out.println("You see the following items:");
            for (Item it : room.getItems()) System.out.println(" - " + it.getName() + ": " + it.getDescription());
        }
        if (room.getEnemy() != null && !room.getEnemy().isDead()) {
            System.out.println("An enemy is here: " + room.getEnemy().getName() + " (HP: " + room.getEnemy().getHealth() + ")");
        }
        if (!room.getExits().isEmpty()) {
            System.out.println("Exits: " + String.join(", ", room.getExits().keySet()));
        }
    }

    private void move(String direction) {
        if (direction.isEmpty()) {
            System.out.println("Go where? Specify a direction (north/south/east/west).");
            return;
        }
        Room current = rooms.get(player.getCurrentRoom());
        String destName = current.getExits().get(direction.toLowerCase());
        if (destName == null) {
            System.out.println("You can't go that way.");
            return;
        }
        player.setCurrentRoom(destName);
        System.out.println("You go " + direction + " to " + destName + ".");
        // Auto-look when you move
        look();

        // If there's an enemy, it may attack first
        Room newRoom = rooms.get(destName);
        if (newRoom.getEnemy() != null && !newRoom.getEnemy().isDead()) {
            System.out.println("The " + newRoom.getEnemy().getName() + " notices you!");
            enemyAttacks(newRoom.getEnemy());
        }
    }

    private void take(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Take what?");
            return;
        }
        Room room = rooms.get(player.getCurrentRoom());
        Item found = room.findItemByName(itemName);
        if (found == null) {
            System.out.println("No such item here.");
            return;
        }
        room.removeItem(found);
        player.addItem(found);
        System.out.println("You picked up: " + found.getName());
    }

    private void drop(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Drop what?");
            return;
        }
        Item found = player.findItemByName(itemName);
        if (found == null) {
            System.out.println("You don't have that item.");
            return;
        }
        player.removeItem(found);
        rooms.get(player.getCurrentRoom()).addItem(found);
        System.out.println("You dropped: " + found.getName());
    }

    private void attack(String arg) {
        Room room = rooms.get(player.getCurrentRoom());
        Enemy enemy = room.getEnemy();
        if (enemy == null || enemy.isDead()) {
            System.out.println("There is no enemy to attack here.");
            return;
        }

        // Player attacks
        int playerAttack = player.getAttackDamage();
        System.out.println("You attack the " + enemy.getName() + " for " + playerAttack + " damage.");
        enemy.takeDamage(playerAttack);
        if (enemy.isDead()) {
            System.out.println("You defeated the " + enemy.getName() + "!");
            // Maybe drop a reward
            Item reward = new Item("Gold Coin", "A shiny gold coin.", 0, 0);
            rooms.get(player.getCurrentRoom()).addItem(reward);
            System.out.println("The " + enemy.getName() + " dropped a Gold Coin.");
            return;
        }

        // Enemy counter-attacks
        enemyAttacks(enemy);
    }

    private void enemyAttacks(Enemy enemy) {
        if (enemy == null || enemy.isDead()) return;
        int dmg = enemy.getAttack();
        System.out.println("The " + enemy.getName() + " attacks you for " + dmg + " damage.");
        player.takeDamage(dmg);
        player.printStatusShort();
    }

    private void useItem(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Use what?");
            return;
        }
        Item it = player.findItemByName(itemName);
        if (it == null) {
            System.out.println("You don't have that item.");
            return;
        }
        if (it.getHeal() > 0) {
            int healed = Math.min(player.getMaxHealth() - player.getHealth(), it.getHeal());
            player.heal(healed);
            player.removeItem(it);
            System.out.println("You use the " + it.getName() + " and restore " + healed + " HP.");
            player.printStatusShort();
            return;
        }
        System.out.println("You can't use that item right now.");
    }

    private void saveGame(String filename) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("Please provide a filename. Example: save mygame.sav");
            return;
        }
        if (!filename.endsWith(".sav")) filename += ".sav";
        boolean ok = SaveLoad.save(this, filename);
        System.out.println(ok ? "Game saved to " + filename : "Failed to save game.");
    }

    public boolean loadGame(String filename) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("Please specify a filename to load.");
            return false;
        }
        if (!filename.endsWith(".sav")) filename += ".sav";
        Game loaded = SaveLoad.load(filename);
        if (loaded == null) return false;

        // Replace state
        this.rooms = loaded.rooms;
        this.player = loaded.player;
        // reinit transient scanner
        this.scanner = new Scanner(System.in);
        System.out.println("Loaded game from " + filename + ".");
        return true;
    }
}
