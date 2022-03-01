package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;

/* containing all the methods related to single player 
 * such as draw card, use card, etc. */
public class PlayerModel {
	
	private static int humanID = 100;
	private static int aiID = 101;
	
	private Board board;
	
	private Player player;
	public Avatar avatar;
	
	//storing the allowed tiles to put card;
	//among those three availableTiles is maintained throughout the game (always updating)
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

	//initialise the cards in hand, i.e. obtain 3 cards from deck
	public void initHand(ActorRef out) {
		for(int i = 0; i < Player.numOfInitHand; i++) {
			player.getHandCard();
			BasicCommands.drawCard(out, player.getCard(i), i+1, 0);
		}
	}
	
	//get a card from deck, considering the position in hand to insert the card
	public void drawOneCard(ActorRef out) {
		int index = player.minFreePosition;
		player.getHandCard();
		if(index < Player.numOfHandCard) {
			BasicCommands.drawCard(out, player.getCard(index), index+1, 0);
		}
	}
	
	//show the available tiles on board for the card on the index^th position in hand
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
			//turn on the highlight
			highlightControl(out, 1);
			
		} else if(c instanceof SpellCard) {







			//turn off the highlight
			highlightControl(out, 0);
			//specific method to be added
		}
	}
	
	//the switch highlight of the allowed tiles to put card
	//called in showAvailables and EventResponders like otherClicked
	public void highlightControl(ActorRef out, int mode) {
		for(Tile t : this.availableTiles) {
			BasicCommands.drawTile(out, t, mode);
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		}
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//update allowed tiles to put unit cards (in common cases, not apply for free_summon cards)
	//this should be called every time after change the position of units (including add or remove unit)
	public void updateAvailables() {
		
		//firstly, clear available tiles, to prepare for populate with the updated information
		availableTiles.clear(); 
		//count the units around the units owned by the player
		for(Unit u : board.activeUnits) {
			int tileX = u.getPosition().tilex;
			int tileY = u.getPosition().tiley;
			int indicator = player.humanOrAI?1:0;
			//update availableTiles
			if(u.id/Deck.capacity==indicator || u.id==100+indicator) {
				for(int i = Math.max(tileX-1, 0); i <= Math.min(tileX+1, 8); i++) {
					for(int j = Math.max(tileY-1, 0); j <= Math.min(tileY+1, 4); j++) {
						Tile tempTile = board.getTile(i, j);
						if(!availableTiles.contains(tempTile)){availableTiles.add(tempTile);}
					}
				}
			}
		}
		//and then remove the units has been occupied
		for(Unit u : board.activeUnits) {
			int tileX = u.getPosition().tilex;
			int tileY = u.getPosition().tiley;
			Tile tempTile = board.getTile(tileX, tileY);
			availableTiles.remove(tempTile);
		}
	}
	
	//(getter) get a card from hand
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

	//use the previously selected card, index indicates the previously selected card
	public boolean useSelectedCard(ActorRef out, int index, int tilex, int tiley) {
		Card c = player.getCard(index-1);
		BasicCommands.addPlayer1Notification(out, ""+player.minFreePosition, 4);

		if(c instanceof UnitCard) {
			Tile target = board.getTile(tilex, tiley);
			if(allowedTiles.contains(target)) {
				UnitCard uC = (UnitCard)c;
				Minion m = (Minion)utils.BasicObjectBuilders.loadUnit(player.unitInfo[c.id%10], c.id, Minion.class);
				board.addUnit(m);
				//write information to the newly created minion object
				m.setHealth(uC.getHealth());
				m.setAttack(uC.getAttack());
				if(uC.searchAbility("twice_attack")) {
					m.setAttackNum(2);
					m.setMoveNum(2);
				}
				m.setInitHealth(uC.getHealth());
				//record the position
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
			String string = null;
			if (c.getId() == 0 || c.getId() == 10) {
				string = "truestrike";
			}
			if (c.getId() == 1 || c.getId() == 11) {
				string = "sundrop_elixir";
			}
			if (c.getId() == 20 || c.getId() == 30) {
				string = "staff_of_ykir";
			}
			if (c.getId() == 21 || c.getId() == 31) {
				string = "entropic_decay";
			}
			switch (string) {
				case "truestrike":
					BasicCommands.addPlayer1Notification(out, "请选择攻击目标！", 3);//提示
					Unit attackUnit;
					for (Unit u : board.activeUnits) {//找到可攻击对象
						if (u.id / Deck.capacity == 1 || u.getId() == 101) {//对方unit
							if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
								Tile target = board.getTile(tilex, tiley);//锁定tile目标
								BasicCommands.drawTile(out, target, 1);//tile变白
								attackUnit = u;
								System.out.println(attackUnit.getId());
								attackUnit.changeHealth(-2);//攻击
								try {
									BasicCommands.playUnitAnimation(out, attackUnit, UnitAnimationType.hit);
									Thread.sleep(500);
								} catch (Exception e) {
									e.printStackTrace();
								}//动画
								BasicCommands.drawTile(out, target, 0);//tile颜色复原
								if (attackUnit.getHealth() <= 0) {//判断生命值小于0死了
									BasicCommands.addPlayer1Notification(out, "玩家失去unit", 3);//提示
									try {
										BasicCommands.playUnitAnimation(out, attackUnit, UnitAnimationType.death);
										Thread.sleep(500);
									} catch (Exception e) {
										e.printStackTrace();
									}//动画
									int tileX = attackUnit.getPosition().tilex;
									int tileY = attackUnit.getPosition().tiley;
									Tile tempTile = board.getTile(tileX, tileY);
									availableTiles.remove(tempTile);//移除
									BasicCommands.deleteUnit(out, attackUnit);//移除
									if (attackUnit.getId() == 8 || attackUnit.getId() == 18) {//如果小于生命值的卡是这
										drawOneCard(out);//被动技能抽牌
										BasicCommands.addPlayer1Notification(out, "被动触发，恭喜你得到了一张卡", 3);
									}
								} else {
									BasicCommands.setUnitHealth(out, attackUnit, attackUnit.getHealth());
								}
							}
						}
					}
					break;

				case "sundrop_elixir":
					BasicCommands.addPlayer1Notification(out, "请选择回血目标！", 3);//提示
					Unit addHealthUnit = null;
					for (Unit u : board.activeUnits) {//找到板上的目标
						if (u.id / Deck.capacity == 0 && u.getId() != 100) {//判断己方unit
							if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
								Tile target = board.getTile(tilex, tiley);//锁定tile目标
								BasicCommands.drawTile(out, target, 1);//tile变白
								u = addHealthUnit;
								addHealthUnit.changeHealth(5);//+5生命值,且不需要判断
								BasicCommands.addPlayer1Notification(out, "恭喜回血完成", 3);//提示
								BasicCommands.setUnitHealth(out, addHealthUnit, addHealthUnit.getHealth());
							}
						}
					}
					break;

				case "staff_of_ykir":
					Unit addAavatarAttack = null;
					BasicCommands.addPlayer1Notification(out, "请选择目标！", 3);//提示
					for (Unit u : board.activeUnits) {
						if (u.getId() == 101) {//判断自己的avator
							if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
								Tile target = board.getTile(tilex, tiley);//锁定tile目标
								BasicCommands.drawTile(out, target, 1);//tile变白
								u = addAavatarAttack;
								addAavatarAttack.changeAttack(2);
								BasicCommands.addPlayer1Notification(out, "攻击力改变成功！", 3);//提示
								BasicCommands.setUnitAttack(out, u, u.getAttack());
							}
						}
						if (u.getId() == 8 || u.getId() == 8) {//卡的被动技能
							u.changeAttack(1);
							u.changeHealth(1);
							BasicCommands.setUnitHealth(out, u, u.getHealth());
							BasicCommands.setUnitAttack(out, u, u.getAttack());
						}
					}
					break;

				case "entropic_decay":
					Unit LetHealthTo0 = null;
					BasicCommands.addPlayer1Notification(out, "请选择攻击目标！", 3);//提示
					for (Unit u : board.activeUnits) {
						if (u.id / Deck.capacity == 0 && u.getId() != 100) {//判断敌方unit
							if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
								Tile target = board.getTile(tilex, tiley);//锁定tile目标
								BasicCommands.drawTile(out, target, 1);//tile变白
								u = LetHealthTo0;
								LetHealthTo0.setHealth(0);
								BasicCommands.addPlayer1Notification(out, "失去unit", 3);//提示
								try {
									BasicCommands.playUnitAnimation(out, LetHealthTo0, UnitAnimationType.death);
									Thread.sleep(500);
								} catch (Exception e) {
									e.printStackTrace();
								}//动画
								int tileX = LetHealthTo0.getPosition().tilex;
								int tileY = LetHealthTo0.getPosition().tiley;
								Tile tempTile = board.getTile(tileX, tileY);
								availableTiles.remove(tempTile);
								BasicCommands.deleteUnit(out, LetHealthTo0);
							}
						}
						if (u.getId() == 8 || u.getId() == 8) {//卡的被动技能
							u.changeAttack(1);
							u.changeHealth(1);
							BasicCommands.setUnitHealth(out, u, u.getHealth());
							BasicCommands.setUnitAttack(out, u, u.getAttack());
						}
					}
					break;
				default:
					return true;
			}
			return true;
		}

		return false;
	}

	//delete a hand card, called when the card is used
	public void deleteHandCard(ActorRef out, int tempCardIndex) {
		player.deleteHandCard(tempCardIndex-1);
		BasicCommands.deleteCard(out, tempCardIndex);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.addPlayer1Notification(out, ""+player.minFreePosition, 4);
	}
	
}
