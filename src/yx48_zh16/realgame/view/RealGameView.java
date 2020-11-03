package yx48_zh16.realgame.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import provided.jxMaps.ui.JxMapsPanel;
import provided.jxMaps.utils.IJxMapsComponentsFactory;
import provided.jxMaps.utils.IJxMaps_Defs;
import provided.jxMaps.utils.IPlace;
import provided.jxMaps.utils.enhanced.EnhancedInfoWindow;
import provided.jxMaps.utils.impl.Place;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import javax.swing.border.TitledBorder;

import com.teamdev.jxmaps.InfoWindowOptions;
import com.teamdev.jxmaps.MapEvent;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.MarkerLabel;

/**
 * The game's definition of the view.
 *
 * @param <TPlacesItem> The type of objects put on the places drop list
 */
public class RealGameView<TPlacesItem> extends JInternalFrame {

	/**
	 * For serialization but don't do it!  Always instantiate GUI elements ON THE TARGET MACHINE.
	 */
	private static final long serialVersionUID = 5283849811019384190L;
	
	
	/**
	 * The adapter to the model
	 */
	private IGView2GModelAdapter<TPlacesItem> view2ModelAdpt;
	
	/**
	 * The content pane of this frame
	 */
	private JPanel contentPane;
	/**
	 * The map panel
	 */
	private final JxMapsPanel pnlMap;
	/**
	 * The control panel
	 */
	private final JPanel pnlControl = new JPanel();
	/**
	 * A label to show on the view
	 */
	private final JLabel lblJxMaps = new JLabel("Submit Your Answer Whenever");
	/**
	 * A label to display a system status notification.
	 */
	private final JLabel lblStatus = new JLabel("No status yet");
	/**
	 * Answer panel
	 */
	private final JPanel pnlAnswer = new JPanel();
	/**
	 * Score Panel
	 */
	private final JPanel pnlScore = new JPanel();
	/**
	 * score label
	 */
	private final JLabel lblNewLabel = new JLabel("Your Score is: ");
	/**
	 * score number label
	 */
	private final JLabel lblScoreNum = new JLabel("0");
	
	
	/**
	 * The components factory associated with the map being shown on the view.
	 * This factory cannot be obtained until AFTER the view is instantiated!
	 */
	private IJxMapsComponentsFactory mapCompFac;

	/**
	 * Submit Score button
	 */
	private final JButton btnSubmitScore = new JButton("Submit Score");
	/**
	 * step label
	 */
	private final JLabel lblNewLabel_1 = new JLabel("Step 2. Left click THE MARKER to show questions");
	/**
	 * step label
	 */
	private final JLabel lblNewLabel_2 = new JLabel("Step 3. Right click THE MARKER to show options to answer the question");
	/**
	 * step label
	 */
	private final JLabel lblSelectAn = new JLabel("Step 4: Select an answer");
	/**
	 * step label
	 */
	private final JLabel lblSubmitScore = new JLabel("Step 6. Submit score when ready (Don't need to answer all questions)");
	/**
	 * step label
	 */
	private final JLabel lblChooseA = new JLabel("Step 1. Choose a marker");
	/**
	 * step label
	 */
	private final JLabel lblStepGo = new JLabel("Step 5: Go to the NEXT marker");
	/**
	 * step label
	 */
	private final JLabel label = new JLabel("");
	
//	private final JPanel pnlChat = new JPanel();
	

