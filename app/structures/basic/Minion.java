package structures.basic;

/* represents a minion of the player, generated when a unit card put onto board
 * a sub class of Unit*/
public class Minion extends Unit{

	public int id;
	private int health;
	
	private boolean provoke = false;
	private int startingHealth;


	//override Unit's methods about health
	@Override
	public void setHealth(int h) {
		this.health = h;
	}
	
	@Override
	public void changeHealth(int h) {
		this.health += h;
		System.out.println(startingHealth+"hhhhh");
		if(this.health > startingHealth) {this.setHealth(startingHealth);}
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
