package structures.basic;

public class UnitCard extends Card{
	
	private int health;
	private int attack;
	
	//getters
	public int getHealth() {
		return health;
	}

	public int getAttack() {
		return attack;
	}
	
	//setters
	public void setHealth(int h) {
		this.health = h;
	}

	public void setAttack(int a) {
		this.attack = a;
	}
}
