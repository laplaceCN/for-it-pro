package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
	

	public void clearHumanMana(ActorRef out) {
		human.setMana(0);
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
			board.activeUnits.get(i).setMoveNum(1);
			if(board.activeUnits.get(i).getId() == 4||board.activeUnits.get(i).getId() == 27||
					board.activeUnits.get(i).getId() == 14||board.activeUnits.get(i).getId() == 37){
				board.activeUnits.get(i).setAttackNum(2);
				board.activeUnits.get(i).setMoveNum(2);
			}
		}

	}
	//这是为了ai方法准备的
//	public void aiMethod() {
//
//		int playerUnitNumber = 0;
//		int aiUnitNumber = 0;
//
//
//
//		for (int i = 0; i < board.activeUnits.size(); i++) {
//			if ((board.activeUnits.get(i).getId() < 20 || board.activeUnits.get(i).getId() == 100)) {
//				playerUnitNumber ++;
//			}else {aiUnitNumber ++;}
//		}
//		if(playerUnitNumber > 3 || aiUnitNumber < 3){
//			//判断有没有合适的魔法卡
//			// 召唤在avatar的后面保存实力
//		}else if(playerUnitNumber <3 ){
//			//判断有没有合适的魔法卡
//			//召唤在能召唤的最前面（最左边)(激进策略)
//		}else {
//			//判断有没有合适的魔法卡
//			//召唤在avatar的前面
//		}
//		//召唤流程结束
//		//开始移动与攻击流程
//		//所有的卡向左移动一格
//		for (int i = 0; i < board.activeUnits.size(); i++) {
//			if((board.activeUnits.get(i).getId()%20 == 1)||
//					board.activeUnits.get(i).getId() == 101){
//				if(board.activeUnits.get(i).getPosition().getTilex() == 1){
//					//向右移动一格
//				}else {
//					//向左移动一格
//				}
//			}
//		}
//		//然后攻击能攻击的对象，按照id倒序配对
//		//遍历所有ai卡每个去攻击
//		for (int i = 0; i < board.activeUnits.size(); i++) {
//			if((board.activeUnits.get(i).getId()%20 == 1)||
//					board.activeUnits.get(i).getId() == 101){
//				//搜索范围内的敌人，攻击
//				ArrayList<Unit> temp = new ArrayList<>();//每个ai卡范围内的敌人列表
//				for (int j = 0; j < board.activeUnits.size(); i++) {
//					if((board.activeUnits.get(j).getId()%20 == 0)||
//							board.activeUnits.get(j).getId() == 100){
//						//在攻击范围内的敌人列表
//						if (Math.abs(board.activeUnits.get(j).getPosition().getTilex()
//								- board.activeUnits.get(i).getPosition().getTilex())+Math.abs(board.activeUnits.get(j).getPosition().getTiley()
//								- board.activeUnits.get(i).getPosition().getTiley()) < 3){
//							temp.add(board.activeUnits.get(j));
//
//						}
//						Unit maxed = Collections.max(temp);
//						Unit attacker = board.activeUnits.get(i);
//
//						//ai的攻击方法,传入两个unit对象，这里不需要xy坐标了。
//
//					}
//
//				}
//			}
//
//		}
//		//攻击结束
//
//
//
//		//总体思路用优先级调试程序，模仿人类思路，首先判断场上情况，然后根据优先级释放卡牌
//		//列举玩家有几个unit，方便以后调试优先级
//		//判断哪个unit可以被攻击
//		//列举出自己能攻击的unit（分支：己方高价值unit进行加强）
//		//匹配攻击对（ai的unit和玩家的unit），规则：根据id倒叙依次进行攻击，优先攻击玩家
//		//执行指令
//
//
//	}
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
		if(tempUnit.getId() == 101 || ((tempUnit.getId() >= 20) && (tempUnit.getId() <100))){
			BasicCommands.addPlayer1Notification(out,"请选择不要ai召唤的单位",2);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			getHumanModel().updateAvailables();
			return;
		}
		//还要知道移动到哪里
		Tile tile = board.getTile(x, y);

		BasicCommands.moveUnitToTile(out,tempUnit,tile);
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
		tempUnit.setMoveNum(tempUnit.getMoveNum() -1);
		board.getTile(x,y).setOwnership(0);
		int previousX = tempUnit.getPosition().getTilex();
		int previousY = tempUnit.getPosition().getTiley();
		board.getTile(previousX,previousY).setOwnership(-1);
		BasicCommands.addPlayer1Notification(out,""+previousX+", "+previousY,2);
		tempUnit.getPosition().setTilex(x);
		tempUnit.getPosition().setTiley(y);
		getHumanModel().updateAvailables();

	}


	//判断是否要攻击
	public boolean checkAttackUnit(int x, int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			//找到点击的卡
			if ((board.activeUnits.get(i).getPosition().getTilex() == x)
					&& board.activeUnits.get(i).getPosition().getTiley() == y) {
				Unit tempunit = board.activeUnits.get(i);
				//判断攻击对象是否属于ai
				if ((tempunit.getId() >20) && (tempunit.getId() != 100)){
					return true;
				}
			}
		}

		return false;
	}
	//判断是否要移动
	public boolean checkMoveUnit(int x, int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if ((board.activeUnits.get(i).getPosition().getTilex() == x)
					&& board.activeUnits.get(i).getPosition().getTiley() == y) {
				return false;
			}
		}
		return true;
	}
	//只考虑了range
	public boolean checkMoveLocation(ActorRef out,int x,int y) {
		System.out.println(tempUnit.getId());
		if(tempUnit.getMoveNum() == 0 || tempUnit.getAttackNum() == 0){
			System.out.println(tempUnit.getAttackNum());
			System.out.println(tempUnit.getMoveNum());
			return false;
		}
		if(tempUnit.getId() == 18 || tempUnit.getId() == 8){//判断是否有飞行功能
			//判断移动的位置是否已经有己方unit，因为攻击方法里只判断了是否存在属于ai的unit
			for (int i = 0; i < board.activeUnits.size(); i++) {
				if ((board.activeUnits.get(i).getPosition().getTilex() == x)
						&& board.activeUnits.get(i).getPosition().getTiley() == y) {
					BasicCommands.addPlayer1Notification(out,"请不要重叠单位",2);
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
					return false;
				}
			}
			return true;
		}
		//判断移动的距离是否超过范围
		if((Math.abs((tempUnit.getPosition().getTilex() - x))+Math.abs((tempUnit.getPosition().getTiley() - y))<3)){
			//判断移动的位置是否已经有己方unit，因为攻击方法里只判断了是否存在属于ai的unit
			for (int i = 0; i < board.activeUnits.size(); i++) {
				if ((board.activeUnits.get(i).getPosition().getTilex() == x)
						&& board.activeUnits.get(i).getPosition().getTiley() == y) {
					BasicCommands.addPlayer1Notification(out,"请不要重叠单位",2);
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
					return false;
				}
			}
			return true;
		}
		return false;
	}
	//只考虑了特殊攻击范围的卡
	public boolean checkAttackLocation(int x, int y) {
		//判断攻击次数是否合法
		if(tempUnit.getAttackNum() == 0){
			return false;

		}

		if(tempUnit.getId() == 5 || tempUnit.getId() == 15||tempUnit.getId() == 25 || tempUnit.getId() == 35){
			ArrayList<Unit> temp = new ArrayList<>();//每个玩家卡范围内的敌人列表

			for (int i = 0; i < board.activeUnits.size(); i++) {
				if((board.activeUnits.get(i).getId()%20 == 1)||
						board.activeUnits.get(i).getId() == 101){
				//在攻击范围内的敌人列表
				if (Math.abs(board.activeUnits.get(i).getPosition().getTilex()
						- x)+Math.abs(board.activeUnits.get(i).getPosition().getTiley()
						- y) < 3){
					temp.add(board.activeUnits.get(i));

				}
					for (int j = 0; j < temp.size(); j++) {
						if(temp.get(j).getId() == 7 || temp.get(j).getId() == 9 || temp.get(j).getId() == 26||
						temp.get(j).getId() == 17 || temp.get(j).getId() == 19 || temp.get(j).getId() == 36){
							if(temp.get(j).getPosition().getTilex() == x && temp.get(j).getPosition().getTiley() == y){
								return true;
							}
							return false;


						}
					}
				}
			}
			return true;
		}
		if((Math.abs((tempUnit.getPosition().getTilex() - x))+Math.abs((tempUnit.getPosition().getTiley() - y))<3)){
			return true;}
		return false;
	}

}
