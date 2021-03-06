package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */

/* extended by minion and avatar; compared to the basic Unit, added attack and some methods, 
 * some of the methods are written to be override by its sub classes*/
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	
	//attack is shared by both avatar and minion,
	//while health is independent in minion but residents in avatar's player attribute
	protected int attack = 0;
	
	//getters and setters for attack
	public void setAttack(int a) {
		this.attack = a;
	}

	public void decAttackNum() {
		this.attackNum -= 2;
	}
	
	public void changeAttack(int a) {
		System.out.println("attack before change" + attack);
		int x = attack + a;
		attack = x;
		System.out.println("attack has been changed" + attack);
	}

	public int getX() {
		return position.getTilex();
	}

	public int getY() {
		return position.getTiley();
	}
	
	public int getAttack() {
		return this.attack;
	}


	
	//to be override
	public void setHealth(int h) {
		
	}
	
	public void changeHealth(int h) {
		
	}
	
	public int getHealth() {
		return 0;
	}
	
	//above methods are saved for implement polymorphism in subclasses
	
	//limits for attack and move;
	//normally, they should be 1; if it is azurite_lion or serpenti, set it to 2
	private int attackNum = 0;
	private int moveNum = 1;

	public int getAttackNum() {
		return attackNum;
	}

	public void setAttackNum(int attackNum) {
		this.attackNum = attackNum;
	}

	public int getMoveNum() {
		return moveNum;
	}

	public void setMoveNum(int moveNum) {
		this.moveNum = moveNum;
	}
	
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}

	public boolean belongToAI() {
		if(id / Deck.capacity == 1 || id == 101) {
			return true;
		}
		return false;
	}

	//evaluate whether two unit are neighbours
	public boolean isNeighbour(Unit u) {
		if(this.position.near(u.getPosition())) {
			return true;
		} else {return false;}
	}

	//helper to change position of the unit
	public void changePosition(int x, int y) {
		int x0 = position.getTilex(), y0 = position.getTiley();
		this.position.setTilex(x0+x);
		this.position.setTiley(y0+y);
	}
	
	
}
