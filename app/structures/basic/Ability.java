package structures.basic;

public class Ability {

	//for 8-pureblade_enforcer
	public static void gainAfterEnemySpells(Minion m) {
		m.changeAttack(1);
		m.changeHealth(1);
	}
	
	//for 9-silverguard_knight
	public static void gainAfterAvatarDamaged(Minion m) {
		m.changeAttack(2);
	}
	
	public static void attackTwice(Minion m) {
		m.setAttackLimit(2);
	}
	
	public static void provoke(Minion m) {
		m.enableProvoke();
	}
	
	public static void ranged(Minion m) {
		
	}
	
	public static void changeHealth(Unit u, int change) {
		if(u instanceof Minion) {
			((Minion) u).changeHealth(change);
		} else if(u instanceof Avatar) {
			((Avatar) u).player.changeHealth(change);
		}
	}
		
	public static void resetHealth(Minion m) {
		m.setHealth(0);
	}
	
	public static void changeHealth(int change, Unit u) {
		
	}
	
	public static void changeAttack(int change, Unit u) {
		
	}
}
