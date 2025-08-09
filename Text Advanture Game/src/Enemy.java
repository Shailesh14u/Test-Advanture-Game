import java.io.Serializable;

public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int health;
    private int attack;

    public Enemy(String name, int health, int attack) {
        this.name = name;
        this.health = health;
        this.attack = attack;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getAttack() { return attack; }

    public void takeDamage(int d) { health -= d; if (health < 0) health = 0; }
    public boolean isDead() { return health <= 0; }
}