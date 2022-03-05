package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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
			this.avatar = (Avatar)utils.BasicObjectBuilders.loadUnit(utils.StaticConfFiles.aiAvatar, aiID, Avatar.class);
		}
		
		this.avatar.setPlayer(player);
		this.player.setAvatar(avatar);
	}

	//initialise the cards in hand, i.e. obtain 3 cards from deck
	public void initHand(ActorRef out) {
		for(int i = 0; i < Player.numOfInitHand; i++) {
			player.getHandCard();
			if(!player.humanOrAI) {BasicCommands.drawCard(out, player.getCard(i), i+1, 0);}
		}
	}
	
	//get a card from deck, considering the position in hand to insert the card
	public void drawOneCard(ActorRef out) {
		int index = player.minFreePosition;
		player.getHandCard();
		if(index < Player.numOfHandCard) {
			if(!player.humanOrAI){BasicCommands.drawCard(out, player.getCard(index), index+1, 0);}
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
				updateAvailables();
				allowedTiles = availableTiles;
			}
			//turn on the highlight
			highlightControl(out, 1);
			
		} else if(c instanceof SpellCard) {
			String string = "";
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
					for (Unit u : board.activeUnits) {
						if (u.id / Deck.capacity == 1 && u.getId() != 101) {
							int x = u.getPosition().tilex;
							int y = u.getPosition().tiley;
							Tile t = board.getTile(x, y);//锁定tile目标
							System.out.println("123");//测试
							BasicCommands.addPlayer1Notification(out, "请选择攻击目标！", 3);
							BasicCommands.drawTile(out, t, 1);
						}
					}
					break;
				case "sundrop_elixir":
					for (Unit u : board.activeUnits) {
						if (u.id / Deck.capacity == 0 && u.getId() != 100) {
							int x = u.getPosition().tilex;
							int y = u.getPosition().tiley;
							Tile t = board.getTile(x, y);//锁定tile目标
							BasicCommands.addPlayer1Notification(out, "请选择回血目标！", 3);
							BasicCommands.drawTile(out, t, 1);
						}
					}
					break;
				case "staff_of_ykir":
					for (Unit u : board.activeUnits) {
						if (u.getId() == 100) {
							int x = u.getPosition().tilex;
							int y = u.getPosition().tiley;
							Tile t = board.getTile(x, y);
							BasicCommands.addPlayer1Notification(out, "请选择目标！", 3);
							BasicCommands.drawTile(out, t, 1);
						}
					}
					break;
				case "entropic_decay":
					for (Unit u : board.activeUnits) {
						if (u.id / Deck.capacity == 0 && u.getId() != 100) {//判断敌方unit
							int x = u.getPosition().tilex;
							int y = u.getPosition().tiley;
							Tile t = board.getTile(x, y);//锁定tile目标
							BasicCommands.drawTile(out, t, 1);//tile变白
							BasicCommands.addPlayer1Notification(out, "攻击目标！", 3);

						}
					}
					break;
			}
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
		BasicCommands.setUnitAttack(out, avatar, avatar.getAttack());
		BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
		System.out.println("PUT: attack="+ avatar.getAttack() + ", health="+ avatar.getHealth());
		
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
				return useUnitCard(out, c, tilex, tiley);
			} else {
				BasicCommands.drawTile(out, target, 2);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.drawTile(out, target, 0);
				return false;
			}
		} else if(c instanceof SpellCard) {
			Tile target = board.getTile(tilex, tiley);//锁定tile目标
			String string = "";
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
				case "truestrike"://玩家魔法卡1
					Unit attackUnit;
					for(Unit u : board.activeUnits){
						if(u.getPosition().tilex == tilex && u.getPosition().tiley == tiley){
							if (u.id / Deck.capacity == 1 && u.getId() != 101) {//为了测试 101有问题
								//System.out.println("456");
								attackUnit = u;
								//System.out.println(u.getId());
								attackUnit.changeHealth(-2);//攻击
								//System.out.println(attackUnit1.getHealth());

								if (attackUnit.getHealth() <= 0) {//判断生命值小于0死了
									if (attackUnit.getId() == 8 || attackUnit.getId() == 18) {//如果小于生命值的卡是这
										drawOneCard(out);//被动技能抽牌
										BasicCommands.addPlayer1Notification(out, "被动触发，恭喜你得到了一张卡", 3);
									}
									else {
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
									}
									try {BasicCommands.playUnitAnimation(out, attackUnit, UnitAnimationType.hit);Thread.sleep(500);} catch (Exception e) {e.printStackTrace();}//动画
								}

								player.costMana(c.manacost);
								BasicCommands.setPlayer1Mana(out, player);
								BasicCommands.setUnitHealth(out, attackUnit, attackUnit.getHealth());
								return true;
							}
						}
					}
					break;

				case "sundrop_elixir"://玩家魔法卡2
					Unit addHealthUnit;
					for(Unit u : board.activeUnits) {
						if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
							if (u.id / Deck.capacity == 0 && u.getId() != 100) {
								addHealthUnit = u;
								addHealthUnit.changeHealth(5);//+5生命值,且不需要判断
								player.costMana(c.manacost);
								BasicCommands.setPlayer1Mana(out, player);
								BasicCommands.addPlayer1Notification(out, "恭喜回血完成", 3);//提示
								BasicCommands.setUnitHealth(out, addHealthUnit, addHealthUnit.getHealth());
								return true;
							}
						}
					}
					break;

				case "staff_of_ykir"://AI魔法卡1
					Unit addAavatarAttack;
					for(Unit u : board.activeUnits) {
						if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
							if (u.getId() == 101) {
								addAavatarAttack = u;
								addAavatarAttack.changeAttack(2);
								player.costMana(c.manacost);
								BasicCommands.setPlayer1Mana(out, player);
								BasicCommands.addPlayer1Notification(out, "攻击力改变成功！", 3);//提示
								BasicCommands.setUnitAttack(out, u, u.getAttack());
								//逻辑有问题吗？
								if (u.getId() == 8 || u.getId() == 18) {//卡的被动技能
									u.changeAttack(1);
									u.changeHealth(1);
									BasicCommands.setUnitHealth(out, u, u.getHealth());
									BasicCommands.setUnitAttack(out, u, u.getAttack());
									BasicCommands.addPlayer1Notification(out, "被动生效", 3);//提示
									return true;
								}
							}
						}
					}
					break;

				case "entropic_decay"://Ai魔法卡2
					Unit LetHealthTo0;
					for(Unit u : board.activeUnits) {
						if (u.getPosition().tilex == tilex && u.getPosition().tiley == tiley) {
							if (u.id / Deck.capacity == 0 && u.getId() != 100) {
								LetHealthTo0 = u;
								LetHealthTo0.setHealth(0);
								player.costMana(c.manacost);
								BasicCommands.setPlayer1Mana(out, player);
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
								if (u.getId() == 8 || u.getId() == 8) {//卡的被动技能
									u.changeAttack(1);
									u.changeHealth(1);
									BasicCommands.setUnitHealth(out, u, u.getHealth());
									BasicCommands.setUnitAttack(out, u, u.getAttack());
									return true;
								}
							}
						}
					}
					break;
				default:
					return false;
			}
			//return false;
		}
		return false;
	}
	//use unit card
	public boolean useUnitCard(ActorRef out, Card c, int tilex, int tiley) {
		Tile target = board.getTile(tilex, tiley);
		if(allowedTiles.contains(target)) {
			UnitCard uC = (UnitCard)c;
			putCardOntoBoard(out, uC, target);
			return true;
		} else {
			BasicCommands.drawTile(out, target, 2);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.drawTile(out, target, 0);
			return false;
		}
	}

	//put card onto board
	public void putCardOntoBoard(ActorRef out, UnitCard uC, Tile target) {
		if(!player.humanOrAI){highlightControl(out, 0);}
		Minion m = (Minion)utils.BasicObjectBuilders.loadUnit(player.unitInfo[uC.id%10], uC.id, Minion.class);
		board.addUnit(m);
		//write information to the newly created minion object
		m.setHealth(uC.getHealth());
		m.setAttack(uC.getAttack());
		if(uC.searchAbility("twice_attack")) {
			m.setAttackNum(2);
			m.setMoveNum(2);
		}
		if(uC.searchAbility("provoke")) {m.enableProvoke();}
		if(uC.searchAbility("ranged")) {m.enableRanged();}
		if(uC.searchAbility("passive_1")) {m.enableP1();}
		if(uC.searchAbility("passive_2")) {m.enableP2();}
		m.setInitHealth(uC.getHealth());
		//record the position
		m.setPositionByTile(target);
		BasicCommands.drawUnit(out, m, target);
		EffectAnimation effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out,effectAnimation,board.getTile(target.getTilex(),
				target.getTiley()));
		target.setOwnership(player.humanOrAI?1:0);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, m, m.getHealth());
		BasicCommands.setUnitAttack(out, m, m.getAttack());
		player.costMana(uC.manacost);
		if(!player.humanOrAI) {BasicCommands.setPlayer1Mana(out, player);}
		else {BasicCommands.setPlayer2Mana(out, player);}
		this.updateAvailables();
	}

	//delete a hand card, called when the card is used
	public void deleteHandCard(ActorRef out, int tempCardIndex) {
		player.deleteHandCard(tempCardIndex-1);
		BasicCommands.deleteCard(out, tempCardIndex);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.addPlayer1Notification(out, ""+player.minFreePosition, 4);
	}



	
}
