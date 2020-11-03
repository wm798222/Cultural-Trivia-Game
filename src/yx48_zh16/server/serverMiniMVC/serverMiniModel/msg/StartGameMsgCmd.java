package yx48_zh16.server.serverMiniMVC.serverMiniModel.msg;

import common.cmd.ARoomMsgCmd;
import common.packet.RoomDataPacket;
import common.virtualNetwork.IVirtualNetwork;
import provided.datapacket.IDataPacketID;
import yx48_zh16.realgame.controller.RealGameController;
import yx48_zh16.realgame.model.IGameAdapter;
import yx48_zh16.realgame.view.RealGameView;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.IStartGameMsg;

/**
 * The Command to process IStartGameMsg.
 *
 */
public class StartGameMsgCmd extends ARoomMsgCmd<IStartGameMsg>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1726369589915379820L;


	/**
	 * Constructor
	 */
	public StartGameMsgCmd() {
		
	}


	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IStartGameMsg> host, Void... params) {
		// TODO Auto-generated method stub
		RealGameController controller = host.getData().getGameController().get();
		
		
		
		
		IGameAdapter gameUpdateAdapter = controller.getGameAdapter();
		this.cmd2model.putItemInDB(host.getData().getKey(), gameUpdateAdapter);
		
//		MixedDataKey key = host.getData().getMddKey();
		
//		this.cmd2model.putItemInDB(key, gameUpdateAdapter);
		
//		IGameAccessServerAdapter gameAccessServerAdapter = host.getData().getGameAccessServerAdapter();
//		IServerAccessGameAdapter serverAccessGame = controller.getAccessServer(gameAccessServerAdapter);
//		IServerAccessGameAdapter serverAccessGame = controller.getServerAccessGame();
		
		
		controller.startModel();
		RealGameView view = (RealGameView) controller.getView();
		view.start();
		System.out.println("NULL????????");
		System.out.println(this.cmd2model);
		this.cmd2model.displayComponent(() -> view, "Game map title in command");
		view.start();
		
		IVirtualNetwork myTeamMembers = host.getData().getYourGroup();
		IVirtualNetwork allPlayers = host.getData().getAllPeople();
		
		return null;
	}


}
