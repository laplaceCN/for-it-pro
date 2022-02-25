package structures.basic;
import java.util.*;

public class UnitCard extends Card{
	
	private int health;
	private int attack;
	private ArrayList<String> abilities = new ArrayList<String>(); 
	
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
	
	public void addAbility(String ability) {
		abilities.add(ability);
	}
	
	//search an ability
	public boolean searchAbility(String ability) {
		System.out.print(abilities.contains(ability));
		return abilities.contains(ability);
	}
}
