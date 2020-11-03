package yx48_zh16.server.serverMVC.serverController;

import java.awt.EventQueue;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import common.receivers.IApplication;
import common.receivers.IRoomMember;
import provided.discovery.IEndPointData;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import provided.mixedData.MixedDataKey;
import yx48_zh16.server.serverMVC.serverModel.ServerModel;
import yx48_zh16.server.serverMVC.serverModel.TeamIdentityWrapper;
import yx48_zh16.server.serverMVC.serverView.ServerView;
import yx48_zh16.server.serverMVC.serverView.IView2ModelAdapter;
import yx48_zh16.server.serverMiniMVC.IMainAccessMiniAdapter;
import yx48_zh16.server.serverMiniMVC.serverMiniController.ServerMiniController;
import yx48_zh16.server.serverMiniMVC.serverMiniView.ServerMiniView;
import yx48_zh16.server.serverMVC.serverModel.IModel2ViewAdapter;
import yx48_zh16.server.serverMVC.serverModel.IUserUpdateAdapter;
import yx48_zh16.server.serverMVC.serverModel.Room;
import yx48_zh16.server.serverMVC.serverModel.TeamWrapper;
import yx48_zh16.server.serverMVC.serverModel.UserWrapper;

/**
 * Controller of game application for the server.
 *
 */
public class ServerController {
	/**
	 * The model in the MVC pattern.
	 */
	private ServerModel model;

	/**
	 * The view in the MVC pattern.
	 */
	@SuppressWarnings("rawtypes")
	private ServerView view;
	
	/**
	 * Constructs a new {@code Controller} object.
	 */
	public ServerController() {

		model = new ServerModel(new IModel2ViewAdapter() {
		
			@Override
			public IMainAccessMiniAdapter addRoom(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IApplication serverStub) {
				
				ServerMiniController mc = new ServerMiniController(roomID, roomName, userName, currentMembers, serverStub);
				ServerMiniView miniView = mc.getMiniView();
				
				mc.setMiniAccessMainAdapter(new IMiniAccessMainAdapter() {
					@Override
					public void leave() {
						view.removeRoom(mc.getMiniView());  
						model.delete(mc.getMiniAdapter());
						
						model.deleteFromChatRoom(mc.getMiniAdapter().getChatRoom());
//						view.updateExistingRooms();
					}

//					@Override
//					public HashMap<Integer, Integer> getValueFromDB(MixedDataKey key) {
//						return model.getValueFromDB(key);
//						
//					}

					@Override
					public <T> void putInServerDB(MixedDataKey<T> key, T value) {
						// TODO Auto-generated method stub
						model.putInDB(key, value);
					}

					@Override
					public <T> T getFromServerDB(MixedDataKey<T> key) {
						// TODO Auto-generated method stub
						return model.getFromDB(key);
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

		
		view = new ServerView<IEndPointData>(new IView2ModelAdapter() {
			
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
				model.connectToServer(user);
			}

			@Override
			public void getConnections(IApplication user) {
//				model.getUsersFromUser(user);
			}

			@Override
			public ArrayList<UserWrapper> getConnectedWrappedUsers() {
//				ArrayList<IServer> users = model.getConnectedUsers();
//				ArrayList<UserWrapper> wrappedUsers = new ArrayList<UserWrapper>();
//				for (IApplication user : users) {
//					wrappedUsers.add(new UserWrapper(user, model.getUserUpdateAdapter()));
//				}
//				return wrappedUsers;
				return null;
			}

			@Override
			public ArrayList<TeamWrapper> getConnectedChatRooms() {
				ArrayList<Room> chatrooms = model.getConnectedChatRooms();
				ArrayList<TeamWrapper> wrappedRooms = new ArrayList<TeamWrapper>();
				for (Room room : chatrooms) {
					wrappedRooms.add(new TeamWrapper(room, model.getServer()));
				}
				return wrappedRooms;
			}

			@Override
			public void sendGame() {
				// TODO Auto-generated method stub
				model.sendGameMsg();
			}

			@Override
			public void startGame() {
				// TODO Auto-generated method stub
				model.startGame();
			}

			@Override
			public void endGame() {
				model.endGame();
				
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
				(new ServerController()).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}

