package yx48_zh16.client.clientMVC.clientView;

import java.util.ArrayList;

import common.receivers.IApplication;
import yx48_zh16.client.clientMVC.clientModel.Room;
import yx48_zh16.client.clientMVC.clientModel.TeamIdentityWrapper;
import yx48_zh16.client.clientMVC.clientModel.TeamWrapper;
import yx48_zh16.client.clientMVC.clientModel.UserWrapper;

/**
 * View to model adapter
 *
 */
public interface IView2ModelAdapter {
	
	/**
	 * Quit chatapp
	 */
	public void quit();

	/**
	 * Create a chatroom 
	 * @param roomName - name of chatroom
	 */
	public void createChatRoom(String roomName);

	/**
	 * Join a room
	 * @param itemAt - a room to join
	 */
	public void joinRoom(TeamIdentityWrapper itemAt);
	
	/**
	 * Login to chat app
	 * @param string - user name
	 */
	public void login(String string);

	/**
	 * Invite an user to a room
	 * @param user - an user to invite
	 * @param chatRoom - a room to invite the user
	 */
	public void inviteToRoom(IApplication user, Room chatRoom);

	/**
	 * Get the chat rooms from an user
	 * @param iConnection - an user to get chatrooms from
	 */
	public void getChatrooms(IApplication iConnection);

	/**
	 * Connect to an user
	 * @param iConnection - an user to connect to
	 */
	public void connectToUser(IApplication iConnection);

	/**
	 * Get an user's other connections
	 * @param user - an user to get connections from
	 */
	public void getConnections(IApplication user);
	
	/**
	 * Get the list of connected users
	 * @return - a list of UserWrapper
	 */
	public ArrayList<UserWrapper> getConnectedWrappedUsers();

	/**
	 * Get a list of connected chat rooms
	 * @return - a list of ChatRoomMemberWrapper
	 */
	public ArrayList<TeamWrapper> getConnectedChatRooms();

	/**
	 * make marker for testing
	 * @param text laglng
	 */
	public void makeMarker(String text);

}
