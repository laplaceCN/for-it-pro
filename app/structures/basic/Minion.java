package structures.basic;

/* represents a minion of the player, generated when a unit card put onto board
 * a sub class of Unit*/
public class Minion extends Unit{

	public int id;
	private int health;
	
	private boolean provoke = false;
	private int startingHealth;
	private boolean ranged = false;
	private boolean passive_1 = false;
	private boolean passive_2 = false;

	public void enableRanged() {
		this.ranged = true;
	}

	//about passive abilities
	public void enableP1() {
		this.passive_1 = true;
	}

	public void enableP2() {
		this.passive_2 = true;
	}

	public boolean getProvoke() {
		return this.provoke;
	}

	public boolean getRanged() {
		return this.ranged;
	}

	public boolean getP1() {
		return this.passive_1;
	}

	public boolean getP2() {
		return this.passive_2;
	}


	//override Unit's methods about health
	@Override
	public void setHealth(int h) {
		this.health = h;

		if(this.health < 0) {this.health = 0;}
	}
	
	@Override
	public void changeHealth(int h) {
		this.health += h;

		if(this.health > startingHealth) {this.setHealth(startingHealth);}
		if(this.health < 0) {this.health = 0;}
	}
	
	@Override
	public int getHealth() {
		return this.health;
	}
	//end of overrides
	
	//about provoke ability
	public void enableProvoke() {
		this.provoke = true;
	}
	
	public void setInitHealth(int h) {
		this.startingHealth = h;
	}
	
	//getters are placed upper in this class or in the super class
	
}