	/**
	 * Create the frame with the given Google Maps API key, title and adapter to the model.
	 * The Google Maps API key MUST be the LOCAL system's key, not a key from a remote entity!
	 * @param googleApiKey The local system's Google Maps API key 
	 * @param title The title of the frame
	 * @param igView2GModelAdapter The adapter to the model
	 */
	public RealGameView(String googleApiKey, String title, IGView2GModelAdapter<TPlacesItem> igView2GModelAdapter) {
		this.view2ModelAdpt = igView2GModelAdapter;
		pnlMap = JxMapsPanel.FACTORY.apply(googleApiKey, (map, pnlChat) -> {
			map.setZoom(2);
		});
		
		
		
		// Write Quizzes
		IPlace place1 = new Place("Rice Univeristy", 29.71724, -95.40150, 5);	
		writeQuiz(place1, place1.getName() + ": Who is the instructor of Comp310?", "Luay", "Swong!!", "Micheal Burke", "Scott Rixner", 1, igView2GModelAdapter);
		
		IPlace place2 = new Place("Cananda", 56.1304, -106.3468, 5);	
		writeQuiz(place2, place2.getName() + ": What's on Canada's national flag?", "Bear", "Sun", "Maple Leaf", "Stars", 2, igView2GModelAdapter);
		
		IPlace place3 = new Place("Australia", -25.2744, 133.7751, 5);	
		writeQuiz(place3, place3.getName() + ": Is Australia the home to the Great Barrier Reef?", "Yes", "No", "-", "-", 0, igView2GModelAdapter);
		
		IPlace place4 = new Place("Japan", 36.2048, 138.2529, 5);	
		writeQuiz(place4, place4.getName() + ": What's highest mountain in Japan?", "Mount Kita", "Mount Yari", "Mount Hotakedake", "Mount Fuji", 3, igView2GModelAdapter);
		
		IPlace place5 = new Place("Sri Lanka", 7.8731, 80.7718, 5);	
		writeQuiz(place5, place5.getName() + " :Sri Lanka was known as Ceylon until what year?", "1998", "1963", "1972", "1889", 2, igView2GModelAdapter);
		
		IPlace place6 = new Place("China", 35.8617, 104.1954, 5);	
		writeQuiz(place6, place6.getName() + ": How many stars are there on the flag of China?", "2", "5", "30", "4", 1, igView2GModelAdapter);
		
		IPlace place7 = new Place("Mongolia", 46.8625, 103.8467, 5);	
		writeQuiz(place7, place7.getName() + ": What is the currency of Mongolia?", "Tugrik", "Topchok", "Kirgin", "Yena", 0, igView2GModelAdapter);
		
		IPlace place8 = new Place("Random", -39.968701, -11.997239, 5);	
		writeQuiz(place8, place8.getName() + ": In which country is there a natural gas pit nicknamed the ‘Door to Hell’ that has been on fire since 1971?", "Turkmenistan", "Azerbaijan", "Bulgaria", "Iceland", 0, igView2GModelAdapter);
		
		IPlace place9 = new Place("China2", 29.126972, 111.138598, 5);
		writeQuiz(place9, place9.getName() + ": Which celebrity was carried by their minders along the Great Wall of China?", "Lady Gaga", "Justin Bieber", "Harry Styles", "Snoop Dogg", 0, igView2GModelAdapter);
		
		IPlace place10 = new Place("Vietnam", 14.0583, 108.2772, 5);
		writeQuiz(place10, place10.getName() + ": In 1976, Saigon changed its name to …?", "Hanoi", "Saigon City", "Pattaya", "Ho Chi Minh City", 3, igView2GModelAdapter);
		
		IPlace place11 = new Place("India", 20.5937, 78.9629, 5);
		writeQuiz(place11, place11.getName() + ": What is the population of India?", "1.2 billion", "2 billion", "870 million", "1 billion", 0, igView2GModelAdapter);
		
		IPlace place12 = new Place("The US",  37.0902, -95.7129, 5);
		writeQuiz(place12, place12.getName() + ": In what year did Christopher Columbus land in North America?", "1423", "1482", "1570", "1492", 3, igView2GModelAdapter);
		
		
		// Colleges
		
		IPlace place13 = new Place("Harvard University", 42.3770, -71.1167, 10);
		writeQuiz(place13, place13.getName() + ": Harvard University was originally called?", "Veritas University", "Widener University", "New College", "British North America College", 2, igView2GModelAdapter);
		
		IPlace place14 = new Place("Yale University", 41.3163, -72.9223, 10);
		writeQuiz(place14, place14.getName() + ": Which one is Yale's secret society?", "Illuminati", "Skull and Bones", "Carbonari", "Bohemian Club", 1, igView2GModelAdapter);
	
		IPlace place15 = new Place("Dartmouth College", 43.7044, -72.2887, 10);
		writeQuiz(place15, place15.getName() + ": Is it the smallest Ivy?", "Yes", "No", "Not Sure", "Whatever", 0, igView2GModelAdapter);
		
		IPlace place16 = new Place("Cornell University", 42.4534, -76.4735, 10);
		writeQuiz(place16, place16.getName() + ": The Cornell mascot is?", "The Stanford Wolf", "Handsome Dan", "Big Red Bear", "The Stanford Tree", 2, igView2GModelAdapter);
		
		IPlace place17 = new Place("Stanford University", 37.4275, -122.1697, 10);
		writeQuiz(place17, place17.getName() + ": The Stanford mascot is?", "The Stanford Wolf", "Handsome Dan", "Nittany Lion", "The Stanford Tree", 3, igView2GModelAdapter);
		

		
//		// Marker for question
//		igView2GModelAdapter.dropQuestionMarker(new Place("Houston", 29.71724, -95.40150, 11));
		
		
		initGUI();
	}
	
	
	/**
	 * Helper function to write quiz
	 * @param place place
	 * @param question the question
	 * @param option0 first option
	 * @param option1 second option
	 * @param option2 third option
	 * @param option3 fourth option
	 * @param answerIdx index of the answer
	 * @param igView2GModelAdapter adpater
	 */
	private void writeQuiz(IPlace place, String question, String option0, String option1, String option2, String option3, 
			int answerIdx, IGView2GModelAdapter<TPlacesItem> igView2GModelAdapter) {
		
		
		mapCompFac = pnlMap.getComponentsFactory();
		//make Marker
		Marker marker = mapCompFac.makeMarker(null); // make a marker with default settings
		marker.setPosition(place.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel = new MarkerLabel(); // make label for the marker
		mLabel.setText(place.getName()); // Put the requested location as the label text
		marker.setLabel(mLabel);  // install the label into the marker
		
		InfoWindowOptions winOptions = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		// add question
		winOptions.setContent(question);
		EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(winOptions); // make the info window with the options
		// Add info window toggling click behavior to the marker
		// click to show question
		marker.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin.open(marker);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker.setVisible(true); // Make the marker visible
		refresh();
		// right click to answer question
		
		marker.addEventListener(IJxMaps_Defs.IEvent.RIGHT_CLICK, new MapEvent() {
			private boolean buttonMade = false;
			@Override
			public void onEvent() {		
				if (!buttonMade) {
					buttonMade = true;
				    JButton btn0 = new JButton(option0);
				    pnlAnswer.add(btn0);
				    JButton btn1 = new JButton(option1);
				    pnlAnswer.add(btn1);
				    JButton btn2 = new JButton(option2);
				    pnlAnswer.add(btn2);
				    JButton btn3 = new JButton(option3);
				    pnlAnswer.add(btn3);
				    
				    refresh();
				    btn0.addActionListener(new ActionListener() {
				    	@Override
						public void actionPerformed(ActionEvent e) {
				    		if (answerIdx == 0) { 
				    			igView2GModelAdapter.addPoints(2);
				    		} else {
				    			igView2GModelAdapter.addPoints(-1);
				    		}	    		
				    		pnlAnswer.remove(btn0);
				    		pnlAnswer.remove(btn1);	
				    		pnlAnswer.remove(btn2);
				    		pnlAnswer.remove(btn3);	
				    		marker.remove();
				    		
						}
				    });
				    btn1.addActionListener(new ActionListener() {
				    	@Override
						public void actionPerformed(ActionEvent e) {
				    		if (answerIdx == 1) { 
				    			igView2GModelAdapter.addPoints(2);
				    		} else {
				    			igView2GModelAdapter.addPoints(-1);
				    		}
				    		pnlAnswer.remove(btn0);
				    		pnlAnswer.remove(btn1);	
				    		pnlAnswer.remove(btn2);
				    		pnlAnswer.remove(btn3);	
				    		marker.remove();
						}
				    });
				    btn2.addActionListener(new ActionListener() {
				    	@Override
						public void actionPerformed(ActionEvent e) {
				    		if (answerIdx == 2) { 
				    			igView2GModelAdapter.addPoints(2);
				    		} else {
				    			igView2GModelAdapter.addPoints(-1);
				    		}
				    		pnlAnswer.remove(btn0);
				    		pnlAnswer.remove(btn1);	
				    		pnlAnswer.remove(btn2);
				    		pnlAnswer.remove(btn3);
				    		marker.remove();
						}
				    });
				    btn3.addActionListener(new ActionListener() {
				    	@Override
						public void actionPerformed(ActionEvent e) {
				    		if (answerIdx == 3) { 
				    			igView2GModelAdapter.addPoints(2);
				    		} else {
				    			igView2GModelAdapter.addPoints(-1);
				    		}
				    		pnlAnswer.remove(btn0);
				    		pnlAnswer.remove(btn1);	
				    		pnlAnswer.remove(btn2);
				    		pnlAnswer.remove(btn3);	
				    		marker.remove();
						}
				    });
				}}
			}); 
		
	}
	

	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		contentPane.add(pnlMap, BorderLayout.CENTER);
		pnlControl.setToolTipText("Panel associated with control functionality.");
		
		contentPane.add(pnlControl, BorderLayout.EAST);
		pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.PAGE_AXIS));
		lblChooseA.setToolTipText("Instructions to play the game.");
		
		pnlControl.add(lblChooseA);
		lblNewLabel_1.setToolTipText("Instructions to play the game.");
		
		pnlControl.add(lblNewLabel_1);
		lblNewLabel_2.setToolTipText("Instructions to play the game.");
		
		pnlControl.add(lblNewLabel_2);
		lblSelectAn.setToolTipText("Instructions to play the game.");
		
		pnlControl.add(lblSelectAn);
		lblStepGo.setToolTipText("Instructions to play the game.");
		
		pnlControl.add(lblStepGo);
		lblSubmitScore.setToolTipText("Instructions to play the game.");
		
		pnlControl.add(lblSubmitScore);
		lblJxMaps.setToolTipText("Right-clicking will place an info window at that location.");
		
		pnlControl.add(lblJxMaps);
		btnSubmitScore.setToolTipText("Click here if you want to submit your current score.");
		btnSubmitScore.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		pnlControl.add(btnSubmitScore);
		btnSubmitScore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				view2ModelAdpt.submitScore();
				pnlControl.remove(btnSubmitScore);
				refresh();
			}
		});
		pnlAnswer.setToolTipText("Displaying your choices of ansers to the question.");
		pnlAnswer.setBackground(Color.WHITE);
		
		
		
		
		
		
		
		
