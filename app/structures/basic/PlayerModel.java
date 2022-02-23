package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;

public class PlayerModel {
	
	private Player player;
	private Board board;
	public ArrayList<Tile> availableTiles;
	
	
	public PlayerModel(Player p, Board b) {
		this.player = p;
		this.board = b;
		this.availableTiles = new ArrayList<Tile>();
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
			for(Tile t : this.availableTiles) {
				BasicCommands.drawTile(out, t, mode);
			}
		} else if(c instanceof SpellCard) {
			//restore the highlights
			for(Tile t : this.availableTiles) {
				BasicCommands.drawTile(out, t, 0);
			}
		}
	}
	
	public void updateAvailables() {
		for(Unit u : board.activeUnits) {
			int tileX = u.getPosition().tilex;
			int tileY = u.getPosition().tiley;
			int indicator = player.humanOrAI?1:0;
			//update availableTiles
			if(u.id/10==indicator || u.id==20+indicator) {
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

	public void setAndDrawAvatar(ActorRef out, int i, int j) {
		Tile target = board.getTile(i, j);
		player.avatar.setPositionByTile(target);
		BasicCommands.drawUnit(out, player.avatar, target);
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
			return false;
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
