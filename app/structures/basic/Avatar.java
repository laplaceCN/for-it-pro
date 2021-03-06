package structures.basic;

/* represents a player's avatar on board
 * the health value is always synchronised with the player
 * a sub class of Unit*/
public class Avatar extends Unit{
	
	static int initAttack = 2;

	Player player;
	private int attack = initAttack;

	@Override
	public int getAttack() {
		return attack;
	}

	@Override
	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setPlayer(Player p) {
		player = p;
		p.avatar = this;
	}
	
	//override Unit's methods about health
	@Override
	public void setHealth(int h) {
		player.health = h;
		if(player.health > 20) {this.setHealth(20);}
		if(player.health < 0) {this.setHealth(0);}
	}
	
	@Override
	public void changeHealth(int h) {
		player.health += h;
		if(player.health > 20) {this.setHealth(20);}
		if(player.health < 0) {this.setHealth(0);}
	}
	
	@Override
	public int getHealth() {
		return player.health;
	}
	//end of overrides
	
}
