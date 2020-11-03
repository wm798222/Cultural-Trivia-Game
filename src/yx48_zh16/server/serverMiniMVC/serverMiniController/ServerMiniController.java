package yx48_zh16.server.serverMiniMVC.serverMiniController;

import java.util.Collection;
import java.util.UUID;

import javax.swing.JComponent;

import common.msg.IRoomMsg;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import provided.mixedData.MixedDataKey;
import yx48_zh16.server.serverMVC.serverController.IMiniAccessMainAdapter;
import yx48_zh16.server.serverMVC.serverModel.Player;
import yx48_zh16.server.serverMVC.serverModel.Room;
import yx48_zh16.server.serverMiniMVC.IMainAccessMiniAdapter;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.ServerMiniModel;
import yx48_zh16.server.serverMiniMVC.serverMiniView.ServerMiniView;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.IMiniModel2ViewAdapter;
import yx48_zh16.server.serverMiniMVC.serverMiniView.IServerMiniViewUpdateAdapter;

/**
 * The definition of the server mini controller.s
 *
 */
public class ServerMiniController {
	
	/**
	 * The mini model
	 */
	private ServerMiniModel model;
	
	/**
	 * The mini view
	 */
	private ServerMiniView view;
	
	/**
	 * The local stored adapter for the main MVC to access mini MVC
	 */
	private IMainAccessMiniAdapter _mainAccessMiniAdapter;
	
	/**
	 * The adapter for the mini MVC to access main MVC
	 */
	private IMiniAccessMainAdapter _miniAccessMainAdapter;
	
	/**
	 * The setter for miniAccessMainAdapter
	 * @param adapter The new miniAccessMainAdapter
	 */
	public void setMiniAccessMainAdapter(IMiniAccessMainAdapter adapter) {
		this._miniAccessMainAdapter = adapter;

	}
	
	/**
	 * The constructor for the mini controller
	 * @param <T> T Type
	 * @param roomID the room ID
	 * @param roomName the room name
	 * @param userName the user who's view this chatApp
	 * @param currentMembers all current members in the this chat room
	 * @param serverStub the app level server stub
	 */
	public <T> ServerMiniController(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IApplication serverStub) {
		
		model = new ServerMiniModel(roomID, roomName, userName, currentMembers, new IMiniModel2ViewAdapter<T>() {

			@Override
			public void displayMessage(String message) {
				// TODO Auto-generated method stub
				view.displayMessage(message);
			}

			@Override
			public void displayComponent(JComponent component) {
				// TODO Auto-generated method stub
				System.out.println("Displaying component in model2view adapter");

				view.displayComponent(component);
			}

			@SuppressWarnings("hiding")
			@Override
			public <T> void putInServerDB(MixedDataKey<T> key, T value) {
				// TODO Auto-generated method stub
				_miniAccessMainAdapter.putInServerDB(key, value);
			}

			@SuppressWarnings("hiding")
			@Override
			public <T> T getFromServerDB(MixedDataKey<T> key) {
				// TODO Auto-generated method stub
				return _miniAccessMainAdapter.getFromServerDB(key);
			}

//			@Override
//			public void createRoomForTeamAndGetRoom() {
//				// TODO Auto-generated method stub
//				_miniAccessMainAdapter.createRoomAndGetRoom();
//			}
			
		}, serverStub);
		
		view = new ServerMiniView(new IServerMiniViewUpdateAdapter<IRoomMsg>() {

			@Override
			public void leave() {
				model.leave();
				_miniAccessMainAdapter.leave();
			}

			@Override
			public void sendMessageClicked(IRoomMsg message) {
				// TODO Auto-generated method stub
				model.sendMessage(message);
			}

			@Override
			public void sendTextMsg(String text) {
				// TODO Auto-generated method stub
				model.sendTextMsg(text);
			}


			@Override
			public void sendMapMsg() {
				model.sendMapMsg();
			}

			@Override
			public void sendGameMsg() {
				// TODO Auto-generated method stub
//				model.sendGameMsg();
			}

//			@Override
//			public void endGame() {
//				model.endGame(_miniAccessMainAdapter);
//				
//			}



		});
		
		
		this._mainAccessMiniAdapter = new IMainAccessMiniAdapter() {
		
			@Override
			public Room getChatRoom() {
				// TODO Auto-generated method stub
				return model.getTeam();
			}
		
			@Override
			public Player getChatUser() {
				// TODO Auto-generated method stub
				return model.getUser();
			}

			@Override
			public void joinRoom() {
				model.joinRoom();
			}

		};

	}
	
	/**
	 * Getter method for the mainAccessMiniAdapter
	 * @return the adapter for the main to access mini
	 */
	public IMainAccessMiniAdapter getMiniAdapter() {
		return this._mainAccessMiniAdapter;
	}
	
	/**
	 * Getter method for the mini view
	 * @return the mini view of this chat room
	 */
	public ServerMiniView getMiniView() {
		return this.view;
	}
	
	/**
	 * Start the mini MVC representing a single chat room
	 */
	public void start() {
		view.start();
		model.start();
	}

}
