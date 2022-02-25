package structures.basic;

public class Minion extends Unit{

	public int id;
	private int health;
	private int attack;
	
	private int attackLimit = 1;
	private boolean provoke = false;
	private int startingHealth;
	
	//setters
	public void setHealth(int h) {
		this.health = h;
	}
	
	public void setAttack(int a) {
		this.attack = a;
	}
	
	public void changeHealth(int h) {
		this.health += h;
		if(this.health > startingHealth) {this.setHealth(startingHealth);}
	}
	
	public void changeAttack(int a) {
		this.attack += a;
	}
	
	public void setAttackLimit(int i) {
		this.attackLimit = i;
	}
	
	public void enableProvoke() {
		this.provoke = true;
	}
	
	public void setInitHealth(int h) {
		this.startingHealth = h;
	}
	
	//getters
	public int getHealth() {
		return health;
	}
	
	public int getAttack() {
		return attack;
	}
	
}
