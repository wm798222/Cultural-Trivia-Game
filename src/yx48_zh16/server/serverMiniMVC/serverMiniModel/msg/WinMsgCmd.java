package yx48_zh16.server.serverMiniMVC.serverMiniModel.msg;

import common.cmd.ARoomMsgCmd;
import common.packet.RoomDataPacket;
import provided.datapacket.IDataPacketID;

/**
 * Unknown Command for IWinMsg
 *
 */
public class WinMsgCmd extends ARoomMsgCmd<IWinMsg>{
		

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -1405207551102536378L;

	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IWinMsg> host, Void... params) {
		this.cmd2model.displayText("Server: You Win!");
		return null;
	}
}
