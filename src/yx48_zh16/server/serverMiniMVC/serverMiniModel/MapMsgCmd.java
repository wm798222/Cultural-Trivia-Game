package yx48_zh16.server.serverMiniMVC.serverMiniModel;

import java.util.function.Supplier;

import javax.swing.JComponent;
import common.ICmd2ModelAdapter;
import common.cmd.ARoomMsgCmd;
import common.packet.RoomDataPacket;
import provided.datapacket.IDataPacketID;

/**
 * The map processing command that could be passed to remote users.
 *
 */
public class MapMsgCmd extends ARoomMsgCmd<IMapMessage> {
		
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -4235523428184429172L;
	
	/**
	 * The command to model adapter.
	 */
	transient ICmd2ModelAdapter cmd2ModelAdpt;

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.cmd2ModelAdpt = cmd2ModelAdpt;
	}
		
//	@SuppressWarnings("rawtypes")
//	@Override
//	public Void apply(IDataPacketID index, ConnectionDataPacket<IMapMessage> host, Void... params) {
//		System.out.println("Processing map");
//
//		this.cmd2ModelAdpt.displayComponent(() -> {
//				
//				String apiKey = cmd2ModelAdpt.getLocalAPIKey();
//				JPanel mapPanel = host.getData().make(apiKey).getView();
////				JxMapsPanel mapPanel = jxMapPanel.defaultFactory.make("AIzaSyBLiQTpVmEZdf1Nu-u1oeuPa9PM6bfweeI");
//				mapPanel.setPreferredSize(new java.awt.Dimension(700,500));
//				return mapPanel;
//			}
//		);
//		return null;
//	}

	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IMapMessage> host, Void... params) {
		Supplier<JComponent> mapPanel = new Supplier<JComponent>() {
			public JComponent get() {
				return host.getData().getMapSupplier().get().apply("AIzaSyBLiQTpVmEZdf1Nu-u1oeuPa9PM6bfweeI");
			}
		};
		cmd2ModelAdpt.displayComponent(mapPanel, "Display map title.");
		return null;
	}
}

	
