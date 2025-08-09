import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Text Adventure Game (Full Version)!");
        System.out.print("Enter your player name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "Hero";

        Game game = new Game(name);

        System.out.println("Do you want to (1) New Game or (2) Load Game? Enter 1 or 2:");
        String choice = sc.nextLine().trim();
        if (choice.equals("2")) {
            System.out.print("Enter save filename to load: ");
            String file = sc.nextLine().trim();
            if (!file.endsWith(".sav")) file += ".sav";
            if (!game.loadGame(file)) {
                System.out.println("Failed to load. Starting a new game.");
                game.setupDefaultWorld();
            }
        } else {
            game.setupDefaultWorld();
        }

        game.start();
        sc.close();
    }
}f