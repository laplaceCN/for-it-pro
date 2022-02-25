package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

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

}
