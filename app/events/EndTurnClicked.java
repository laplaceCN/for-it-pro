package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		BasicCommands.addPlayer1Notification(out, "EndTurnClicked!", 2);
		gameState.getHumanModel().drawOneCard(out);
		gameState.clearHumanMana(out);

		gameState.recoverCardState();
		gameState.perTurnSkill();


		//清楚暂存器状态
		Player hModel = gameState.getHuman();
		if(gameState.cardClickedAndWaiting) {
			gameState.cardClickedAndWaiting = false;
			int previousIndex = gameState.tempCardIndex;
			//let the buffer rest
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.getHumanModel().showAvailables(out, previousIndex, 0);
			//let the buffer rest
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.drawCard(out, hModel.getCard(previousIndex-1), previousIndex, 0);
			//gameState.tempCardIndex = -1;
		}
		gameState.clearHumanMana(out);
		gameState.clearAiMana(out);
//		gameState.aiMethod();

		gameState.getBoardModel().setHumanMana(out, gameState);
		gameState.getBoardModel().setAiMana(out, gameState);

	}
	/**
	 * //		原文：
	 * //		57.The program continues to extract card objects sequentially from the Card
	 * //		array and pass them to the front end using drawCard() method.
	 * //		58.The program calls Recover() method to reset the member variable values of
	 * //		movement and attack times of all units. The purpose of this action is to remove the
	 * //		restriction that it cannot attack or move in preparation for the next turn.
	 * //		59.After clicking the button, the program passes the player's health and mana
	 * //		information to the front end using setPlayerHealth() method and setPlayerMana()
	 * //		method.
	 * //		60.The program destroys the contents of all temporary containers, including Spell
	 * //		card and Unit card.
	 * //		61.The program use drawCard() method to change the hand mode value to 0.
	 * //		62.The program calls the AI() method.
	 *
	 *			57 读取卡组卡片并放入手牌
	 *			读取卡组方法：
	 *			实现一：读取下一个卡牌信息，并且获取对象 readCard(this.turn/第几回合/)放入手牌容器
	 *			额外的工作，需要一个计数器记录目前的回合数
	 *			实现二：读取下一个卡牌信息，并且获取对象，放入手牌容器 readCard(因为还未使用的卡牌集合是独立存在的，所以不需要传入是第几回合)
	 *			额外的工作：需要多个集合，并在获取对象后，在后台转移对象，movelistCard()：从未被使用集合转移到已被使用集合
	 *
	 *			共同实现：使用drawCard(传入参数)(手牌的呈现)（已经有现成的方法）
	 *
	 *			58 Recover()方法重置所有的卡的攻击次数与移动次数，为了达到这个目标：
	 *			实现一：在之前的流程中计算移动次数与攻击次数时，不对卡片对象的成员变量进行修改以便保留原始信息。
	 *			在此基础上创建两个的map集合：key：卡片id value：次数 一个复制移动次数 一个复制攻击次数（在卡牌对象被放置在桌面时）
	 *			在这个阶段这两个集合：
	 *			遍历集合：
	 *				拿到map的key，
	 *				获取卡片对象，（又一层遍历查询）
	 *				拿到卡片的成员变量并赋值到这个key对应的value
	 *			实现二：比较暴力，我们在之前的流程中直接使用成员变量修改攻击与移动次数信息。
	 *			在这个阶段遍历场上所有tile看是否有被部署的unit
	 *			如果存在，根据这个unit对应的卡片信息（是否是特殊卡片），直接对成员变量进行赋值，
	 *			（反正特殊情况少，分支也不麻烦，只是拓展性差点）
	 *
	 *			59 使用setPlayerHealth()？？？和 setPlayerMana()设置前端玩家的数值
	 *			这里主要是确定值到底是多少：
	 *			实现：根据回合计数器计算出玩家的mana值，玩家的健康值我认为最好在之前的流程实时反馈
	 *			额外的工作，需要一个计数器记录目前的回合数
	 *
	 *			60 （如果之后的设计还有临时容器）
	 *			将临时容器或者说老师给的gameState的状态进行修改，重置为初始状态。
	 *
	 *			61 修改手牌的大小：
	 *			实现：
	 *			遍历手牌集合：
	 *				调用	drawCard()方法修改mod值为0
	 *
	 *			62 调用ai方法，这个比较复杂，之后详谈。
	 *
	 *
	 *
	 *
	 */

}
