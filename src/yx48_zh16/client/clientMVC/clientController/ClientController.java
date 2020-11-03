package yx48_zh16.client.clientMVC.clientController;

import java.awt.EventQueue;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import provided.discovery.IEndPointData;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import yx48_zh16.client.clientMVC.clientModel.ClientModel;
import yx48_zh16.client.clientMVC.clientView.ClientView;
import yx48_zh16.client.clientMVC.clientView.IView2ModelAdapter;
import yx48_zh16.client.clientMiniMVC.IMainAccessMiniAdapter;
import yx48_zh16.client.clientMiniMVC.clientMiniController.ClientMiniController;
import yx48_zh16.client.clientMiniMVC.clientMiniView.ClientMiniView;
import yx48_zh16.client.clientMVC.clientModel.IModel2ViewAdapter;
import yx48_zh16.client.clientMVC.clientModel.IUserUpdateAdapter;
import yx48_zh16.client.clientMVC.clientModel.Room;
import yx48_zh16.client.clientMVC.clientModel.TeamIdentityWrapper;
import yx48_zh16.client.clientMVC.clientModel.TeamWrapper;
import yx48_zh16.client.clientMVC.clientModel.UserWrapper;
import common.receivers.IApplication;
import common.receivers.IRoomMember;

/**
 * Controller of game application for the client.
 *
 */
public class ClientController {
	/**
	 * The model in the MVC pattern.
	 */
	private ClientModel model;

	/**
	 * The view in the MVC pattern.
	 */
	@SuppressWarnings("rawtypes")
	private ClientView view;
	
	/**
	 * Constructs a new {@code Controller} object.
	 */
	public ClientController() {

		model = new ClientModel(new IModel2ViewAdapter() {
		
			@Override
			public IMainAccessMiniAdapter addRoom(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IApplication applicationMe) {
				
				ClientMiniController mc = new ClientMiniController(roomID, roomName, userName, currentMembers, applicationMe);
				ClientMiniView miniView = mc.getMiniView();
				
				mc.setMiniAccessMainAdapter(new IMiniAccessMainAdapter() {
					@Override
					public void leave() {
						view.removeRoom(mc.getMiniView());  
						model.delete(mc.getMiniAdapter());
						
						model.deleteFromChatRoom(mc.getMiniAdapter().getChatRoom());
//						view.updateExistingRooms();
					}
					
				});
				mc.start();
				
				view.installRoom(roomName, miniView);
				return mc.getMiniAdapter();

				
			}

			@Override
			public void startDiscoveryPanel() {
				view.startDiscoveryPnl();
			}

			@Override
			public void updateConnectedUser(UserWrapper newConnection) {
				view.updateUsers(newConnection);
			}

			@Override
			public void updateAvailRooms(TeamIdentityWrapper availRoom) {
				view.updateAvailableRooms(availRoom);
			}

			@Override
			public void updateConnectedRooms(TeamWrapper TeamWrapper) {
				view.updateConnectedRooms(TeamWrapper);

			}

			@Override
			public void deleteFromConnectedChatRoom(TeamWrapper TeamWrapper) {
//				view.updateExistingRooms();
			}

			@Override
			public void deleteFromConnectedUsers(UserWrapper userWrapper) {
//				view.updateExistingUsers();
			}

			@Override
			public void updateAvailableUser(UserWrapper userWrapper) {
				// TODO Auto-generated method stub
				view.updateAvailableUser(userWrapper);
			}

		}, new IUserUpdateAdapter() {

			@Override
			public void deleteConnectedUsers(UserWrapper user) {
				// TODO Auto-generated method stub
				view.deleteFromConnectedUsers(user);
			}
			
		});

		
		view = new ClientView<IEndPointData>(new IView2ModelAdapter() {
			
			@Override
			public void quit() {
				model.quit();
			}

			@Override
			public void createChatRoom(String roomName) {
				model.createTeam(roomName);
			}

			@Override
			public void joinRoom(TeamIdentityWrapper itemAt) {
				model.joinRoom(itemAt);
			}

			@Override
			public void login(String username) {
				model.start(username);
			}

			@Override
			public void inviteToRoom(IApplication user, Room chatRoom) {
				model.inviteToRoom(user, chatRoom);
			}

			@Override
			public void getChatrooms(IApplication user) {
				// TODO Auto-generated method stub
				model.getChatRooms(user);
			}

			@Override
			public void connectToUser(IApplication user) {
				model.connectToClient(user);
			}

			@Override
			public void getConnections(IApplication user) {
//				model.getUsersFromUser(user);
			}

			@Override
			public ArrayList<UserWrapper> getConnectedWrappedUsers() {
//				ArrayList<IClient> users = model.getConnectedUsers();
//				ArrayList<UserWrapper> wrappedUsers = new ArrayList<UserWrapper>();
//				for (IClient user : users) {
//					wrappedUsers.add(new UserWrapper(user, model.getUserUpdateAdapter()));
//				}
//				return wrappedUsers;
				return null;
			}

			@Override
			public ArrayList<TeamWrapper> getConnectedChatRooms() {
//				ArrayList<Team> chatrooms = model.getConnectedChatRooms();
//				ArrayList<TeamWrapper> wrappedRooms = new ArrayList<TeamWrapper>();
//				for (Team room : chatrooms) {
//					wrappedRooms.add(new TeamWrapper(room, model.getUser()));
//				}
//				return wrappedRooms;
				return null;
			}

			@Override
			public void makeMarker(String text) {
				// TODO Auto-generated method stub
				
			}
		}, new IDiscoveryPanelAdapter<IEndPointData>() {

			@Override
			public void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<IEndPointData>> endPtsUpdateFn) {
				// TODO Auto-generated method stub
				model.connectToDiscoveryServer(category, watchOnly, endPtsUpdateFn);
				
			}

			@Override
			public void connectToEndPoint(IEndPointData selectedValue) {
				model.connectToEndPoint((IEndPointData) selectedValue);
			}
			
		});

	}

	/**
	 * Starts both the view and the model associated with the controller.
	 */
	public void start() {
		view.start();
//		model.start();
	}

	/**
	 * Starts the program by starting the controller.
	 * 
	 * @param args - the command line input
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				(new ClientController()).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}

