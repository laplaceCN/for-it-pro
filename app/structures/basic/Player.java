package structures.basic;

import akka.actor.ActorRef;
import utils.StaticConfFiles;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	public static int numOfHandCard = 6;
	public static int numOfInitHand = 3;
	public boolean[] handPositionStatus;
	public int minFreePosition;
	
	int health;
	int mana;
	Deck deck;
	Card[] hand;
	public boolean humanOrAI;	//false is human, true is AI
	public Avatar avatar;
	
	public String[] unitInfo;
	
	public Player(boolean humanOrAI) {
		super();
		this.health = 20;
		this.mana = 0;
		this.humanOrAI = humanOrAI;
		
		this.avatar = null;
		deck = new Deck(humanOrAI);
		hand = new Card[numOfHandCard];
		
		//start recording of hand position status
		this.handPositionStatus = new boolean[6];
		for(int i = 0; i < 6; i++) {handPositionStatus[i] = true;}
		minFreePosition = 0;
		
		unitInfo = new String[10];
		//spell cards (0 and 1) don't have unitInfo
		if(!humanOrAI) {
			unitInfo[0] = null;
			unitInfo[1] = null;
			unitInfo[2] = StaticConfFiles.u_comodo_charger;
			unitInfo[3] = StaticConfFiles.u_azure_herald;
			unitInfo[4] = StaticConfFiles.u_azurite_lion;
			unitInfo[5] = StaticConfFiles.u_fire_spitter;
			unitInfo[6] = StaticConfFiles.u_hailstone_golem;
			unitInfo[7] = StaticConfFiles.u_ironcliff_guardian;
			unitInfo[8] = StaticConfFiles.u_pureblade_enforcer;
			unitInfo[9] = StaticConfFiles.u_silverguard_knight;
		} else {
			unitInfo[0] = null;
			unitInfo[1] = null;
			unitInfo[2] = StaticConfFiles.u_blaze_hound;
			unitInfo[3] = StaticConfFiles.u_bloodshard_golem;
			unitInfo[4] = StaticConfFiles.u_planar_scout;
			unitInfo[5] = StaticConfFiles.u_pyromancer;
			unitInfo[6] = StaticConfFiles.u_rock_pulveriser;
			unitInfo[7] = StaticConfFiles.u_serpenti;
			unitInfo[8] = StaticConfFiles.u_windshrike;
			unitInfo[9] = StaticConfFiles.u_hailstone_golem;
		}
	}
	
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	
	public void getHandCard() {
		if(minFreePosition==numOfHandCard) {
			deck.getCard();		//if hand is full, discard the card got from deck
		}
		
		hand[minFreePosition] = deck.getCard();
		handPositionStatus[minFreePosition] = false;
		
		int index;
		for(index=minFreePosition+1; index<Player.numOfHandCard; index++) {
			if(handPositionStatus[index]) {
				minFreePosition=index; 
				break;
			}
		}
		minFreePosition = index;
	}
	
	public void deleteHandCard(int i) {
		hand[i] = null;
		handPositionStatus[i] = true;
		if(i < minFreePosition) {minFreePosition = i;}
	}
		
	//getters
	
	public int getMana() {
		return mana;
	}
	
	public int getHealth() {
		return health;
	}
	
	//get a hand card
	public Card getCard(int i) {
		return hand[i];
	}
	
	//setter
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setMana(int mana) {
		this.mana = mana;
	}
	
	public void costMana(int cost) {
		mana -= cost;
	}
	
	public void changeHealth(int change) {
		this.health =+ change;
	}
	
	public boolean isDead() {
		return (health>0);
	}
	
}
