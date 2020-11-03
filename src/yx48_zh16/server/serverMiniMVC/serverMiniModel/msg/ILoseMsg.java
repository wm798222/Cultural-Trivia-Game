package yx48_zh16.server.serverMiniMVC.serverMiniModel.msg;

import common.msg.IRoomMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Message the server send to the player who lost the game
 *
 */
public interface ILoseMsg extends IRoomMsg{
	/**
	 * Method for getting the DataPacketID corresponding to this message.
	 * @return The IDataPacketID
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ILoseMsg.class);
	}
	
	
	@Override
	public default IDataPacketID getID() {
		// TODO Auto-generated method stub
		return ILoseMsg.GetID();
	}
	
	/**
	 * Getter method for the ID of this type of message.
	 * @return The ID for this type of message
	 */
	public IDataPacketID getMessageID();
	
	
	/**
	 * Method to make the ILoseMsg
	 * @return ILoseMsg
	 */
	static ILoseMsg make() {
		return new ILoseMsg() {
			/**
			 * serial ID
			 */
			private static final long serialVersionUID = 5663469915414984119L;

			@Override
			public IDataPacketID getMessageID() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
}
