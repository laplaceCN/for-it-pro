package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 * The main purpose of this class is to handle changes in the game state when the player clicks on a tile
 * There are three main types of responses in this class:
 * the player wants to display range of attack or movement,
 * the player wants to attack or move, and the player wants to summon
 *
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();



		if (gameState.cardClickedAndWaiting) {
			gameState.cardClickedAndWaiting = false;
			//stop showing available tiles
			gameState.getHumanModel().showAvailables(out, gameState.tempCardIndex, 0);
			//use the card
			boolean flag = gameState.getHumanModel().useSelectedCard(out, gameState.tempCardIndex, tilex, tiley);
			//The return value represents whether the card was successfully placed
			gameState.getHumanModel().highlightControl(out,0);

			//remove card from hand
			if(flag) {gameState.getHumanModel().deleteHandCard(out, gameState.tempCardIndex);}
		}

		else if(gameState.tileClickedAndWaiting){
			gameState.getBoardModel().offAvailables(out, 0);
			
			//Reset the game temp state
			gameState.tileClickedAndWaiting = false;
			gameState.getHumanModel().highlightControl(out, 0);

			//Determine if the player wants to move with this click
			boolean move = gameState.getBoardModel().checkMoveUnit(tilex,tiley);
			//Determine if the player wants to attack with this click
			boolean attack = gameState.getBoardModel().checkAttackUnit(tilex,tiley);
			if(move){
				//Determine if the move conforms to the rules
				move = gameState.getBoardModel().checkMoveLocation(gameState,out,tilex,tiley);
				if(move){
					//move
					gameState.getBoardModel().move(gameState,out,tilex,tiley);

				}
			}else if(attack) {
				//检查攻击的位置是否合法
				attack = gameState.getBoardModel().checkAttackLocation(gameState,tilex,tiley);

				if(attack){
					//Check that the attack is in compliance with the rules
			gameState.getBoardModel().Attack(out,tilex,tiley,gameState);}}
			else {
				BasicCommands.addPlayer1Notification(out,"please click the your card again",2);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}



		}
		else if(!gameState.tileClickedAndWaiting && !gameState.cardClickedAndWaiting){
			for (int i = 0; i < gameState.getBoard().activeUnits.size(); i++) {
				//If the click location exists unit
				if((gameState.getBoard().activeUnits.get(i).getPosition().getTilex() == tilex)&&
						(gameState.getBoard().activeUnits.get(i).getPosition().getTiley() == tiley)){
					gameState.tempUnit = gameState.getBoard().activeUnits.get(i);
					System.out.println(gameState.tempUnit.getAttack()+"hello");
					System.out.println(gameState.tempUnit.getHealth()+"hello");


					gameState.tileClickedAndWaiting = true;

					//Displays range of attack or movement
					if(gameState.tempUnit.getId() < 20 || gameState.tempUnit.getId() ==100) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						gameState.getBoardModel().speshowAvailables(gameState,out);

						gameState.getBoardModel().showAvailables(out, tilex, tiley, 1);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						BasicCommands.addPlayer1Notification(out, "you are clicking the unit", 2);
					}

				}
			}

		}


		}
		
	}


