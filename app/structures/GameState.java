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
	
}
