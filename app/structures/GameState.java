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
	private static int idHuman = 20;
	private static int idAI = 21;

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
	// the avatars
	private Avatar aHuman;
	private Avatar aAI;
	
	public GameState() {
		
		// Create a Board
		board = new Board();
		
		// Create players and their player model
		human = new Player(isHuman);
		ai = new Player(isAI);
		
		// Create players' playerModels
		humanModel = new PlayerModel(human, board);
		aiModel = new PlayerModel(ai, board);
		
		// Create avatars, link them to players and add them to board's list
		// Unit id for avatarHuman = 20, avartarAI = 21
		aHuman = (Avatar)utils.BasicObjectBuilders.loadUnit(utils.StaticConfFiles.humanAvatar, idHuman, Avatar.class);
		aAI = (Avatar)utils.BasicObjectBuilders.loadUnit(utils.StaticConfFiles.aiAvatar, idAI, Avatar.class);
		aHuman.setPlayer(human);
		aAI.setPlayer(ai);
		board.addUnit(aHuman);
		board.addUnit(aAI);
	}
	
	public void setManaAndCountRound() {
		human.setMana(numRound+1);
		ai.setMana(numRound+1);
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
	
	public Avatar getHumanAvatar() {
		return this.aHuman;
	}
	
	public Avatar getAIAvatar() {
		return this.aAI;
	}
	
	public PlayerModel getHumanModel() {
		return humanModel;
	}
	
	public PlayerModel getAIModel() {
		return aiModel;
	}
	
}