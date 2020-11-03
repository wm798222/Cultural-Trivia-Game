package common.msg.status.network;

import java.util.UUID;

import common.msg.IRoomMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * IServerCheckStatusMsg is special message sent to the server to check its status. Since a client doesn't know 
 * a priori who the server is, this message should be sent to everyone.
 *
 */
public interface IServerCheckStatusMsg extends IRoomMsg {
	/**
	 * Retrieve the ID value directly from the interface.
	 * getID() merely delegates to this method.
	 * @return The host ID value associated with this data type.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IServerCheckStatusMsg.class);
	}
	
	@Override
	public default IDataPacketID getID() {
		return GetID();
	}	
	
	/**
	 * 
	 * @return - Unique ID identifying the message. This unique ID should be generated when this message is created. 
	 */
	public UUID getUUID();

	/**
	 * Factory method to create the data type from an input value from an existing UUID.
	 * @return An IServerCheckStatusMsg object
	 */
	public static IServerCheckStatusMsg make() {
		return new IServerCheckStatusMsg() {
			private static final long serialVersionUID = -5699132495859428978L;
			private UUID uuid = UUID.randomUUID();
			@Override
			public UUID getUUID() {
				return uuid;
			}
			
		};
	}
}
