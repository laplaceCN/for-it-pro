package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;

public class PlayerModel {
	
	private static int humanID = 100;
	private static int aiID = 101;
	
	private Board board;
	
	private Player player;
	public Avatar avatar;
	
	public ArrayList<Tile> availableTiles;	//all the available tiles under normal circumstance
	public ArrayList<Tile> freeSummon;		//all the unoccupied tiles, which are available for free-summon cards
	public ArrayList<Tile> allowedTiles;	//the working set of tiles which can be used
	
	public PlayerModel(Player p, Board b) {
		this.player = p;
		this.board = b;
		this.availableTiles = new ArrayList<Tile>();
		this.allowedTiles = new ArrayList<Tile>();
		if(!player.humanOrAI) {
			this.avatar = (Avatar)utils.BasicObjectBuilders.loadUnit(utils.StaticConfFiles.humanAvatar, humanID, Avatar.class);
		} else {
			this.avatar = (Avatar)utils.BasicObjectBuilders.loadUnit(utils.StaticConfFiles.humanAvatar, aiID, Avatar.class);
		}
		
		this.avatar.setPlayer(player);
		this.player.setAvatar(avatar);
	}

	public void initHand(ActorRef out) {
		for(int i = 0; i < Player.numOfInitHand; i++) {
			player.getHandCard();
			BasicCommands.drawCard(out, player.getCard(i), i+1, 0);
		}
	}
	
	//get a card from deck
	public void drawOneCard(ActorRef out) {
		int index = player.minFreePosition;
		player.getHandCard();
		if(index < Player.numOfHandCard) {
			BasicCommands.drawCard(out, player.getCard(index), index+1, 0);
		}
	}
	
	public void showAvailables(ActorRef out, int index, int mode) {
		Card c = player.getCard(index-1);
		if(c instanceof UnitCard) {
			if(((UnitCard) c).searchAbility("free_summon")) {
				freeSummon = new ArrayList<Tile>();
				for(int i = 0; i < 9; i++) {
					for(int j = 0; j < 5; j++) {
						Tile t = board.getTile(i, j);
						if(t.getOwnership() == -1) {freeSummon.add(t);}
					}
				}
				allowedTiles = freeSummon;
			} else {
				allowedTiles = availableTiles;
			}
			
			highlightControl(out, 1);
			
		} else if(c instanceof SpellCard) {
			//restore the highlights
			highlightControl(out, 0);
		}
	}
	
	public void highlightControl(ActorRef out, int mode) {
		for(Tile t : this.availableTiles) {
			BasicCommands.drawTile(out, t, mode);
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		}
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public void updateAvailables() {
		availableTiles.clear();
		for(Unit u : board.activeUnits) {
			int tileX = u.getPosition().tilex;
			int tileY = u.getPosition().tiley;
			int indicator = player.humanOrAI?1:0;
			//update availableTiles
			if(u.id/Deck.capacity==indicator || u.id==100+indicator) {
				for(int i = Math.max(tileX-1, 0); i <= Math.min(tileX+1, 8); i++) {
					for(int j = Math.max(tileY-1, 0); j <= Math.min(tileY+1, 5); j++) {
						Tile tempTile = board.getTile(i, j);
						if(!availableTiles.contains(tempTile)){availableTiles.add(tempTile);}
					}
				}
			}
		}
		//remove the units occupied
		for(Unit u : board.activeUnits) {
			int tileX = u.getPosition().tilex;
			int tileY = u.getPosition().tiley;
			Tile tempTile = board.getTile(tileX, tileY);
			availableTiles.remove(tempTile);
		}
	}
	
	//get a card from hand
	public Card getCard(int i) {
		return player.getCard(i);
	}

	//draw avatar
	public void setAndDrawAvatar(ActorRef out, int i, int j) {
		
		BasicCommands.addPlayer1Notification(out, ""+avatar.id, 2);;
		Tile target = board.getTile(i, j);
		target.setOwnership(player.humanOrAI?1:0);
		player.avatar.setPositionByTile(target);
		BasicCommands.drawUnit(out, player.avatar, target);
		
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, avatar, 2);
		BasicCommands.setUnitHealth(out, avatar, 20);
		
		board.addUnit(avatar);
		
		this.updateAvailables();
	}

	public boolean useSelectedCard(ActorRef out, int index, int tilex, int tiley) {
		Card c = player.getCard(index-1);
		BasicCommands.addPlayer1Notification(out, ""+player.minFreePosition, 4);

		if(c instanceof UnitCard) {
			Tile target = board.getTile(tilex, tiley);
			if(availableTiles.contains(target)) {
				UnitCard uC = (UnitCard)c;
				Minion m = (Minion)utils.BasicObjectBuilders.loadUnit(player.unitInfo[c.id%10], c.id, Minion.class);
				board.addUnit(m);
				m.setHealth(uC.getHealth());
				m.setAttack(uC.getAttack());
				m.setPositionByTile(target);
				BasicCommands.drawUnit(out, m, target);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.setUnitHealth(out, m, m.getHealth());
				BasicCommands.setUnitAttack(out, m, m.getAttack());
				player.costMana(c.manacost);
				BasicCommands.setPlayer1Mana(out, player);
				this.updateAvailables();
				return true;
			} else {
				BasicCommands.drawTile(out, target, 2);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.drawTile(out, target, 0);
				return false;
			}
		} else if(c instanceof SpellCard) {
			return true;
		}

		return false;
	}

	public void deleteHandCard(ActorRef out, int tempCardIndex) {
		player.deleteHandCard(tempCardIndex-1);
		BasicCommands.deleteCard(out, tempCardIndex);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.addPlayer1Notification(out, ""+player.minFreePosition, 4);
	}
	
}
