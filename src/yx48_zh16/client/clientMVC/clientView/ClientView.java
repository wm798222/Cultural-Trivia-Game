package yx48_zh16.client.clientMVC.clientView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import provided.discovery.impl.view.DiscoveryPanel;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import yx48_zh16.client.clientMVC.clientModel.Room;
import yx48_zh16.client.clientMVC.clientModel.TeamIdentityWrapper;
import yx48_zh16.client.clientMVC.clientModel.TeamWrapper;
import yx48_zh16.client.clientMVC.clientModel.UserWrapper;
import yx48_zh16.client.clientMiniMVC.clientMiniView.ClientMiniView;

/**
 * The definition of the client's view. 
 *
 * @param <TEndPoint> The type of end point in the discovery server.
 */
public class ClientView<TEndPoint> extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4729634785879405359L;

	/**
	 * Control panel
	 */
	private final JPanel controlPanel = new JPanel();
	
	/**
	 * Quit button
	 */
	private final JButton quitBtn = new JButton("Quit");
	
	/**
	 * Panel for room actions
	 */
	private final JPanel roomsPanel = new JPanel();
	
	/**
	 * Create room button
	 */
	private final JButton createChatTeamBtn = new JButton("Create Team");
	
	/**
	 * Name of the room to create
	 */
	private final JTextField roomName = new JTextField();
	
	/**
	 * Panel for user actions
	 */
	private final JPanel usersPanel = new JPanel();
	
	/**
	 * Store connected users
	 */
	private final JComboBox<UserWrapper> connectedUsers = new JComboBox<UserWrapper> ();
	
	/**
	 * Store connected chat rooms
	 */
	private final JComboBox<TeamWrapper> connectedChatRooms = new JComboBox<TeamWrapper>();
	
	/**
	 * Users label
	 */
	private final JLabel lblNewLabel = new JLabel("Connected Users");
	
	/**
	 * Rooms label
	 */
	private final JLabel lblNewLabel_1 = new JLabel("Connected Teams");
	
	/**
	 * Main panel
	 */
	private final JPanel panel = new JPanel();
	
	/**
	 * Invite button
	 */
	private final JButton btnInvite = new JButton("Invite");
	
	/**
	 * Tabbed panel to store chat rooms
	 */
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * View to model adapter
	 */
	private IView2ModelAdapter iView2ModelAdapter;
	
	/**
	 * Panel for room actions
	 */
	private final JPanel panel_1 = new JPanel();
	
	/**
	 * Available room panel
	 */
	private final JLabel availTeamsLabel = new JLabel("Available Teams");
	
	/**
	 * Dropdown for available rooms
	 */
	private final JComboBox<TeamIdentityWrapper> availableRoomsDropdown = new JComboBox<TeamIdentityWrapper>();
	
	/**
	 * Join room button
	 */
	private final JButton joinBtn = new JButton("Join");
	
	/**
	 * Discovery server panel
	 */
	private final JPanel discoveryServerPnl = new JPanel();
	
	/**
	 * Adapter for discovery server panel
	 */
	private IDiscoveryPanelAdapter<TEndPoint> discoveryServerPanelAdapter;
	
	/**
	 * Discovery panel
	 */
	private DiscoveryPanel<TEndPoint> discoveryServerPanel;
	
	/**
	 * Login panel
	 */
	private final JPanel loginPane = new JPanel();
	
	/**
	 * User name
	 */
	private final JTextField userNameTextField = new JTextField();
	
	/**
	 * Login button
	 */
	private final JButton loginBtn = new JButton("Log in");
	
	/**
	 * Get chat rooms from an user
	 */
	private final JButton btnGetTeams = new JButton("Get Teams");
	
	/**
	 * Dropdown for available users
	 */
	private final JComboBox<UserWrapper> availableUsers = new JComboBox<UserWrapper>();
	
	/**
	 * Available users panel
	 */
	private final JLabel lblAvailableUsers = new JLabel("Available Users");
	
	/**
	 * Connect to user button
	 */
	private final JButton connectToUserBtn = new JButton("Connect ");
	
	/**
	 * Get other connections from an user
	 */
	private final JButton btnGetConectionFrom = new JButton("Get connections from user");
 	
	
	/**
	 * Constructor for the view
	 * @param iView2ModelAdapter - a view to model adapter
	 * @param discoveryServerPanelAdapter - adapter for discovery panel
	 */
	public ClientView(IView2ModelAdapter iView2ModelAdapter, IDiscoveryPanelAdapter<TEndPoint> discoveryServerPanelAdapter) {
		userNameTextField.setToolTipText("Type in a user name");
		userNameTextField.setColumns(10);
		this.iView2ModelAdapter = iView2ModelAdapter;
		this.discoveryServerPanelAdapter = discoveryServerPanelAdapter;
		discoveryServerPanel = new DiscoveryPanel<TEndPoint>(this.discoveryServerPanelAdapter);
		roomName.setToolTipText("Type in the room name in here.");
		roomName.setColumns(10);
		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setBounds(100, 100, 1300, 727);
		controlPanel.setBackground(Color.PINK);
		
		getContentPane().add(controlPanel, BorderLayout.NORTH);
		loginPane.setToolTipText("The log-in panel.");
		
		controlPanel.add(loginPane);
		loginPane.setLayout(new GridLayout(3, 0, 0, 0));
		
		loginPane.add(userNameTextField);
		loginBtn.setToolTipText("Log in with your username to chat. You have to log in first!");
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				iView2ModelAdapter.login(userNameTextField.getText());
			}
			
		});
		
		loginPane.add(loginBtn);
		quitBtn.setToolTipText("Click this to quit and close ChatApp");
		loginPane.add(quitBtn);
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iView2ModelAdapter.quit();
			}
		});
		
		usersPanel.setToolTipText("Handles user-related functionalities");
		controlPanel.add(usersPanel);
		usersPanel.setLayout(new GridLayout(7, 0, 0, 0));
		lblAvailableUsers.setToolTipText("\"Available Users\" label");
		
		usersPanel.add(lblAvailableUsers);
		availableUsers.setToolTipText("Available users to connect to.");
		
		usersPanel.add(availableUsers);
		connectToUserBtn.setToolTipText("Connect with selected available user.");
		
		
		connectToUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iView2ModelAdapter.connectToUser(availableUsers.getItemAt(availableUsers.getSelectedIndex()).getUser());
			}
		});
		
		usersPanel.add(connectToUserBtn);
		lblNewLabel.setToolTipText("\"Connected Users\" label");
		
		usersPanel.add(lblNewLabel);
		connectedUsers.setToolTipText("Select a connected user here.");
		
		usersPanel.add(connectedUsers);
		btnGetTeams.setToolTipText("Click to get all chatrooms that the selected user is connected to.");
		
		btnGetTeams.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				iView2ModelAdapter.getChatrooms(connectedUsers.getItemAt(connectedUsers.getSelectedIndex()).getUser());
			}
			
		});
		
		usersPanel.add(btnGetTeams);
		btnGetConectionFrom.setToolTipText("Get all user that the selected user is connected to.");
		
