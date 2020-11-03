package yx48_zh16.realgame.controller;

import java.awt.EventQueue;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JComponent;

import common.identity.IIdentity;
import common.msg.IRoomMsg;
import common.packet.RoomDataPacket;
import common.receivers.IRoomMember;
import common.virtualNetwork.IVirtualNetwork;
import provided.jxMaps.utils.IJxMapsComponentsFactory;
import provided.jxMaps.utils.IPlace;
import yx48_zh16.client.clientMiniMVC.clientMiniModel.ClientMiniModel;
import yx48_zh16.realgame.model.GameAdapter;
import yx48_zh16.realgame.model.IGModel2GViewAdapter;
import yx48_zh16.realgame.model.IGameAdapter;
import yx48_zh16.realgame.model.RealGameModel;
import yx48_zh16.realgame.view.RealGameView;
import yx48_zh16.server.serverMVC.serverModel.VirtualNetworkPlayers;
import yx48_zh16.realgame.view.IGView2GModelAdapter;



/**
 * The controller of the game.
 *
 */
public class RealGameController {
	

	/**
	 *  The nodes for all players in a network graph
	 */
	@SuppressWarnings("rawtypes")
	private IVirtualNetwork allPlayers;
	
	/**
	 * serverStub
	 */
	private IRoomMember serverStub;
	
	/**
	 * The view of the app
	 */
	RealGameView<IPlace> view;
	
	/**
	 * The model of the app
	 */
	RealGameModel model;
	
	/**
	 * The client mini/chatroom model
	 */
	ClientMiniModel clientMiniModel;
	
	/**
	 * The game adapter to provide game service to client
	 */
	private IGameAdapter _gameUpdateAdapter;
	
	/**
	 * UUID for this game
	 */
	private UUID uuid;
	
	/**
	 * Constructs an instance of the app using the given Google Maps API key string.
	 * @param googleApiKey  The Google Maps API key string
	 * @param myTeam The nodes for all players in your team
	 * @param allPlayers The nodes for all players in a network graph
	 * @param serverStub the stub of the server
	 * @param uuid UUID of this game
	 */
	@SuppressWarnings("rawtypes")
	public RealGameController(String googleApiKey, IVirtualNetwork myTeam, IVirtualNetwork allPlayers, IRoomMember serverStub, UUID uuid) {
		
		this.setAllPlayers(allPlayers);
		this.setServerStub(serverStub);
		this.setUuid(uuid);
		this._gameUpdateAdapter = new GameAdapter(this.model);

		view = new RealGameView<IPlace>(googleApiKey, "COMP 310 JxDemo", new IGView2GModelAdapter<IPlace>() {

			@Override
			public void addMarkerAt() {
				model.addMarkerAtCenter();
			}

			@Override
			public void addMoveTo(IPlace place) {
				model.moveMapTo(place);
			}

			@Override
			public void geocodeLocation(String location) {
				model.geocodeRequest(location);
			}

			@Override
			public void makePolygon() {
				model.makePolygon();
			}
			
			@Override
			public void makePolyline() {
				model.makePolyline();
			}
			@Override
			public void clearPolygonPoints() {
				model.clearRecentLatLngs();
			}

			@Override
			public void searchNearby(String placeType, String radius) {
				model.searchNearbyPlaces(placeType, Double.valueOf(radius));
				
			}

			@Override
			public void submitAns(String text) {
				model.submitAns(text);
				
			}

			@Override
			public void dropMarker(String text, String text2) {
				model.dropMarker(text,text2);
				
			}

			@Override
			public void StartGame() {
				model.startGame();
				
			}

			@Override
			public void submitAnswer(String string) {
				model.submitAnswer(string);
				
			}

			@Override
			public void addPoints(int i) {
				model.addPoints(i);
				
			}

			@Override
			public void dropQuestionMarker(IPlace place1) {
				model.dropQuestionMarker(place1);
				
			}

			@Override
			public void submitScore() {
				model.submitScore();
				
			}
			
		});
		
		model = new RealGameModel(new IGModel2GViewAdapter() {

			@Override
			public IJxMapsComponentsFactory getMapComponentsFactory() {
				return view.getMapComponentsFactory();
			}

			@Override
			public void addPlace(IPlace place) {
//				view.addPlace(place);
			}

			@Override
			public void displayStatus(String str) {
				view.displayStatus(str);
			}

			@Override
			public void refresh() {
				view.refresh();
			}

			@Override
			public void displayScore(String str) {
				view.displayScore(str);
				
			}
		}, serverStub, myTeam, uuid);

	}
	
	
	/**
	 * Start the app
	 */
	public void start() {
		view.start();
		model.start();
	}
	
