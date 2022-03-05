package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.ArrayList;

/**
 * This is a utility class that provides the player with methods defined for manipulating the Unit object in the board.
 * The methods in this class can be divided into three categories, which can support those function: show, attack, and move.
 *
 * @author Ether group
 *
 */

public class BoardModel {

	Board board;
	Player human;
	Player ai;

	/**
	 * Constructor for the BoardModel. This is called by the GameState when the game initalizes.
	 *
	 * @param human,
	 * @param ai,
	 * @param board,
	 */
	
	public BoardModel(Player human, Player ai, Board board) {
		this.board = board;
		this.human = human;
		this.ai = ai;
	}

	/**
	 * The purpose of this method is to populate tile objects, which is necessary to build tile in board.
	 *
	 * @param out
	 */
	
	public void makeTiles(ActorRef out) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j ++) {
				BasicCommands.drawTile(out, board.getTile(i, j), 0);
			}
		}
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}


	/**
	 * The purpose of this method is to show the attack and movement range of cards, not including cards with special abilities.
	 * This method is usually used when the board is clicked for the first time.
	 * @param out
	 * @param x
	 * @param y
	 * @param mode
	 */

	public void showAvailables(ActorRef out, int x, int y, int mode) {
		// Displays movement and attack range
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j++) {
				Tile tile = board.getTile(i,j);
				if (tile.getOwnership() == 1){
					BasicCommands.drawTile(out,tile,2);
					try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
				}
				if((Math.abs((board.getTile(i,j).tilex - x))+Math.abs((board.getTile(i,j).tiley - y))<3)){
					if(tile.getOwnership() == 1){
						BasicCommands.drawTile(out,tile,2);
						try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
					}else if(tile.getOwnership() == 0){
						BasicCommands.drawTile(out,tile,0);
						try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
					}else
					BasicCommands.drawTile(out,tile,1);
					try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		}
		// Reset the tile that cannot be moved to
		// Check the status of nearby grids
		boolean upTile = checkUpTile(x,y);
		boolean downTile = checkDownTile(x,y);
		boolean leftTile = checkLeftTile(x,y);
		boolean rightTile = checkRightTile(x,y);
		// Reset based on the different situations that exist in nearby units
		if(upTile && y+2<5){
			Tile tile = board.getTile(x,y+2);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(downTile && y-2>-1){
			Tile tile = board.getTile(x,y-2);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(leftTile && x-2 >-1){
			Tile tile = board.getTile(x-2,y);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(rightTile && x+2<9){
			Tile tile = board.getTile(x+2,y);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(rightTile&&upTile){
			Tile tile = board.getTile(x+1,y+1);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(rightTile&&downTile){
			Tile tile = board.getTile(x+1,y-1);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(leftTile&&upTile){
			Tile tile = board.getTile(x-1,y+1);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		if(leftTile&&downTile){
			Tile tile = board.getTile(x-1,y-1);
			if (tile.ownership == -1) {
				BasicCommands.drawTile(out,tile,0);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}

	}

	/**
	 * The purpose of this method is to check whether there is unit right unit clicked
	 *
	 * @param x
	 * @param y
	 * @return
	 *
	 */

	private boolean checkRightTile(int x, int y) {
		if(x<8){
			if(board.getTile(x+1,y).ownership >= 0){
				return true;
			}
		}
		return false;

	}
	/**
	 * The purpose of this method is to check whether there is unit left unit clicked
	 *
	 * @param x
	 * @param y
	 * @return
	 *
	 */

	private boolean checkLeftTile(int x, int y) {
		if(x>0){
			if(board.getTile(x-1,y).ownership >= 0){
				return true;
			}
		}
		return false;

	}
	/**
	 * The purpose of this method is to check whether there is unit under unit clicked
	 *
	 * @param x
	 * @param y
	 * @return
	 *
	 */

	private boolean checkDownTile(int x, int y) {
		if(y>0){
			if(board.getTile(x,y-1).ownership >= 0){
				return true;
			}
		}
		return false;
	}
	/**
	 * The purpose of this method is to check whether there is unit above unit clicked
	 *
	 * @param x
	 * @param y
	 * @return
	 */

	private boolean checkUpTile(int x, int y) {
		if(y<4){
			if(board.getTile(x,y+1).ownership >= 0){
				return true;
			}
		}
		return false;

	}

	/**
	 *
	 * This method is used to reset the on-court display state.
	 * It is usually called by otherClicked
	 * @param out
	 * @param mode
	 *
	 */
	public void offAvailables(ActorRef out, int mode) {


		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j++) {
				Tile tile = board.getTile(i,j);

				BasicCommands.drawTile(out,tile,mode);
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

			}
			try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}


	/**
	 *
	 * The purpose of this method is to set the mana and health values of the player and AI on the board.
	 * This method is usually used at the beginning and end of a round.
	 * @param out
	 * @param gameState
	 *
	 */
	public void showStatisticsAndCountRound(ActorRef out, GameState gameState) {
		//set mana and count round number
		human.setMana(gameState.numRound+1);
		ai.setMana(gameState.numRound+1);
		//show them
		BasicCommands.setPlayer1Health(out, human);
		BasicCommands.setPlayer2Health(out, ai);
		BasicCommands.setPlayer1Mana(out, human);
		BasicCommands.setPlayer2Mana(out, ai);
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		//and count round
		gameState.numRound++;
	}

	/**
	 *
	 * The purpose of this method is to set the values of the player and AI on the board.
	 * This method is usually used at the beginning and end of a round.
	 * @param out
	 * @param gameState
	 *
	 */
	public void setHumanMana(ActorRef out, GameState gameState) {
		human.setMana(gameState.numRound + 1);
		BasicCommands.setPlayer1Mana(out, human);

	}
	/**
	 *
	 * The purpose of this method is to set the mana values of the player and AI on the board.
	 * This method is usually used at the beginning and end of a round.
	 * @param out
	 * @param gameState
	 *
	 */
	public void setAiMana(ActorRef out, GameState gameState) {
		ai.setMana(gameState.numRound + 1);
		BasicCommands.setPlayer2Mana(out, ai);
		gameState.numRound++;

	}
	/**
	 *
	 * TThe purpose of this method is to check for the presence of a Unit object
	 * along the path of the card movement in first possible road.
	 *
	 * This method is usually used before the card is moved.
	 * @param tile
	 * @param gameState
	 * @param x
	 * @param y
	 * @return
	 */

	public boolean checkMoveRoad(GameState gameState,Tile tile,int x, int y) {
		int tempxbig = Math.max(x,gameState.tempUnit.getPosition().getTilex());
		int tempxsmall = Math.min(x,gameState.tempUnit.getPosition().getTilex());


		for (int i = tempxsmall;
			 i <= tempxbig; i++) {

			if (board.getTile(i, gameState.tempUnit.getPosition().getTiley()).getOwnership() > -1) {

				if (board.getTile(i, gameState.tempUnit.getPosition().getTiley()) == tile) {

				}else {
				return true;}
			}
		}
		//上面的不存在就继续查询纵向有没有存在的单位
		int tempybig = Math.max(y,gameState.tempUnit.getPosition().getTiley());
		int tempysmall = Math.min(y,gameState.tempUnit.getPosition().getTiley());


		for (int j = tempysmall;
			 j <= tempybig; j++) {
			if (board.getTile(x, j).getOwnership() > -1) {

				if (board.getTile(x, j) == tile) {

				}else {
					return true;}
			}
		}
		return false;
	}

	/**
	 *
	 * TThe purpose of this method is to check for the presence of a Unit object
	 * along the path of the card movement in second possible road.
	 *
	 * This method is usually used before the card is moved.
	 * @param tile
	 * @param gameState
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean checkMoveRoad2(GameState gameState, Tile tile, int x, int y) {
		int tempxbig = Math.max(x,gameState.tempUnit.getPosition().getTilex());
		int tempxsmall = Math.min(x,gameState.tempUnit.getPosition().getTilex());

		for (int i = tempxsmall;
			 i <= tempxbig; i++) {

			if (board.getTile(i, y).getOwnership() > -1) {

				if (board.getTile(i, y) == tile) {

				}else {
					return true;}
			}
		}

		int tempybig = Math.max(y,gameState.tempUnit.getPosition().getTiley());
		int tempysmall = Math.min(y,gameState.tempUnit.getPosition().getTiley());


		for (int j = tempysmall;
			 j <= tempybig; j++) {
			if (board.getTile(gameState.tempUnit.getPosition().getTilex(), j).getOwnership() > -1) {


				if (board.getTile(gameState.tempUnit.getPosition().getTilex(), j) == tile) {

				}else {
					return true;}

			}
		}
		return false;
	}
	/**
	 * The purpose of this method is to show the attack and movement range of cards about cards with special abilities.
	 * This method is usually used when the board is clicked for the first time.
	 * @param out
	 * @param gameState
	 *
	 */

	public void speshowAvailables(GameState gameState,ActorRef out) {

		//range
		if(gameState.tempUnit.getId() == 5 || gameState.tempUnit.getId() == 15 ||
				gameState.tempUnit.getId() == 25 || gameState.tempUnit.getId() == 35
		){
			for (int i = 0; i < board.activeUnits.size(); i++) {
				if(board.activeUnits.get(i).getId()/20 == 1 || board.activeUnits.get(i).getId() == 101){
					Tile tile = board.getTile(board.activeUnits.get(i).getPosition().getTilex(),
							board.activeUnits.get(i).getPosition().getTiley());
					BasicCommands.drawTile(out,tile,2);
					try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
		if (gameState.tempUnit.getId() == 28 || gameState.tempUnit.getId() == 38) {
			//flying
			for(int i = 0; i < 9; i++) {
				for(int j = 0; j < 5; j++) {
					Tile tile = board.getTile(i,j);
					if(tile.getOwnership() == -1){
						BasicCommands.drawTile(out,tile,1);}
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

				}
				try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
			}

		}


	}

	/**
	 * The purpose of this method is to check whether the position the player is trying to move conforms to the rules of the game
	 * This method is usually used when the player clicks the board screen for the second time in a row
	 * @param out
	 * @param gameState
	 * @return
	 */


	public boolean checkMoveLocation(GameState gameState,ActorRef out,int x,int y) {


		//不能多次移动或者攻击后移动
		if(gameState.tempUnit.getMoveNum() == 0 || gameState.tempUnit.getAttackNum() < 0){
			System.out.println(gameState.tempUnit.getAttackNum());
			System.out.println(gameState.tempUnit.getMoveNum());
			return false;
		}
		if(gameState.tempUnit.getId() == 28 || gameState.tempUnit.getId() == 38){//Determine whether it has flying function
			//Check whether the moved location already has a Unit
			for (int i = 0; i < board.activeUnits.size(); i++) {
				if ((board.activeUnits.get(i).getPosition().getTilex() == x)
						&& board.activeUnits.get(i).getPosition().getTiley() == y) {
					BasicCommands.addPlayer1Notification(out,"The position has been taken",2);
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
					return false;
				}
			}
			return true;
		}
		//Determine whether the distance moved is out of range
		if((Math.abs((gameState.tempUnit.getPosition().getTilex() - x))+Math.abs((gameState.tempUnit.getPosition().getTiley() - y))<3)){
			//Check whether the moved location already has a Unit
			for (int i = 0; i < board.activeUnits.size(); i++) {
				if ((board.activeUnits.get(i).getPosition().getTilex() == x)
						&& board.activeUnits.get(i).getPosition().getTiley() == y) {
					BasicCommands.addPlayer1Notification(out,"The position has been taken",2);
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
					return false;
				}
			}
			return true;
		}
		return false;
	}


	/**
	 * The purpose of this method is to check whether the position the player is trying to attack conforms to the rules of the game
	 * This method is usually used when the player clicks the board screen for the second time in a row
	 * @param x
	 * @param gameState
	 * @param y
	 * @return
	 *
	 */

	public boolean checkAttackLocation(GameState gameState, int x, int y) {//
		//Determine if the number of attacks matches the rules of the game
		if(gameState.tempUnit.getAttackNum() <= 0){
			System.out.println("fenzhi1");
			return false;
		}
		// to deal with provoke skill

		ArrayList<Unit> temp = new ArrayList<>();//List of enemies in range of attack
		boolean flag = false;


		for (int i = 0; i < board.activeUnits.size(); i++) {
			if((board.activeUnits.get(i).getId()/20 == 1|| board.activeUnits.get(i).getId()/20 == 101)){

				if (Math.abs((gameState.tempUnit.getPosition().getTilex() - x)) <2
						&& Math.abs((gameState.tempUnit.getPosition().getTiley() - y)) <2){
					temp.add(board.activeUnits.get(i));

				}
				//Cards that can be attacked remotely
				else if(gameState.tempUnit.getId()%10 == 5 ) {
					temp.add(board.activeUnits.get(i));
				}

				//Judge whether there is provoke skill first and then judge whether the target is
				for (Unit unit : temp) {
					if (unit.getId() == 7 || unit.getId() == 9 || unit.getId() == 26 ||
							unit.getId() == 17 || unit.getId() == 19 || unit.getId() == 36) {
						if (unit.getPosition().getTilex() == x && unit.getPosition().getTiley() == y) {
							return true;
						}
						flag = true;
					}
				}
				if(flag){// for provoke skills

					return false;
				}

			}

		}
		//Check range skill
		if(gameState.tempUnit.getId()%10 == 5 ){
			return true;
		}

		return Math.abs((gameState.tempUnit.getPosition().getTilex() - x)) <2 && Math.abs((gameState.tempUnit.getPosition().getTiley() - y)) <2;
	}
	/**
	 * The purpose of this method is to determine the player's intention to move or attack
	 * This method is usually used when the player clicks the board screen for the second time in a row
	 * @param x
	 * @param y
	 * @return
	 *
	 */

	public boolean checkAttackUnit(int x, int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if ((board.activeUnits.get(i).getPosition().getTilex() == x)
					&& board.activeUnits.get(i).getPosition().getTiley() == y) {
				Unit tempunit = board.activeUnits.get(i);
				if ((tempunit.getId() >20) && (tempunit.getId() != 100)){
					return true;
				}
			}
		}

		return false;
	}
	/**
	 * The purpose of this method is to determine the player's intention to move or attack
	 * This method is usually used when the player clicks the board screen for the second time in a row
	 * @param x
	 * @param y
	 * @return
	 *
	 */
	public boolean checkMoveUnit(int x, int y) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if ((board.activeUnits.get(i).getPosition().getTilex() == x)
					&& board.activeUnits.get(i).getPosition().getTiley() == y) {
				return false;
			}
		}
		return true;
	}
	/**
	 * The purpose of this method is to perform the in-game moves that the player wants to perform
	 * and report the results back to the web page.
	 *
	 * This method is usually used when the player clicks the board screen for the second time in a row
	 * @param x
	 * @param y
	 * @param gameState
	 * @param out
	 *
	 */


	public void move(GameState gameState,ActorRef out,int x, int y) {
		//The card to be moved
		Unit tempUnit = gameState.tempUnit;
		if(tempUnit.getId() == 101 || ((tempUnit.getId() >= 20) && (tempUnit.getId() <100))){
			BasicCommands.addPlayer1Notification(out,"Please select units summoned by AI",2);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.getHumanModel().updateAvailables();
			return;
		}
		Tile tilDe = board.getTile(tempUnit.getPosition().getTilex(),tempUnit.getPosition().getTiley());
		Tile tileAr = board.getTile(x,y);
		//Get the moving target
		//Judge the moving path 1

		boolean road1 = checkMoveRoad(gameState,tilDe,x,y);

		//Judge the moving path 2

		boolean road2 = checkMoveRoad2(gameState,tilDe,x,y);
		System.out.println(road2);

		if(road1&&!road2){
			BasicCommands.moveUnitToTile(out, tempUnit, tileAr,true);

		}else if(road1&&road2){
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.addPlayer1Notification(out,"There is no path to move",2);

		}else if(!road1&&!road2){
			BasicCommands.moveUnitToTile(out, tempUnit, tileAr);


		}else {
			System.out.println("error");// for test


		}
		//Change the board state
		unitStateUpdate(gameState,out,x,y);


	}
	/**
	 * The purpose of this method is to update the storage state of the board whenever the Unit object on the board takes action.
	 *
	 * This method is usually used when the player clicks the board screen for the second time in a row
	 * @param x
	 * @param y
	 * @param out
	 * @param gameState
	 *
	 */


	private void unitStateUpdate(GameState gameState,ActorRef out,int x, int y) {
		gameState.tempUnit.setMoveNum(gameState.tempUnit.getMoveNum() -1);
		board.getTile(x,y).setOwnership(0);
		int previousX = gameState.tempUnit.getPosition().getTilex();
		int previousY = gameState.tempUnit.getPosition().getTiley();
		board.getTile(previousX,previousY).setOwnership(-1);
		BasicCommands.addPlayer1Notification(out,""+previousX+", "+previousY,2);
		gameState.tempUnit.getPosition().setTilex(x);
		gameState.tempUnit.getPosition().setTiley(y);
		gameState.getHumanModel().updateAvailables();
	}

	/**
	 * The purpose of this method is to reset the unit's attack count and move count at the end of the turn.
	 *
	 * This method is usually used when the player clicks endclicked button.
	 *
	 */

	public void recoverCardState() {


		for (int i = 0; i < board.activeUnits.size(); i++) {

			board.activeUnits.get(i).setAttackNum(1);
			board.activeUnits.get(i).setMoveNum(1);
			if(board.activeUnits.get(i).getId() == 4||board.activeUnits.get(i).getId() == 27||
					board.activeUnits.get(i).getId() == 14||board.activeUnits.get(i).getId() == 37){
				board.activeUnits.get(i).setAttackNum(3);
			}
		}

	}


	/**
	 * The purpose of this method is to reset the status of the special ability card at the end of the turn.
	 *
	 * This method is usually used when the player clicks endclicked button.
	 *
	 */

	public void perTurnSkill() {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if (board.activeUnits.get(i).getId() == 4 || board.activeUnits.get(i).getId() == 14
					||board.activeUnits.get(i).getId() == 27 || board.activeUnits.get(i).getId() == 37) {
				int oldAttack = board.activeUnits.get(i).getAttack();
				board.activeUnits.get(i).setAttack(oldAttack*2);
			}
		}

	}

	/**
	 * Call this method when the player attacks the opponent
	 * @param out
	 * @param x
	 * @param y
	 * @param gameState
	 */
	public void Attack(ActorRef out,int x,int y,GameState gameState) {
		for (int i = 0; i < board.activeUnits.size(); i++) {
			if ((board.activeUnits.get(i).getPosition().getTilex() == x) &&
					(board.activeUnits.get(i).getPosition().getTiley() == y)) {
				System.out.println(gameState.tempUnit.getAttackNum());
				gameState.tempUnit.setAttackNum(gameState.tempUnit.getAttackNum() - 2);
				System.out.println(gameState.tempUnit.getAttackNum());
				Unit attacked = board.activeUnits.get(i);  //create attacked unit
				Unit attacker = gameState.tempUnit;     //create attacker
				if (attacked instanceof Avatar) {attacked = (Avatar) attacked;}
				else {attacked = (Minion) attacked;}
				if (attacker instanceof Avatar) {attacker = (Avatar) attacker;}
				else {attacker = (Minion) attacker;}


				attackAnimation(out, attacker);
				int attackValue;
				if (attacked.getHealth() <= attacker.getAttack()) {
					attackDied (out,attacked,attacked.getPosition().getTilex(),attacked.getPosition().getTiley(),gameState);
				} else {

					attackValue = attacker.getAttack();
					changeHealthAfterAttack(out,attackValue,attacked);
					//the attacked unit starts to counterattack
					attackAnimation(out, attacked);
					if (attacker.getHealth() <= attackValue) {
						attackDied (out,attacker,attacker.getPosition().getTilex(),attacker.getPosition().getTiley(),gameState);
					} else {
						attackValue = attacked.getAttack();
						changeHealthAfterAttack(out,attackValue,attacker);
					}
				}
			}
		}
	}

	/**
	 * when the unit is attacked and counterattacked, its health will be changed
	 * @param out
	 * @param attackValue
	 * @param unit
	 */
	public void changeHealthAfterAttack(ActorRef out,int attackValue,Unit unit){
		unit.setHealth(unit.getHealth() - attackValue);
		int attackedHealth = unit.getHealth();
		BasicCommands.setUnitHealth(out, unit, attackedHealth);
		if (unit instanceof Avatar) {
			if (unit.getId() == 101) {
				ai.setHealth(unit.getHealth());
				BasicCommands.setPlayer2Health(out, ai);
			} else {
				human.setHealth(unit.getHealth());
				BasicCommands.setPlayer1Health(out, human);
			}
		}
	}


	/**
	 * when unit is attacked or counterattacked, its health is less than or equal to 0, this unit dies
	 * @param out
	 * @param unit
	 * @param x
	 * @param y
	 * @param gameState
	 */
	public void attackDied (ActorRef out,Unit unit,int x,int y,GameState gameState){
		board.activeUnits.remove(unit);
		//修改availableTiles
		gameState.getHumanModel().availableTiles.add(board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()));
		//在前端删掉相关的卡牌
		BasicCommands.deleteUnit(out, unit);
		BasicCommands.addPlayer1Notification(out, "the" + unit.getId() + "has been killed", 2);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		board.getTile(x,y).setOwnership(-1);
		if (unit instanceof Avatar) {
			//游戏结束
		}
	}

	/**
	 * when the attacker's ID is 5, 15,25,35. Special animation appears
	 * @param out
	 * @param unit
	 */
	public void attackAnimation(ActorRef out,Unit unit){
		EffectAnimation effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
		if((unit.getId() == 5) ||(unit.getId() == 15) ||(unit.getId() == 25) ||(unit.getId() == 35)){
			BasicCommands.playEffectAnimation(out,effectAnimation,board.getTile(unit.getPosition().getTilex(),
					unit.getPosition().getTiley()));
			try {Thread.sleep(800);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.playUnitAnimation(out,unit,UnitAnimationType.idle);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}else{
			BasicCommands.playUnitAnimation(out,unit,UnitAnimationType.attack);
			try {Thread.sleep(800);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.playUnitAnimation(out,unit,UnitAnimationType.idle);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

}




