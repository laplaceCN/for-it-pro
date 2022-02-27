package structures.basic;

import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.StaticConfFiles;

/* represents the deck of each player, 
 * contains two set of card generating information, 
 * active depends on the player is human or AI
 * deck generating from the blueprint in the constructor. */
public class Deck {
	
	public static int capacity = 20;
	private int toBeDrawn = 0;
	
	private Card[] deck;
	
	private String[] deckInfo;
	private int[] healthInfo;
	private int[] attackInfo;
	private HashMap<String, ArrayList<Integer>> abilityInfo;
	
	
	private int deckNo;		//if it is human player, deckNo = 0; if it is AI player, deckNo = 1
	
	public Deck(boolean isAI) {
		loadInfo(isAI);
		deck = new Card[capacity];
		for(int i = 0; i < capacity; i++) {
			int n = i%10;
			if(n<2) {
				SpellCard s = (SpellCard)utils.BasicObjectBuilders.loadCard(deckInfo[n], i + capacity*deckNo, SpellCard.class);
				//ability marks
				deck[i] = s;
			} else {
				UnitCard u = (UnitCard)utils.BasicObjectBuilders.loadCard(deckInfo[n], i + capacity*deckNo, UnitCard.class);
				u.setHealth(healthInfo[n]);
				u.setAttack(attackInfo[n]);
				//write ability marks
				for(String ability : abilityInfo.keySet()) {
					if(abilityInfo.get(ability).contains(u.id)) {u.addAbility(ability);}
				}
				deck[i] = u;
			}	
		}
	}
	
	public Card getCard() {
		//for test:
//		if(deck[0].id == 10) {
//			String notification = ""+deck[toBeDrawn].cardname;
//			BasicCommands.addPlayer1Notification(out, notification, 2);
//		}
		if(toBeDrawn == Deck.capacity) {return null;}
		return deck[toBeDrawn++];
	}
	
	private void loadInfo(boolean isAI) {
		//decide which set of information needs to be loaded according to who is the player
		//human or AI
		deckNo = isAI ? 1 : 0;
		deckInfo = new String[10];
		healthInfo = new int[10];
		attackInfo = new int[10];
		abilityInfo = new HashMap<String, ArrayList<Integer>>();
		
		abilityInfo.put("provoke", new ArrayList<Integer>());
		abilityInfo.get("provoke").add(7);abilityInfo.get("provoke").add(17);
		abilityInfo.get("provoke").add(9);abilityInfo.get("provoke").add(19);
		abilityInfo.get("provoke").add(26);abilityInfo.get("provoke").add(36);
		
		abilityInfo.put("ranged", new ArrayList<Integer>());
		abilityInfo.get("ranged").add(5);abilityInfo.get("ranged").add(15);
		abilityInfo.get("ranged").add(25);abilityInfo.get("ranged").add(35);
		
		abilityInfo.put("flying", new ArrayList<Integer>());
		abilityInfo.get("flying").add(8);abilityInfo.get("flying").add(18);
		
		abilityInfo.put("twice_attack", new ArrayList<Integer>());
		abilityInfo.get("twice_attack").add(4);abilityInfo.get("twice_attack").add(14);
		abilityInfo.get("twice_attack").add(27);abilityInfo.get("twice_attack").add(37);

		abilityInfo.put("free_summon", new ArrayList<Integer>());
		abilityInfo.get("free_summon").add(7);abilityInfo.get("free_summon").add(17);
		abilityInfo.get("free_summon").add(24);abilityInfo.get("free_summon").add(34);
				
		if(!isAI) {
			deckInfo[0] = StaticConfFiles.c_truestrike;				//-2 health -> an enemy unit
			deckInfo[1] = StaticConfFiles.c_sundrop_elixir;			//+5 health -> a unit (result<=init)
			deckInfo[2] = StaticConfFiles.c_comodo_charger;			//---
			deckInfo[3] = StaticConfFiles.c_azure_herald;			//+3 health -> avatar (result<=init)
			deckInfo[4] = StaticConfFiles.c_azurite_lion;			//*2 attack per turn
			deckInfo[5] = StaticConfFiles.c_fire_spitter;			//ranged
			deckInfo[6] = StaticConfFiles.c_hailstone_golem;		//---
			deckInfo[7] = StaticConfFiles.c_ironcliff_guardian;		//provoke, summoned anywhere
			deckInfo[8] = StaticConfFiles.c_pureblade_enforcer;		//+1 health, +1 attack when enemy spells
			deckInfo[9] = StaticConfFiles.c_silverguard_knight;		//provoke, +2 attack if avatar demaged
			
			healthInfo[0] = 0;
			healthInfo[1] = 0;
			healthInfo[2] = 3;
			healthInfo[3] = 4;
			healthInfo[4] = 3;
			healthInfo[5] = 2;
			healthInfo[6] = 6;
			healthInfo[7] = 10;
			healthInfo[8] = 4;
			healthInfo[9] = 1;
			
			attackInfo[0] = 0;
			attackInfo[1] = 0;
			attackInfo[2] = 1;
			attackInfo[3] = 1;
			attackInfo[4] = 2;
			attackInfo[5] = 3;
			attackInfo[6] = 4;
			attackInfo[7] = 3;
			attackInfo[8] = 1;
			attackInfo[9] =	5;
		} else {
			deckInfo[0] = StaticConfFiles.c_staff_of_ykir;				//+2 attack -> avatar
			deckInfo[1] = StaticConfFiles.c_entropic_decay;				//=0 health -> a non avatar unit
			deckInfo[2] = StaticConfFiles.c_blaze_hound;				//both player draw a card
			deckInfo[3] = StaticConfFiles.c_bloodshard_golem;			//---
			deckInfo[4] = StaticConfFiles.c_planar_scout;				//summoned anywhere
			deckInfo[5] = StaticConfFiles.c_pyromancer;					//ranged
			deckInfo[6] = StaticConfFiles.c_rock_pulveriser;			//provoke
			deckInfo[7] = StaticConfFiles.c_serpenti;					//*2 attack per turn
			deckInfo[8] = StaticConfFiles.c_windshrike;					//flying, draw a card when it dies
			deckInfo[9] = StaticConfFiles.c_hailstone_golem;			//---
			
			healthInfo[0] = 0;
			healthInfo[1] = 0;
			healthInfo[2] = 3;
			healthInfo[3] = 3;
			healthInfo[4] = 1;
			healthInfo[5] = 1;
			healthInfo[6] = 4;
			healthInfo[7] = 4;
			healthInfo[8] = 3;
			healthInfo[9] = 6;
			
			attackInfo[0] = 0;
			attackInfo[1] = 0;
			attackInfo[2] = 4;
			attackInfo[3] = 4;
			attackInfo[4] = 2;
			attackInfo[5] = 2;
			attackInfo[6] = 1;
			attackInfo[7] = 7;
			attackInfo[8] = 4;
			attackInfo[9] =	4;							
		}
	}
}
