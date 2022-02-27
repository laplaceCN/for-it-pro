package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.*;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	
	//token indicating human or AI
	private static boolean isHuman = false;
	private static boolean isAI = true;

	//round number
	public int numRound = 1;
	
	//state tokens
	public boolean gameInitalised = false;	
	public boolean cardClickedAndWaiting = false;
	public boolean tileClickedAndWaiting = false;
	
	public boolean something = false;
	
	//temporary containers
	public int tempCardIndex;
	public Unit tempUnit;


	// the Board
	private Board board;
	// the players
	private Player human;
	private Player ai;
	// the player models
	private PlayerModel humanModel;
	private PlayerModel aiModel;
	private BoardModel boardModel;
	
	public GameState() {
		
		// Create a Board
		board = new Board();
		
		// Create players and their player model
		human = new Player(isHuman);
		ai = new Player(isAI);
		
		// Create players' playerModels
		humanModel = new PlayerModel(human, board);
		aiModel = new PlayerModel(ai, board);
		boardModel = new BoardModel(human, ai, board);
	}
	
	public void showStatisticsAndCountRound(ActorRef out) {
		//set mana and count round number
		human.setMana(numRound+1);
		ai.setMana(numRound+1);
		//show them
		BasicCommands.setPlayer1Health(out, human);
		BasicCommands.setPlayer2Health(out, ai);
		BasicCommands.setPlayer1Mana(out, human);
		BasicCommands.setPlayer2Mana(out, ai);
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		//and count round
		numRound++;
	}

	public void clearHumanMana(ActorRef out) {
		human.setMana(0);
		BasicCommands.setPlayer1Mana(out, human);
	}
	public void setHumanMana(ActorRef out) {
		human.setMana(numRound + 2);
		BasicCommands.setPlayer1Mana(out, human);
	}


	//getters
	public Board getBoard() {
		return board;
	}
	
	public Player getHuman() {
		return human;
	}
	
	public Player getAI() {
		return ai;
	}
	
	public PlayerModel getHumanModel() {
		return humanModel;
	}
	
	public PlayerModel getAIModel() {
		return aiModel;
	}
	
	public BoardModel getBoardModel() {
		return boardModel;
	}

	//这个方法是为了重置卡片的攻击次数与移动次数限制（目前还没有考虑特殊卡片的情况）
	//目前也没有区分属于玩家的卡和属于怪兽的卡，虽然不影响效果

	public void recoverCardState() {

		for (int i = 0; i < board.activeUnits.size(); i++) {
			board.activeUnits.get(i).setAttackNum(1);
			board.activeUnits.get(i).setMoveNum(1);;

		}

	}
	//这是为了ai方法准备的
	public void aiMethod() {


	}
	//这是为了攻击方法准备的
	public void Attack(ActorRef out,int x,int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if ((board.activeUnits.get(i).getPosition().getTilex() == x) &&
					(board.activeUnits.get(i).getPosition().getTiley() == y)) {
				Unit united = board.activeUnits.get(i);
				Unit tempUniting = this.tempUnit;
				if(united instanceof Avatar) {//被攻击者属于玩家
					if (tempUniting instanceof Minion) {//攻击者属于怪兽
						Avatar avatared = (Avatar) united;// 被攻击者名称后面加了ed
						Minion minioning = (Minion) tempUniting;// 攻击者名称后面加了ing

						int attackUnitHealth = minioning.getHealth();

						int attackedUnitHealth = ai.getHealth();
						//被攻击者血量
						avatared.changeHealth(attackedUnitHealth -= minioning.getAttack());
						//若被攻击后被攻击者生命值大于0
						if (attackedUnitHealth >= 0) {
							//进行反击
							minioning.setHealth(attackUnitHealth -= avatared.getAttackNum());
							if (attackUnitHealth >= 0) {//若攻击者的生命值大于等于0
								BasicCommands.setUnitHealth(out, minioning, attackUnitHealth);
								BasicCommands.setUnitHealth(out, avatared, attackedUnitHealth);
							} else {//攻击者的生命值小于0的情况


							}
						} else {//被攻击者受到攻击之后生命值小于0的情况，删除卡牌，出发结束游戏


						}
					}
					//攻击者也属于玩家类
					else {


					}
				}

//				else if (unit instanceof Minion ){
//					Minion minion = (Minion)unit;
//
//					Unit tempUnit = this.tempUnit;
//
//					int attackUnitHealth = tempUnit.getHealth();
//
//					int attackedUnitHealth = unit.getHealth();
//					//被攻击者血量
//					unit.setHealth(attackedUnitHealth -= tempUnit.getAttack());
//					//若被攻击后生命值大于0
//					if (attackedUnitHealth >= 0) {
//						//进行反击
//						tempUnit.setHealth(attackUnitHealth -= unit.getAttack());
//						if (attackUnitHealth >= 0) {
//							BasicCommands.setUnitHealth(out, tempUnit, attackUnitHealth);
//							BasicCommands.setUnitHealth(out, tempUnit, attackedUnitHealth);
//						} else {
//						}
//					}
//
//
//				}



			}

		}





	}
	//这是为了移动方法准备的
	public void move(ActorRef out,int x, int y) {
		//知道要移动哪张卡
		Unit tempUnit = this.tempUnit;
		//还要知道移动到哪里
		Tile tile = board.getTile(x, y);

		BasicCommands.moveUnitToTile(out,tempUnit,tile);
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

	}



	public boolean checkAttackUnit(int x, int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			//找到点击的卡
			if ((board.activeUnits.get(i).getPosition().getTilex() == x)
					&& board.activeUnits.get(i).getPosition().getTiley() == y) {
				Unit tempunit = board.activeUnits.get(i);
				//判断攻击对象是否属于ai
				if ((tempunit.getId() >20) || (tempunit.getId() != 100)){
					return true;
				}
			}
		}

		return false;
	}

	public boolean checkMoveUnit(int x, int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if ((board.activeUnits.get(i).getPosition().getTilex() == x)
					&& board.activeUnits.get(i).getPosition().getTiley() == y) {
				return false;
			}
		}
		return true;
	}
	//没有考虑特殊卡
	public boolean checkMoveLocation(int x,int y) {
		if((Math.abs((tempUnit.getPosition().getTilex() - x))+Math.abs((tempUnit.getPosition().getTiley() - y))<3)){

		return true;}
		return false;
	}
	//没有考虑特殊卡
	public boolean checkAttacLocation(int x, int y) {
		if((Math.abs((tempUnit.getPosition().getTilex() - x))+Math.abs((tempUnit.getPosition().getTiley() - y))<3)){

			return true;}
		return false;
	}
}
