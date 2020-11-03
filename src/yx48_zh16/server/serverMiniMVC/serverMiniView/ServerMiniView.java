package yx48_zh16.server.serverMiniMVC.serverMiniView;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.ScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

/**
 * Definition and implementation of the mini view displaying the chat room content.
 *
 */
public class ServerMiniView extends JPanel {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -2523539088903133804L;
	
	/**
	 * The JPabel for displaying the room name if needed.
	 */
	private final JLabel chatRoomLabel = new JLabel("       ");
	
	/**
	 * The text area for displaying string messages.
	 */
	private final JTextArea textArea = new JTextArea();
	
	/**
	 * The text field for typing in the input of messages.
	 */
	private final JTextField textField = new JTextField();
	
	/**
	 * The button that sends messages when clicked.
	 */
	private final JButton btnSendmessgebutton = new JButton("Send Text");
	
	/**
	 * The panel holding the heading of the chat room.
	 */
	private final JSplitPane splitPane_1 = new JSplitPane();
	
	/**
	 * The button for leaving the chat room.
	 */
	private final JButton leaveButton = new JButton("Leave");
	
	/**
	 * The panel holding the text area displaying texts.
	 */
	private final ScrollPane scrollPane = new ScrollPane();
	
	/**
	 * The adapter for the mini view to update.
	 */
	private IServerMiniViewUpdateAdapter<?> _miniViewAdapter;
	
	/**
	 * The panel holding the input components.
	 */
	private final JPanel panel = new JPanel();
	/**
	 * button to send game
	 */
	private final JButton btnSendGame = new JButton("Send Game");
	
	/**
	 * The constructor of mini view.
	 * @param miniViewAdapter adapter for view to update.
	 */
	public ServerMiniView(IServerMiniViewUpdateAdapter<?> miniViewAdapter) {
		textField.setToolTipText("Enter your messages here.");
		textField.setColumns(20);
		this._miniViewAdapter = miniViewAdapter;
		initGUI();
	}
	
	/**
	 * Initialization the GUI components.
	 */
	private void initGUI() {
		setToolTipText("Panel for displaying content of a single chat room.");
		setLayout(new BorderLayout(0, 0));
		splitPane_1.setToolTipText("Displaying header components.");
		add(splitPane_1, BorderLayout.PAGE_START);
		leaveButton.setToolTipText("Click here to leave the chat room.");
		leaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_miniViewAdapter.leave();
			}
		});
		
		splitPane_1.setLeftComponent(leaveButton);
		splitPane_1.setRightComponent(chatRoomLabel);
		chatRoomLabel.setHorizontalAlignment(SwingConstants.CENTER);
		chatRoomLabel.setForeground(Color.GRAY);
		btnSendmessgebutton.setToolTipText("Click here to send text messages in the text field.");
		
		this.btnSendmessgebutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_miniViewAdapter.sendTextMsg(textField.getText());
				
			}	
		});
		add(scrollPane, BorderLayout.CENTER);
		textArea.setToolTipText("Text messages displayed here.");
		
		scrollPane.add(textArea);
		panel.setToolTipText("Panel for command-related functionality.");
		panel.setLayout(new GridLayout(1, 3, 0, 0));

		panel.add(textField);
		panel.add(btnSendmessgebutton);
		add(panel, BorderLayout.PAGE_END);
		
		btnSendGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				_miniViewAdapter.sendGameMsg();
			}
			
		});
		panel.add(btnSendGame);
	
	}

	/**
	 * Start the GUI frame.
	 */
	public void start() {
		this.setVisible(true);
	}

	/**
	 * Method for displaying text messages.
	 * @param message the string messages.
	 */
	public void displayMessage(String message) {
		textArea.append(message + "\n");
	}
	
	/**
	 * Method for displaying a JComponent in the view.
	 * @param component the JComponent to be displayed.
	 */
	public void displayComponent(JComponent component) {
		System.out.println("Displaying component in view");
		
		// one way to display the component inside the chat room
		
//		remove(scrollPane);
//		add(component, BorderLayout.CENTER);
//		
//		remove(this.splitPane_1);
//		JButton closeImageButton = new JButton("Close image");
//		closeImageButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				remove(component);
//				add(scrollPane, BorderLayout.CENTER);
//				remove(closeImageButton);
//				add(splitPane_1, BorderLayout.PAGE_START);
//			}
//			
//		});
//		add(closeImageButton, BorderLayout.PAGE_START);
		
		
		// display the component as a pop-up window
		
		JFrame imageFrame = new JFrame();
		imageFrame.getContentPane().add(component);
		imageFrame.setBounds(new Rectangle(800,800));
//		imageFrame.pack();
		imageFrame.setVisible(true);
		
	}
	
	/**
	 * Method for displaying a component upon receiving a certain message.
	 * @param component The component to be displaying while removing the existing components.
	 */
	public void addComponent(JComponent component) {
		add(component, BorderLayout.CENTER);
	}

}
