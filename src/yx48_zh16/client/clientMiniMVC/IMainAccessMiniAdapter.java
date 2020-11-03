package yx48_zh16.client.clientMiniMVC;

import yx48_zh16.client.clientMVC.clientModel.Player;
import yx48_zh16.client.clientMVC.clientModel.Room;

/**
 * The adapter for to allow the main MVC to access methods in the mini MVC.
 *
 */
public interface IMainAccessMiniAdapter {
	
	/**
	 * Abstract method of getting the chat room in a mini MVC.
	 * @return the chat room held in the mini MVC.
	 */
	public Room getChatRoom();

	/**
	 * Getter method of the user holding the chat room.
	 * @return the user holding the chat room and the mini MVC.
	 */
	public Player getChatUser();

	/**
	 * Method for telling all other memebrs that the user joined the room.
	 */
	public void joinRoom();
	
}
