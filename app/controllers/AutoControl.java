package controllers;

import java.util.ArrayList;
import akka.actor.ActorRef;
import org.springframework.ui.Model;
import structures.GameState;
import structures.basic.*;
import commands.*;


public class AutoControl {

	Board board;
	PlayerModel m;
	ActorRef out;
	Player ai;
	PlayerModel ememy;

	public AutoControl(ActorRef out, GameState gameState) {
		this.out = out;
		this.board = gameState.getBoard();
		this.m = gameState.getAIModel();
		this.ai = gameState.getAI();
		this.ememy = gameState.getHumanModel();
	}
	
	public void autoPlay() {
		ArrayList<Unit> aiUnits = new ArrayList<Unit>();
		for(Unit temp : board.activeUnits) {
			if(temp.belongToAI()) {aiUnits.add(temp);}
		}
		//launch attack
		for(Unit temp : aiUnits) {
			if(temp.belongToAI() && temp.getAttackNum() > -1) {
				checkAndAttack(temp);
			}
		}
		//move
		for(Unit temp : aiUnits) {
			if(temp.belongToAI() && temp.getMoveNum() > -1) { //check it!
				checkAndMove(out, temp);
			}
		}
		//launch attack again
		for(Unit temp : aiUnits) {
			if(temp.belongToAI() && temp.getAttackNum() > -1) {
				checkAndAttack(temp);
			}
		}
		//use card
		Card temp;
		System.out.println("plyer has "+ai.getMana()+" this round.");
		for(int index = 0; index < 6; index++) {
			System.out.println("inspecint " + index + " in hand.....");
			temp = ai.getCard(index);
			if(temp != null && temp.getManacost() <= ai.getMana()) {
				System.out.println("Checking Card No."+temp.getId());
				if(temp instanceof UnitCard) {	
					Tile target = this.chooseTarget((UnitCard) temp);
					if(target != null) {
						m.putCardOntoBoard(out, (UnitCard) temp, target);
						ai.deleteHandCard(index);
						System.out.println("Card No."+temp.getId()+" is put to "+target.getTilex()+", "+target.getTiley());
					}
				} else {
					if(temp.getId() % 10 == 0) {
						//+2attack to avatar
						ai.avatar.changeAttack(2);
						BasicCommands.setUnitAttack(out, ai.avatar, ai.avatar.getAttack());
						try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.setUnitAttack(out, ai.avatar, ai.avatar.getAttack());
						System.out.println(ai.avatar.getAttack());//test
						ai.deleteHandCard(index);
						ai.costMana(temp.getManacost());
						System.out.println("Card No." + temp.getId() + " spells!");
					} else {
						//=0health to a non avatar
						Minion target = null;
						int maxHealth = 0;
						for(Unit i : board.activeUnits) {
							if(i.getId()<100) {
								if(((Minion) i).getHealth() > maxHealth) {
									maxHealth = ((Minion) i).getHealth();
									target = ((Minion) i);
								}
							}
						}
						if(target != null) {
							target.setHealth(0);
							//target dies
							board.activeUnits.remove(target);
							BasicCommands.deleteUnit(out, target);
							board.getTile(target.getX(), target.getY()).setOwnership(-1);
							
							ai.deleteHandCard(index);
							ai.costMana(temp.getManacost());
							BasicCommands.setPlayer2Mana(out, ai);
							System.out.println("Card No." + temp.getId() + " spells!");
						}
					}
					for(Unit i : board.activeUnits) {
						if(i instanceof Minion) {
							if(((Minion) i).getP1()) {
								i.changeAttack(1);i.changeHealth(1);}
						}
					}
					ai.deleteHandCard(index);
				}
			}
		}
	}
	
	/*
	 * attack part
	 */
	public void checkAndAttack(Unit u) {
		System.out.println("checking attack option of unit "+u.getId()+" at "+u.getX()+","+u.getY());
		ArrayList<Unit> range = getAttackRange(u);
		if(range.size()>0) {
			Unit target = getTarget(range);
			//launch attack
			if(u instanceof Minion) {u = (Minion)u;} 
			else {u = (Avatar)u;}
			if(target instanceof Minion) {target = (Minion)target;} 
			else {target = (Avatar)target;}
			
			int valAttack = u.getAttack();
			target.changeHealth(-valAttack);
			System.out.println("attacked!");
			
			if(target instanceof Avatar) {
				for(Unit i : board.activeUnits) {
					if(i instanceof Minion) {
						if(((Minion) i).getP2()) {
							i.changeAttack(2);}
					}
				}
			}
			
			if(target.getHealth() > 0) {
				//counter attack
				int val2 = target.getAttack();
				u.changeHealth(-val2);
				if(u.getHealth() == 0) {
					board.activeUnits.remove(u);
					BasicCommands.deleteUnit(out, u);
					board.getTile(u.getX(), u.getY()).setOwnership(-1);
					return;
				}
			} else {
				//if target dies
				board.activeUnits.remove(target);
				BasicCommands.deleteUnit(out, target);
				board.getTile(target.getX(), target.getY()).setOwnership(-1);

			}
			u.decAttackNum();
		}
	}
		