//		pnlChat.setBorder(new TitledBorder(null, "Talk with teammates", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		pnlChat.setMaximumSize(new Dimension(32767, 30));
//		pnlControl.add(pnlChat);
//		pnlChat.setLayout(new BorderLayout(0, 0));
//		
//		
//		
//		scrollChat.add(textAreaChat);
//		pnlChat.add(scrollChat, BorderLayout.CENTER);
//		
//		
//		this.pnlMessageSend.setLayout(new GridLayout(1, 2, 0, 0));
//		textFieldChat.setToolTipText("Type in your message to teammates here.");
//
//		pnlMessageSend.add(this.textFieldChat);
//		btnSendText.setText("Send message");
//		pnlMessageSend.add(this.btnSendText);
//		pnlChat.add(pnlMessageSend, BorderLayout.PAGE_END);
		
		
		
		
		
		
		pnlAnswer.setMaximumSize(new Dimension(32767, 50));
		pnlAnswer.setBorder(new TitledBorder(null, "Your Answer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlControl.add(pnlAnswer);
		pnlScore.setToolTipText("Your score is displayed here once starting the game.");
		pnlScore.setBackground(Color.WHITE);
		
		pnlScore.setMaximumSize(new Dimension(32767, 30));
		
		pnlControl.add(pnlScore);
		
		pnlScore.add(lblNewLabel);
		lblScoreNum.setToolTipText("This is your current score.");
		pnlScore.add(lblScoreNum);
		
		pnlControl.add(label);
		lblStatus.setToolTipText("Info on the map");
		lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblStatus.setBackground(Color.CYAN);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		pnlControl.add(lblStatus);
		pnlControl.add(Box.createVerticalGlue()); // keeps the components in the box layout all at the top.
		
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		pnlMap.start();
		setVisible(true);
//		refresh();
	}
	
	
//	/**
//	 * Add the given place to the drop lists
//	 * @param place An object representing a place.   Must have an intelligible toString() method. 
//	 */
//	public void addPlace(TPlacesItem place) {
//		cbxPlaces.addItem(place);
//		refresh();
//	}

	/**
	 * Get the map components factory associated with the map being displayed
	 * @return A new IJxMapsComponentsFactory instance
	 */
	public IJxMapsComponentsFactory getMapComponentsFactory() {
		return pnlMap.getComponentsFactory();
	}
	

	/**
	 * Display the given string on the status display string
	 * @param str  The string to display
	 */
	public void displayStatus(String str) {
		lblStatus.setText(str);
		refresh();
	}
	
	/**
	 * Display the Score of yourself
	 * @param str the score
	 */
	public void displayScore(String str) {
		lblScoreNum.setText(str);
		refresh();
	}
	
	/**
	 * Refresh the view by refreshing the view and repainting it.
	 */
	public void refresh() {
		revalidate(); // make sure the layouts are ok.
		repaint();
	}

}
