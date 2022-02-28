package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

import java.util.ArrayList;

public class BoardModel {

	Board board;
	Player human;
	Player ai;
	
	public BoardModel(Player human, Player ai, Board board) {
		this.board = board;
		this.human = human;
		this.ai = ai;
	}
	
	public void makeTiles(ActorRef out) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j ++) {
				BasicCommands.drawTile(out, board.getTile(i, j), 0);
			}
		}
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}
	//传入tile的x与y坐标显示,没有考虑特殊情况
	public void showAvailables(ActorRef out, int x, int y, int mode) {
		//显示移动与攻击范围

		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j++) {
				if((Math.abs((board.getTile(i,j).tilex - x))+Math.abs((board.getTile(i,j).tiley - y))<3)){
					Tile tile = board.getTile(i,j);
					BasicCommands.drawTile(out,tile,mode);
					try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
				}

			}
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
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
	
	public void setHumanMana(ActorRef out, GameState gameState) {
		human.setMana(gameState.numRound + 2);
		BasicCommands.setPlayer1Mana(out, human);
	}

}
