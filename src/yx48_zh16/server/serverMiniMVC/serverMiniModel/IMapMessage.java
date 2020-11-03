package yx48_zh16.server.serverMiniMVC.serverMiniModel;


import java.util.function.Function;
import java.util.function.Supplier;

import common.msg.IRoomMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;
import provided.jxMaps.ui.JxMapsPanel;

/**
 * Image message
 *
 */
public interface IMapMessage extends IRoomMsg{
	
	/**
	 * Method for getting the DataPacketID corresonding to this message.
	 * @return The IDataPacketID
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IMapMessage.class);
	}
	
	
	@Override
	public default IDataPacketID getID() {
		// TODO Auto-generated method stub
		return IMapMessage.GetID();
	}
	
	/**
	 * Getter method for the ID of this type of message.
	 * @return The ID for this type of message
	 */
	public IDataPacketID getMessageID();
	
	/**
	 * Factory for getting the map.
	 * @return A supplier that gives a map
	 */
	public Supplier<Function<String, JxMapsPanel>> getMapSupplier();
	
	/**
	 * Get the title corresponding to this message/
	 * @return The string title
	 */
	public String getTitle();
	
	/**
	 * The factory method that makes a message of this interface type.
	 * @param messageID The message ID
	 * @return A IMapMessage object
	 */
	static IMapMessage make(final IDataPacketID messageID) {
		
		
		return new IMapMessage() {
			/**
			 * The serial version UID
			 */
			private static final long serialVersionUID = -7562489340138835550L;

			@Override
			public IDataPacketID getMessageID() {
				// TODO Auto-generated method stub
				return messageID;
			}

			@Override
			public Supplier<Function<String, JxMapsPanel>> getMapSupplier() {
				return new Supplier<Function<String ,JxMapsPanel>>() {
					
					@Override
					public Function<String, JxMapsPanel> get(){
						return JxMapsPanel.DEFAULT_FACTORY;
					}
				};
			}

			@Override
			public String getTitle() {
				return "Map";
			}};
			
	}

}
