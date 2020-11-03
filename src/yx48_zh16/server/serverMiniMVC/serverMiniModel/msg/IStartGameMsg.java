package yx48_zh16.server.serverMiniMVC.serverMiniModel.msg;

import java.util.UUID;
import java.util.function.Supplier;

import common.msg.IRoomMsg;
import common.receivers.IRoomMember;
import common.virtualNetwork.IVirtualNetwork;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;
import provided.mixedData.MixedDataKey;
import yx48_zh16.realgame.controller.RealGameController;

/**
 * The message sent to start the game on the player's side.
 *
 */
public interface IStartGameMsg extends IRoomMsg{
	
	
	/**
	 * Method for getting the DataPacketID corresonding to this message.
	 * @return The IDataPacketID
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IStartGameMsg.class);
	}
	
	
	@Override
	public default IDataPacketID getID() {
		// TODO Auto-generated method stub
		return IStartGameMsg.GetID();
	}
	
	/**
	 * Getter method for the ID of this type of message.
	 * @return The ID for this type of message
	 */
	public IDataPacketID getMessageID();
	
	/**
	 * Method to get game controller
	 * @return a game controller
	 */
	public Supplier<RealGameController> getGameController();
	
	
	/**
	 * getter for your group
	 * @return your groups's network
	 */
	@SuppressWarnings("rawtypes")
	public IVirtualNetwork getYourGroup();
	
	
	/**
	 * getter for all people
	 * @return all people's network
	 */
	@SuppressWarnings("rawtypes")
	public IVirtualNetwork getAllPeople();
	
	
	/**  getter for database key
	 * @return database key
	 */
	@SuppressWarnings("rawtypes")
	public MixedDataKey getKey();
	
	
	/**
	 * getter for player UUID
	 * @return UUID
	 */
	public UUID getPlayerUUID();
	
	/**
	 * getter for serverStub
	 * @return IRoomMember that represents the server
	 */
	public IRoomMember getServerStub();
	
	/**
	 * @param yourGroup network for your group
	 * @param allPeople network for all people
	 * @param user the user of the game
	 * @param adapterKey key to access game adapter
	 * @param uuid UUID
	 * @param serverStub the server's stub
	 * @return an IStartGameMsg
	 */ 
	@SuppressWarnings("rawtypes")
	static IStartGameMsg make(final IVirtualNetwork yourGroup,final IVirtualNetwork allPeople, final IRoomMember user, final MixedDataKey adapterKey, final UUID uuid, final IRoomMember serverStub) {
		return new IStartGameMsg() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2536743593445925504L;

			@Override
			public IDataPacketID getMessageID() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Supplier<RealGameController> getGameController() {
				// TODO Auto-generated method stub
				return new Supplier<RealGameController>() {

					@Override
					public RealGameController get() {
						// TODO Auto-generated method stub
						String MAPS_API_KEY= ""; 
						return new RealGameController(MAPS_API_KEY, yourGroup, allPeople, serverStub, uuid);
					}
				};
			}

			@Override
			public IVirtualNetwork getYourGroup() {
				// TODO Auto-generated method stub
				return yourGroup;
			}

			@Override
			public IVirtualNetwork getAllPeople() {
				// TODO Auto-generated method stub
				return allPeople;
			}

			@Override
			public MixedDataKey getKey() {
				// TODO Auto-generated method stub
				return adapterKey;
			}

			@Override
			public IRoomMember getServerStub() {
				// TODO Auto-generated method stub
				return serverStub;
			}

			@Override
			public UUID getPlayerUUID() {
				// TODO Auto-generated method stub
				return uuid;
			}
			
		};
	}


}
