import java.io.*;

public class SaveLoad {
    public static boolean save(Game game, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(game);
            return true;
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
            return false;
        }
    }

    public static Game load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = in.readObject();
            if (obj instanceof Game) return (Game) obj;
            System.out.println("Invalid save file.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load failed: " + e.getMessage());
            return null;
        }
    }
}