//		btnGetConectionFrom.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				iView2ModelAdapter.getConnections(connectedUsers.getItemAt(connectedUsers.getSelectedIndex()).getUser());
//			}
//		});
		
		usersPanel.add(btnGetConectionFrom);
		discoveryServerPnl.setToolTipText("Panel associated with the discovery server functionality.");
		
		controlPanel.add(discoveryServerPnl);
		discoveryServerPnl.add(discoveryServerPanel);
		roomsPanel.setToolTipText("Panel to create rooms.");
		
		controlPanel.add(roomsPanel);
		roomsPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		roomsPanel.add(roomName);
		createChatTeamBtn.setToolTipText("Click this to create a room with room name you typed in.");
		createChatTeamBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iView2ModelAdapter.createChatRoom(roomName.getText());
			}
		});
		
		roomsPanel.add(createChatTeamBtn);
		panel.setToolTipText("Panel for room-related functionality.");
		
		controlPanel.add(panel);
		panel.setLayout(new GridLayout(2, 0, 0, 0));
		btnInvite.setToolTipText("Click here to invite a selected user to join a selected \"CONNECTED\" room.");
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iView2ModelAdapter.inviteToRoom(connectedUsers.getItemAt(connectedUsers.getSelectedIndex()).getUser(), (Room) connectedChatRooms.getItemAt(connectedChatRooms.getSelectedIndex()).getChatRoom());
			}
		});
		
		panel.add(btnInvite);
		joinBtn.setToolTipText("Click here to join a selected \"AVAILABLE\" room.");
		joinBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iView2ModelAdapter.joinRoom(availableRoomsDropdown.getItemAt(availableRoomsDropdown.getSelectedIndex()));
			}
		});
		
		panel.add(joinBtn);
		panel_1.setToolTipText("Room selection panel.");
		controlPanel.add(panel_1);
		panel_1.setLayout(new GridLayout(4, 0, 0, 0));
		lblNewLabel_1.setToolTipText("\"Connected Rooms\" label.");
		panel_1.add(lblNewLabel_1);
		connectedChatRooms.setToolTipText("Select a connected room to invite someone");
		panel_1.add(connectedChatRooms);
		availableRoomsDropdown.setToolTipText("Selected an available room to join");
		
		availableRoomsDropdown.setMaximumSize(new Dimension(50,20));
		
		panel_1.add(availableRoomsDropdown);
		availTeamsLabel.setToolTipText("\"Available Rooms\" label.");
		
		panel_1.add(availTeamsLabel);
		tabbedPane.setToolTipText("Display rooms in this panel.");
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		this.setVisible(true);
	}

	/**
	 * Add an user to the connected users dropdown
	 * @param user - an user to add to the drop down
	 */
	public void updateUsers(UserWrapper user) {		
		connectedUsers.insertItemAt(user, 0);
	}
	
	/**
	 * Delete an user from the drop down
	 * @param user - an user to delete from the dropdown
	 */
	public void deleteConnectedUsers(UserWrapper user) {
		int i = 0;
		while (true) {
			if (connectedUsers.getItemAt(i).equals(user)) {
				connectedUsers.remove(i);
			}
			i++;
		}
	}
	
	/**
	 * Update the dropdown of available rooms
	 * @param room - room to add to the dropdown
	 */
	public void updateAvailableRooms(TeamIdentityWrapper room) {
		availableRoomsDropdown.insertItemAt(room, 0);
	}
	
	/**
	 * Install a room to the tabbed panel
	 * @param roomName - name of the room
	 * @param miniView - the GUI of the mini mvc
	 */
	public void installRoom(String roomName, ClientMiniView miniView) {
		// TODO Auto-generated method stub
		tabbedPane.add(roomName, miniView);
	}
	
	/**
	 * Remove a room from the tabbed panel
	 * @param miniView - the view of the room to remove
	 */
	public void removeRoom(ClientMiniView miniView) {
		tabbedPane.remove(miniView);;
	}

	/**
	 * Start the discovery panel
	 */
	public void startDiscoveryPnl() {
		discoveryServerPanel.start();
	}

	/**
	 * Update the list of connected chat rooms
	 * @param chatRoomMemberWrapper - a room to add to the list
	 */
	public void updateConnectedRooms(TeamWrapper chatRoomMemberWrapper) {
		connectedChatRooms.insertItemAt(chatRoomMemberWrapper, 0);
		
	}
	
