package yx48_zh16.server.serverMiniMVC.serverMiniModel;

import java.util.function.Function;
import java.util.function.Supplier;

import provided.datapacket.IDataPacketID;
import provided.jxMaps.ui.JxMapsPanel;

/**
 * Concrete class of IMapMessage.
 *
 */
public class MapMessage implements IMapMessage {

	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = -2497674457596471012L;
	
	/**
	 * The game name.
	 */
	String gameName;
	
	/**
	 * Constructor of the class
	 * @param gameName The String name of the game
	 */
	public MapMessage(String gameName) {
		this.gameName = gameName;
	}

	@Override
	public IDataPacketID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataPacketID getMessageID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Supplier<Function<String, JxMapsPanel>> getMapSupplier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