	/**
	 * start game model
	 */
	public void startModel() {
		model.start();
	}
	
	/**
	 * get view's JComponent
	 * @return JCompnin
	 */
	public JComponent getView() {
		return this.view;
	}
	
	
	/**
	 * The game adapter to provide game service to client
	 * @return the game adapter
	 */
	public IGameAdapter getGameAdapter() {
		return this._gameUpdateAdapter;
	}
	
	
	/**
	 * Launch the application where the first parameter on the 
	 * command line should be the Google Maps API key.
	 * @param args args[0] = Google Maps API key string
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if(0<args.length) {
//						String googleApiKey = args[0]; 
						String MAPS_API_KEY = "";
						new RealGameController(MAPS_API_KEY, new VirtualNetworkPlayers(new ArrayList<IRoomMember>(), new IIdentity() {

							/**
							 * serial ID
							 */
							private static final long serialVersionUID = -521126808744101783L;

							@Override
							public String getName() {
								// TODO Auto-generated method stub
								return null;
							}

							@Override
							public UUID getID() {
								// TODO Auto-generated method stub
								return null;
							}
							
						}), new VirtualNetworkPlayers(new ArrayList<IRoomMember>(), new IIdentity() {

							/**
							 * serial ID
							 */
							private static final long serialVersionUID = -5455397224782620249L;

							@Override
							public String getName() {
								// TODO Auto-generated method stub
								return null;
							}

							@Override
							public UUID getID() {
								// TODO Auto-generated method stub
								return null;
							}
							
						}), new IRoomMember() {

							@Override
							public void receiveMsg(RoomDataPacket<? extends IRoomMsg> msg) throws RemoteException {
								// TODO Auto-generated method stub
								
							}

							@Override
							public String getName() throws RemoteException {
								// TODO Auto-generated method stub
								return null;
							}

							
							
							
						}, UUID.randomUUID()).start();
					}
					else {
						throw new IllegalArgumentException("No Google Maps API key string supplied on the command line!");
					}
				} catch (Exception e) {
					System.err.println("[JxMapsDemoController.main()] Exception while starting the system: "+e);
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * getter for serverStub
	 * @return IRoomMember
	 */
	public IRoomMember getServerStub() {
		return serverStub;
	}

	/**
	 * setter for serverStub
	 * @param serverStub stub for server
	 */
	public void setServerStub(IRoomMember serverStub) {
		this.serverStub = serverStub;
	}


	/**
	 * get all players
	 * @return IVirtualNetwork
	 */
	@SuppressWarnings("rawtypes")
	public IVirtualNetwork getAllPlayers() {
		return allPlayers;
	}


	/**
	 * setter for all players
	 * @param allPlayers nodes for all players
	 */
	@SuppressWarnings("rawtypes")
	public void setAllPlayers(IVirtualNetwork allPlayers) {
		this.allPlayers = allPlayers;
	}


	/**
	 * getter for uuid
	 * @return UUID UUID
	 */
	public UUID getUuid() {
		return uuid;
	}


	/**
	 * setter for uuid
	 * @param uuid UUID
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
