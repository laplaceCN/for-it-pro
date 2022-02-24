package structures.basic;

public class Minion extends Unit{

	public int id;
	private int health;
	private int attack;




	//setters
	public void setHealth(int h) {
		this.health = h;
	}
	
	public void setAttack(int a) {
		this.attack = a;
	}
	
	//getters
	public int getHealth() {
		return health;
	}
	
	public int getAttack() {
		return attack;
	}
	
}
