package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay 
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A 
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game 
 * Actor, which you can use to trigger game logic.
 * 
 * { 
 *   String messageType = “heartbeat”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 * The main purpose of this class is to reduce the possiblity of buffer overloaded
 * The main action of this class is to wait the action in web when the game is going
 *
 * @author Ether group
 *
 */
public class Heartbeat implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		gameState.heartbeatNum++;
		if(gameState.heartbeatNum == 8) {
			gameState.heartbeatNum = 0;
			try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
		}

		
	}

}