//	/**
//	 * Update the existing chat rooms in the dropdown
//	 */
//	public void updateExistingRooms() {
//		ArrayList<TeamWrapper> existingChatRooms = this.iView2ModelAdapter.getConnectedChatRooms();
//		this.connectedChatRooms.removeAllItems();
//		
//		for (TeamWrapper room : existingChatRooms) {
//			this.connectedChatRooms.insertItemAt(room, 0);
//		}
//	}

	/**
	 * Delete an user from the list of connected users
	 * @param userWrapper - an user to delete from the dropdown
	 */
	public void deleteFromConnectedUsers(UserWrapper userWrapper) {
		int i = 0;
		while (i <= connectedUsers.getItemCount() - 1) {
			if (connectedUsers.getItemAt(i).getUser().equals(userWrapper.getUser())) {
				break;
			}
			i += 1;
		}
			connectedUsers.removeItemAt(i);	
		
	}
	
	/**
	 * Update the dropdown of existing users.
	 */
	public void updateExistingUsers() {
		ArrayList<UserWrapper> existingUsers = this.iView2ModelAdapter.getConnectedWrappedUsers();
		this.connectedUsers.removeAllItems();
		
		for (UserWrapper user : existingUsers) {
			this.connectedUsers.insertItemAt(user, 0);
		}
	}

	/**
	 * Update the drop down of available users that you can connect to
	 * @param userWrapper - an user to add to the available user dropdown
	 */
	public void updateAvailableUser(UserWrapper userWrapper) {
		// TODO Auto-generated method stub
		this.availableUsers.insertItemAt(userWrapper, 0);
	}

}
