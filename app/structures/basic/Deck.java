package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.StaticConfFiles;

public class Deck {
	
	private int capacity = 10;
	private int toBeDrawn = 0;
	
	private Card[] deck;
	
	private String[] deckInfo;
	private int[] healthInfo;
	private int[] attackInfo;
	//need a ability marks map
	private int deckNo;		//if it is human player, deckNo = 0; if it is AI player, deckNo = 1
	
	public Deck(boolean isAI) {
		loadInfo(isAI);
		deck = new Card[capacity];
		for(int i = 0; i < capacity; i++) {
			if(i<2) {
				SpellCard s = (SpellCard)utils.BasicObjectBuilders.loadCard(deckInfo[i], i + 10*deckNo, SpellCard.class);
				//ability marks
				deck[i] = s;
			} else {
				UnitCard u = (UnitCard)utils.BasicObjectBuilders.loadCard(deckInfo[i], i + 10*deckNo, UnitCard.class);
				u.setHealth(healthInfo[i]);
				u.setAttack(attackInfo[i]);
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
		return deck[toBeDrawn++];
	}
	
	private void loadInfo(boolean isAI) {
		//decide which set of information needs to be loaded according to who is the player
		//human or AI
		deckNo = isAI ? 1 : 0;
		deckInfo = new String[10];
		healthInfo = new int[10];
		attackInfo = new int[10];
		
		if(!isAI) {
			deckInfo[0] = StaticConfFiles.c_truestrike;
			deckInfo[1] = StaticConfFiles.c_sundrop_elixir;
			deckInfo[2] = StaticConfFiles.c_comodo_charger;
			deckInfo[3] = StaticConfFiles.c_azure_herald;
			deckInfo[4] = StaticConfFiles.c_azurite_lion;
			deckInfo[5] = StaticConfFiles.c_fire_spitter;
			deckInfo[6] = StaticConfFiles.c_hailstone_golem;
			deckInfo[7] = StaticConfFiles.c_ironcliff_guardian;
			deckInfo[8] = StaticConfFiles.c_pureblade_enforcer;
			deckInfo[9] = StaticConfFiles.c_silverguard_knight;
			
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
			deckInfo[0] = StaticConfFiles.c_staff_of_ykir;
			deckInfo[1] = StaticConfFiles.c_entropic_decay;
			deckInfo[2] = StaticConfFiles.c_blaze_hound;
			deckInfo[3] = StaticConfFiles.c_bloodshard_golem;
			deckInfo[4] = StaticConfFiles.c_planar_scout;
			deckInfo[5] = StaticConfFiles.c_pyromancer;
			deckInfo[6] = StaticConfFiles.c_rock_pulveriser;
			deckInfo[7] = StaticConfFiles.c_serpenti;
			deckInfo[8] = StaticConfFiles.c_windshrike;
			deckInfo[9] = StaticConfFiles.c_hailstone_golem;
			
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
