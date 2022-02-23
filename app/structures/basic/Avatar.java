package structures.basic;

public class Avatar extends Unit{
	
	static int initAttack = 2;

	Player player;
	private int attack = initAttack;
	
	public void setPlayer(Player p) {
		player = p;
		p.avatar = this;
	}
	
	public void changeHealth(int change) {
		player.changeHealth(change);
		//if(player.isDead){}
	}
}
