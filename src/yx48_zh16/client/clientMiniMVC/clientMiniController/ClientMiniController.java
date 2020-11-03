package yx48_zh16.client.clientMiniMVC.clientMiniController;

import java.util.Collection;
import java.util.UUID;

import javax.swing.JComponent;

import common.msg.IRoomMsg;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import yx48_zh16.client.clientMVC.clientController.IMiniAccessMainAdapter;
import yx48_zh16.client.clientMVC.clientModel.Player;
import yx48_zh16.client.clientMVC.clientModel.Room;
import yx48_zh16.client.clientMiniMVC.IMainAccessMiniAdapter;
import yx48_zh16.client.clientMiniMVC.clientMiniModel.ClientMiniModel;
import yx48_zh16.client.clientMiniMVC.clientMiniView.ClientMiniView;
import yx48_zh16.client.clientMiniMVC.clientMiniModel.IMiniModel2ViewAdapter;
import yx48_zh16.client.clientMiniMVC.clientMiniView.IClientMiniViewUpdateAdapter;

/**
 * The definition of the controller of client's mini MVC.
 *
 */
public class ClientMiniController {
	
	/**
	 * The mini model
	 */
	private ClientMiniModel model;
	
	/**
	 * The mini view
	 */
	private ClientMiniView view;
	
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
	 * @param roomID the room ID
	 * @param roomName the room name
	 * @param userName the user who's view this chatApp
	 * @param currentMembers all current members in the this chat room
	 * @param applicationMe appliation level stub for me
	 */
	public ClientMiniController(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IApplication applicationMe) {
		
		model = new ClientMiniModel(roomID, roomName, userName, currentMembers, new IMiniModel2ViewAdapter() {

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

			@Override
			public void leave() {
				// TODO Auto-generated method stub
				
				
				System.out.println("CLIENT ----- Leave from time out reached");
				model.leave();
				_miniAccessMainAdapter.leave();
			}
			
		}, applicationMe);
		
		view = new ClientMiniView(new IClientMiniViewUpdateAdapter<IRoomMsg>() {

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
			public void sendImageMsg(String imgName) {
				model.sendImageMsg(imgName);
			}
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
	public ClientMiniView getMiniView() {
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