	public ArrayList<Unit> getAttackRange(Unit u){
		ArrayList<Unit> range = new ArrayList<Unit>();	//attack range
		if(u instanceof Minion) {
			//if u is a unit with ranged ability
			if(((Minion) u).getRanged()) {
				for(Unit i : board.activeUnits) {
					if(!i.belongToAI()) {range.add(i);}
				}
				return range;
			}
		}
		//if is common units, only neighbour enemy unit should be added to range
		for(Unit i : board.activeUnits) {
			if(u.isNeighbour(i) && !i.belongToAI()) {
				range.add(i);
			}
		}
		return range;
	}
	
	public Unit getTarget(ArrayList<Unit> range) {	
		for(Unit i : range) {
			//if there is a neighbour has provoke ability
			if(i instanceof Minion) {
				if(((Minion) i).getProvoke()){
					return i;
				}
			}
		}
		//if there is no neighbour has provoke ability
		return range.get(0);
	}
	
	/*
	 * move part
	 */
	public void checkAndMove(ActorRef out, Unit u) {
		int direction = 0;
		int dx = 0, dy = 0;
		System.out.println("checking move for unit "+u.getId());
		for(Unit i : board.activeUnits) {
			if(!i.belongToAI()) {
				direction = checkRoute(u, i);
				if(direction != 0) {break;}
			}
		}
		//decide which direction to move
		switch(direction) {
		case(-2):{dx = 0; dy = -1;};break;
		case(-1):{dx = 0; dy = 1;};break;
		case(1):{dx = 1; dy = 0;};break;
		case(2):{dx = -1; dy = 0;};break;
		}
		move(out, u, dx, dy);
		if(dx!=0||dy!=0) {System.out.println("moved");}
	}
	
	public int checkRoute(Unit a, Unit b) {
		int direction = 0;	//-2:y--, -1:y++, 1:x++, 2:x--
		boolean h = true, v = true;
		int x1 = a.getX(), x2 = b.getX();
		int y1 = a.getY(), y2 = b.getY();
		
		int x0, xL = Math.abs(x1 - x2);
		int y0, yL = Math.abs(y1 - y2);
		//check route 1 (horizontal first)
		if(x1 == x2) {h = false;}
		if(h) {
			x0 = Math.min(x1, x2);
			for(int i = 0; i < xL; i++) {
				if(board.getTile(x0 + i, y1).getOwnership() > -1) {
					h = false;
					break;
				}
			}
		}
		if(h) {
			y0 = Math.min(y1, y2);
			for(int j = 0; j < yL; j++) {
				if(board.getTile(x2, y0 + j).getOwnership() > -1) {
					h = false;
					break;
				}
			}
		}
		//check route 2 (vertical first)
		if(y1 == y2) {v = false;}
		if(v) {
			x0 = Math.min(x1, x2);
			for(int i = 0; i < xL; i++) {
				if(board.getTile(x0 + i, y2).getOwnership() > -1) {
					v = false;
					break;
				}
			}
		}
		if(v) {
			y0 = Math.min(y1, y2);
			for(int j = 0; j < yL; j++) {
				if(board.getTile(x1, y0 + j).getOwnership() > -1) {
					v = false;
					break;
				}
			}
		}
		
		if(!h && !v) {return 0;}
		if(h & x1<x2) {return 1;}
		if(h & x1>x2) {return 2;}
		if(v & y1<y2) {return -1;}
		if(v & y1>y2) {return -2;}
		else {return 0;}
	}
	
	public void move(ActorRef out, Unit u, int dx, int dy) {
		int x0 = u.getX(), y0 = u.getY();
		//remove from the current tile
		board.getTile(x0, y0).setOwnership(-1);
		u.changePosition(dx, dy);
		//move to the target
		board.getTile(x0+dx, y0+dy).setOwnership(1);
		Tile target = board.getTile(x0+dx, y0+dy);
		//boolean horizontalFirst = ;//not used temporarily
		BasicCommands.moveUnitToTile(out, u, target);
	}
	
	/*
	 * put card part
	 */
	public Tile chooseTarget(UnitCard u) {
		if(u.searchAbility("free_summon")) {
			m.freeSummon = new ArrayList<Tile>();
			for(int i = 0; i < 9; i++) {
				for(int j = 0; j < 5; j++) {
					Tile t = board.getTile(i, j);

					if(t.getOwnership() == -1) {m.freeSummon.add(t);}
				}
			}
			m.allowedTiles = m.freeSummon;
		} else {
			m.allowedTiles = m.availableTiles;
		}
		
		if(m.allowedTiles.size()>0) {
			int selection = m.allowedTiles.size()/2;
			return m.allowedTiles.get(selection);
		} else {return null;}
	}
	
}