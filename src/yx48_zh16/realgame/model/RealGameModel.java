package yx48_zh16.realgame.model;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.teamdev.jxmaps.Circle;
import com.teamdev.jxmaps.GeocoderRequest;
import com.teamdev.jxmaps.GeocoderResult;
import com.teamdev.jxmaps.Icon;
import com.teamdev.jxmaps.IconSequence;
import com.teamdev.jxmaps.InfoWindowOptions;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.MapEvent;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.MarkerLabel;
import com.teamdev.jxmaps.MarkerOptions;
import com.teamdev.jxmaps.PlaceSearchRequest;
import com.teamdev.jxmaps.PlacesServiceStatus;
import com.teamdev.jxmaps.Polygon;
import com.teamdev.jxmaps.PolygonOptions;
import com.teamdev.jxmaps.Polyline;
import com.teamdev.jxmaps.PolylineOptions;
import com.teamdev.jxmaps.Size;
import com.teamdev.jxmaps.StandardSymbol;
import com.teamdev.jxmaps.Symbol;

import common.packet.RoomDataPacket;
import common.receivers.IRoomMember;
import common.virtualNetwork.IVirtualNetwork;

import com.teamdev.jxmaps.Map;

import provided.jxMaps.utils.IAction;
import provided.jxMaps.utils.IDistanceCases;
import provided.jxMaps.utils.IJxMapsComponentsFactory;
import provided.jxMaps.utils.IJxMaps_Defs;
import provided.jxMaps.utils.IMapLayer;
import provided.jxMaps.utils.IMapNavigator;
import provided.jxMaps.utils.IPlace;
import provided.jxMaps.utils.enhanced.EnhancedInfoWindow;
import provided.jxMaps.utils.impl.DistanceLogicalHost;
import provided.jxMaps.utils.impl.IPlace_Samples;
import provided.jxMaps.utils.impl.LogicalAction;
import provided.jxMaps.utils.impl.MapLayer;
import provided.jxMaps.utils.impl.Place;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;
import provided.util.file.IFileContents;
import provided.util.file.impl.FileContents;
import provided.util.logic.ILogicalVisitor;


/**
 * The model of the game.
 *
 */
public class RealGameModel {

	/**
	 * The adapter to the view
	 */
	private IGModel2GViewAdapter model2ViewAdpt;
	
	/**
	 * server's room level stub
	 */
	private IRoomMember serverStub;
	
	/**
	 * UUID
	 */
	private UUID uuid;

	/**
	 * The components factory associated with the map being shown on the view.
	 * This factory cannot be obtained until AFTER the view is instantiated!
	 */
	private IJxMapsComponentsFactory mapCompFac;

	/**
	 * The navigator associated with the map being shown on the view
	 * This factory cannot be obtained until AFTER the view is instantiated!
	 */
	private IMapNavigator mapNav;
	
	/**
	 * A dictionary of places to be shown on the view
	 */
	private java.util.Map<String, IPlace> samplePlacesDict = IPlace_Samples.getSamplePlacesDict();

	
	/**
	 * A list of the LatLngs from the most recent info windows that were made
	 */
	private List<LatLng> recentLatLngs = new ArrayList<LatLng>();
	
	
	/**
	 * team score
	 */
	private int teamScore;
	
	/**
	 * num answer
	 */
	private int numAns;
	
	/**
	 * question number
	 */
	private int questionNum;
	
	/**
	 * Your game score
	 */
	private int yourScore = 0;
	
	// Hard coded Locations
	/**
	 * game place dictionary for testing
	 */
	public static final java.util.Map<String, IPlace> GAME_PLACES_DICT = Collections.unmodifiableMap(new HashMap<String, IPlace>() {

		/**
		 * serial ID
		 */
		private static final long serialVersionUID = 7084841522430799717L;

		{
			// Question 1 A
			put("HOUSTON", new Place("Houston", 29.71724, -95.40150, 11));
			put("NYC", new Place("New York City", 40.748974, -73.990288, 11));
			
			// Question 1 B
			put("AUSTIN", new Place("Austin", 30.2672, -97.7431, 11));
			put("LA", new Place("LA", 34.0522, -118.2437, 11));
			

		}
	});
	
	
	/**
	 * Instantiate the model with the given adapter to the view
	 * @param model2ViewAdpt The adapter to the view
	 * @param serverStub server's stub
	 * @param myTeam my own team
	 * @param uuid UUID
	 */
	@SuppressWarnings("rawtypes")
	public RealGameModel(IGModel2GViewAdapter model2ViewAdpt, IRoomMember serverStub, IVirtualNetwork myTeam, UUID uuid) {
		this.model2ViewAdpt = model2ViewAdpt;
//		this.cmd2mAapter = cmd2mAapter;
		this.serverStub = serverStub;
//		this.memberID = memberID;
		this.uuid = uuid;
//		this.key = key;
	}
	

	
	/**
	 * Start the model, configuring the places list on the view and attaching events to the map.
	 */
	public void start() {
		IRMIUtils rmiUtils = new RMIUtils((str)->{ System.out.println("[DemoModel.start()] From rmiUtils: " + str);});
		int classServerPort = IRMI_Defs.STUB_PORT_EXTRA;
		rmiUtils.startRMI(classServerPort);
		
		// load the view with the places in the sample places dictionary
		samplePlacesDict.values().forEach((place)->{
			model2ViewAdpt.addPlace(place);
		});
		
		// Now that the view is instantiated, the map components factory can be obtained.
		mapCompFac = model2ViewAdpt.getMapComponentsFactory();
		// The navigator is obtained from the map components factory
		mapNav = mapCompFac.makeNavigator();

		double boundaryRadius = 50.0; // feet
		
		DistanceLogicalHost distHost = new DistanceLogicalHost (
				()->{return samplePlacesDict.get("WILLY").getLatLng();},
				mapCompFac.getMap()::getCenter,
				IJxMaps_Defs.EARTH_RADIUS_FEET,
				boundaryRadius);
		
		ILogicalVisitor<Void, Map> distLogVisitor = distHost.makeLogicalVisitor(
				new IDistanceCases() {

					@Override
					public void caseLessThan(double distance, Map map) {
						System.out.println("[caseLessThan] distance "+distance+" < "+boundaryRadius+" boundary radius");
					}

					@Override
					public void caseGreaterThanOrEqual(double distance, Map map) {
						System.out.println("[caseGreaterThan] distance "+distance+" >= "+boundaryRadius+" boundary radius");
					}
					
				});
				
		
		mapNav.addMapEvent(IJxMaps_Defs.IEvent.CENTER_CHANGED, new LogicalAction<Map>(distHost, distLogVisitor));
		

		// This is a handler for an event that will be used to display a status label on the view 
		// whenever the center of the map or its zoom level is changed.
		IAction<Map> centerAndZoomUpdate = (map)->{
			LatLng center = map.getCenter();
			double zoom = map.getZoom();
			String status = "Center = "+center+", zoom = "+zoom;
			model2ViewAdpt.displayStatus(status);
			System.out.println("[centerAndZoomUpdate] Status: "+status);
		};
		
		// Add the centerAndZoomUpdate event to both the center-move and zoom changed events
		mapNav.addMapEvent(IJxMaps_Defs.IEvent.CENTER_CHANGED, centerAndZoomUpdate);
		mapNav.addMapEvent(IJxMaps_Defs.IEvent.ZOOM_CHANGED, centerAndZoomUpdate);

		
		// Add a right-click MapMoustEvent to the map
		mapNav.addMapMouseEvent(IJxMaps_Defs.IEvent.RIGHT_CLICK, (mouseEvt)->{
			System.out.println("[Right-click mouse] LatLng = "+mouseEvt.latLng());
			EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(null);  // Make an info window with the default options.
			infoWin.setPosition(mouseEvt.latLng()); // Set the position of the info window on the map.
			
			String filename = Math.random()<0.5? "Rice_Owl_flying.png":"humbird.gif"; // Randomly pick an image
			String imageURL = rmiUtils.getClassFileServerURL()+"provided/jxMaps/utils/images/"+filename; // assemble the whole image URL
			System.out.println("[Right-click mouse] imageURL = "+imageURL);
			infoWin.setContent("<b style='color:red'>Location:</b><br>"+mouseEvt.latLng()+"<br><img src=\""+imageURL+"\">"); // assemble the HTML to display
			infoWin.open(); 
			
			recentLatLngs.add(infoWin.getPosition()); // Save this info win's LatLng the list of recent LatLngs
		});
		

		
		model2ViewAdpt.refresh(); // refresh the view to make sure everything is laying out correctly.
//		timer.schedule(cmd2mAapter.sendMessageToMember(IQuestion1Msg, member);, delay);
		
		
	}

	/**
	 * Add a marker at the map's center that will toggle an info window open/closed with left-clicks
	 */
	public void addMarkerAtCenter() {
		System.out.println("[DemoModel.addMarkerAt()] cwd = "+ System.getProperty("user.dir"));  // Just a check to make sure that the working directory is properly set to the bin folder.
				
		// Load an IFileContents object with the contents of an image file.   Need to save the type of file because will need that later.
		IFileContents iconFileContents = new FileContents("Rice Owl", "/provided/jxMaps/utils/images/Rice_OwlBlueTMCMYK300DPI.png", "png");  // filename of image file has leading slash to make it relative to the root.  Otherwise is relative to the provided.util.file.impl folder.

		// iconFileContents is fully Serializable and could be transmitted to a client if necessary.
		
		// The following is what a client would do if given an IFileContents object with the contents of an image file.
		Icon icon = new Icon();		
		icon.loadFromStream(iconFileContents.getInputStream(), iconFileContents.getInfo()); // load the image into an icon object, using the image type stored in the IFileContent's info field.
		icon.setScaledSize(new Size(50,50));   // scale the image down to 50x50 pixels
		
		// Making a marker with a custom icon
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setIcon(icon); // set the marker's icon
		markerOptions.setVisible(false);  // want to marker to be initially invisible
		
		Marker marker = mapCompFac.makeMarker(markerOptions);  // Ask the map components factory to make a marker for the associated map with the given options.
		
		// Set the marker at the center of the map
		marker.setPosition(mapCompFac.getMap().getCenter());

		// Set the marker's label
		MarkerLabel mLabel = new MarkerLabel();
		mLabel.setText(iconFileContents.getName());  // label is the name of the icon here (could be anything desired)
		marker.setLabel(mLabel);
		marker.setVisible(true);  // now show the marker
		
		// Make an enhanced info window 
		EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(null); // Ask the map component factory to make an info window with the default options.		
		infoWin.setContent("Howdy pardner from <br>"+marker.getPosition()+"!");  // Set the text of the info window

		// Add an click event to the marker that will toggle the info window on/off.
		marker.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {
			
			@Override
			public void onEvent() {
				if(infoWin.isOpen()) {
					infoWin.close();  // close the info window
				}
				else {
					infoWin.open(marker);  // Open the info window, attached to the top of the marker.
				}
			}
			
		});
		
//		// use adpater to tell the client model to send everyone in your team the marker msg
//		gameAccessClientMMAdatper.tellTeamMakeMarker(mapCompFac.getMap().getCenter(), "Your Teammate Just Gave You a Hint", myTeam);
		
	}



	/**
	 * Move the center of the map to the location specified by the given place
	 * @param place The IPlace to move the map to.
	 */
	public void moveMapTo(IPlace place) {
		mapNav.moveTo(place);   // Let the navigator move the map.	
	}
	
	/**
	 * Create markers at the LatLng corresponding to the geocoded given location.  The markers
	 * will toggle an info window open/close when left-clicked.  Note that a given location may
	 * result in multiple markers being made.
	 * @param location  The location to geocode.
	 */
	public void geocodeRequest(String location) {
		GeocoderRequest request = new GeocoderRequest(); // make a geocode request object
		request.setAddress(location);  // set the request's address to the desired location
		
		// Have the map service the geocode request
		mapNav.serviceGeoCoderRequest(request, (results, status)->{
			// This consumer will run when the geocode request has finished
			// results = the possibly multiple geocoding results (e.g. try "Rice University")
			for(int index = 0;index<results.length; index++) {
				// Go through the results one by one
				GeocoderResult result = results[index];
				System.out.println("[DemoModel.geocodeRequest(\""+location+"\")] Result: ("+ index +") "+result.getFormattedAddress()+" = "+result.getGeometry().getLocation());
				Marker marker = mapCompFac.makeMarker(null); // make a marker with default settings
				marker.setPosition(result.getGeometry().getLocation());  // Set the marker's position to the result's position.
				MarkerLabel mLabel = new MarkerLabel(); // make label for the marker
				mLabel.setText(request.getAddress()); // Put the requested location as the label text
				marker.setLabel(mLabel);  // install the label into the marker
				
				InfoWindowOptions winOptions = new InfoWindowOptions(); // make an info window
				// Set the info window options to show the geocoded address of the requested location and its LatLng.
				winOptions.setContent("("+(index+1)+" of "+results.length+") "+result.getFormattedAddress()+"<br/>"+result.getGeometry().getLocation());
				EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(winOptions); // make the info window with the options
				// Add info window toggling click behavior to the marker
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
			}
			
		});		
	}
	
	/**
	 * Make a polygon on the map using the recentLatLngs list of locations
	 */
	public void makePolygon() {
		
		PolygonOptions options = new PolygonOptions();
		options.setStrokeColor("red"); // set the line color
		options.setFillColor("yellow"); // set the fill color
		options.setVisible(false);  // Don't make it visible until later
		options.setPaths(new LatLng[][] {recentLatLngs.toArray(new LatLng[recentLatLngs.size()])}); // set the polygon's paths

		Polygon polygon = mapCompFac.makePolygon(options);  // make a new polygon with the given options

// The polygon path can be set after construction if desired:
//		polygon.setPath(recentLatLngs.toArray(new LatLng[recentLatLngs.size()])); // set the path of polygon

		polygon.setVisible(true);  // make the polygon visible
	}


	/**
	 * Make a polyline on the map using the recentLatLngs list of locations
	 */
	public void makePolyline() {
		
		PolylineOptions options = new PolylineOptions();
		options.setVisible(false);  // Don't make it visible until later.  The default in PolylineOptions is visible=true otherwise.	
		options.setStrokeColor("blue"); // set the line color
		
		Symbol iconArrow = new Symbol(); // make an icon to put on the polyline
		iconArrow.setPath(StandardSymbol.FORWARD_CLOSED_ARROW);  // Make the icon an arrowhead
		Symbol iconCircle = new Symbol(); // make an icon to put on the polyline
		iconCircle.setPath(StandardSymbol.CIRCLE); // Make the icon a circle

		List<IconSequence> iconSequences = new ArrayList<IconSequence>(); // A list of icon sequences to use on the polyline
		
		IconSequence iconSequenceStart = new IconSequence(); // make an icon sequence to hold the icon
		iconSequenceStart.setIcon(iconCircle);  // set the icon to use
		iconSequenceStart.setOffset("0%");  // put the circle at the start of the polyline
		
		
		iconSequences.add(iconSequenceStart); // add the icon sequence to the list 
		
		int numArrows = 5;  // The number of arrowheads to put on the polyline
		
		// Make and add the arrowheads to the icon sequences
		for(int step = 0; step < numArrows; step++) {
			IconSequence iconSequence = new IconSequence();  // make an icon sequence to hold an icon
			iconSequence.setIcon(iconArrow);  // set the icon to use
			iconSequence.setOffset((100/numArrows)*(step+1)+ "%"); // How far along the polyline to put the icon.  "0%"= beginning, "100%" = end	
			iconSequences.add(iconSequence); // add the icon sequence to the list 
		}
		
		options.setIcons(iconSequences.toArray(new IconSequence[iconSequences.size()])); // Set the polyline's icon sequences	
			
		options.setPath(recentLatLngs.toArray(new LatLng[recentLatLngs.size()]));  // set the path of the polyline
		
		
		Polyline polyline = mapCompFac.makePolyline(options);  // make a new polyline with the given options

		polyline.setVisible(true);  // make the polyline visible
	}
	

	/**
	 * Clear the list of recent LatLngs
	 */
	public void clearRecentLatLngs() {
		recentLatLngs.clear();
	}
	
	
	/**
	 * A layer that holds the markers made during a search
	 */
	private IMapLayer searchMapLayer = new MapLayer();
	/**
	 * Search within the given radius of the center of the map for a particular type of place. 
	 * Also draw a circle of that radius centered on the map. 
	 * @param placeType The type of place to look for, e.g. "restaurant", "bar", etc.
	 * @param radius The radius to look in, in meters.
	 */
	public void searchNearbyPlaces(String placeType, double radius) {
		System.out.println("[DemoModel.searchNearbyPlaces] place type = "+placeType+", radius  = "+radius);
		
		LatLng searchCenter = mapNav.getMap().getCenter(); // The center of the search
		
//		RadarSearchRequest request = new RadarSearchRequest(); // A radar search seems to be basically the same thing as a places search
		PlaceSearchRequest request = new PlaceSearchRequest();  // make a new places search request
		request.setLocation(searchCenter); // Search relative to the search center location
		request.setTypes(new String[] {placeType}); // Set the type of place to search for
		request.setRadius(radius);  // Set the radius of the search
		
		// Invoke a places search with the above request and the given handler function
		mapNav.servicePlacesSearch(request, (results, status)->{
			if (status == PlacesServiceStatus.OK) { // Make sure that the search went ok.
				// loop through the results
				searchMapLayer.setVisible(false); // Make the previous search's markers and circle invisible.
				searchMapLayer.clear(); // This is just to reuse the same MapLayer object -- a more typical use case would make a new MapLayer here.
				
				for(int idx=0; idx<results.length;idx++) {
					System.out.println(idx+". "+results[idx].getName()); // print the result to the console
					Marker marker = mapCompFac.makeMarker(null); // Make an invisible marker
					MarkerLabel label = new MarkerLabel(); // Make a marker label
					label.setText(results[idx].getName()); // Set the text of the marker label to the result's name
					marker.setLabel(label); // install the label into the marker
					marker.setPosition(results[idx].getGeometry().getLocation()); // set the location of the marker to be the location of the result
					searchMapLayer.add(marker); // Add to the MapLayer
					//					marker.setVisible(true);  // Make the marker visible -- commented out so that MapLayer can do this en masse.					
				}
				
				Circle circle = mapCompFac.makeCircle(null); // Make a circle object with the default options
				circle.setCenter(searchCenter); // Center the circle on the search center
				circle.setRadius(radius); // Set the radius of the circle to be the search radius
//				circle.setVisible(true); // Make the circle visible. -- commented out so that MapLayer can do this en masse.					
				searchMapLayer.add(circle); // Add to the MapLayer
				searchMapLayer.setVisible(true);  // Make all the entities in the MapLayer visible.
			}
		});	
		

	}



	/**
	 * submit answer
	 * @param text answer
	 */
	public void submitAns(String text) {
		int ans = Integer.parseInt(text);
		
		// 1. updated the team score for myself
		setTeamScore(getTeamScore() + ans);
		
		// 2. updated number of Answers in the team
		setNumAns(getNumAns() + ans);
		
		
		// 3. tell other people in your team to update the score
		
		
		// 4. tell other people in your team to update the number of answers
		
		
		
	}



	/**
	 * getter of team score
	 * @return team score
	 */
	public int getTeamScore() {
		return teamScore;
	}



	/**
	 * setter of team score
	 * @param teamScore team score
	 */
	public void setTeamScore(int teamScore) {
		this.teamScore = teamScore;
	}



	/**
	 * get number answer
	 * @return number answer
	 */
	public int getNumAns() {
		return numAns;
	}



	/**
	 * set number answer
	 * @param numAns number answer
	 */
	public void setNumAns(int numAns) {
		this.numAns = numAns;
	}



	/**
	 * drop marker for testing
	 * @param text lag
	 * @param text2 lng
	 */
	public void dropMarker(String text, String text2) {
		
	}
	
	


	/**
	 * start Game for testing
	 */
	public void startGame() {
		// display the first question
		IPlace place1 = GAME_PLACES_DICT.get("HOUSTON");
		IPlace place2 = GAME_PLACES_DICT.get("NYC");
		
		IPlace place3 = GAME_PLACES_DICT.get("AUSTIN");
		IPlace place4 = GAME_PLACES_DICT.get("LA");
		
		createQuetion(place1, place2, place3, place4);
			
		
	}



	/**
	 * create Question
	 * @param place1 place 1
	 * @param place2 place 2
	 * @param place3 place 3
	 * @param place4 place 4
	 */
	private void createQuetion(IPlace place1, IPlace place2, IPlace place3, IPlace place4) {
		
		// create Route A City 1 Marker
		Marker marker = mapCompFac.makeMarker(null); // make a marker with default settings
		marker.setPosition(place1.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel = new MarkerLabel(); // make label for the marker
		mLabel.setText(place1.getName()); // Put the requested location as the label text
		marker.setLabel(mLabel);  // install the label into the marker
		
		InfoWindowOptions winOptions = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions.setContent("Route A City 1: " + place1.getName());
		EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(winOptions); // make the info window with the options
		// Add info window toggling click behavior to the marker
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
		
		// create Route A City 2 Marker
		Marker marker2 = mapCompFac.makeMarker(null); // make a marker with default settings
		marker2.setPosition(place2.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel2 = new MarkerLabel(); // make label for the marker
		mLabel2.setText(place2.getName()); // Put the requested location as the label text
		marker2.setLabel(mLabel2);  // install the label into the marker
		
		InfoWindowOptions winOptions2 = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions2.setContent("Route A City 2: " + place2.getName());
		EnhancedInfoWindow infoWin2 = mapCompFac.makeInfoWindow(winOptions2); // make the info window with the options
		// Add info window toggling click behavior to the marker
		marker2.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin2.open(marker2);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin2.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker2.setVisible(true); // Make the marker visible
		
		// create Route B City 1 Marker
		Marker marker3 = mapCompFac.makeMarker(null); // make a marker with default settings
		marker3.setPosition(place3.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel3 = new MarkerLabel(); // make label for the marker
		mLabel3.setText(place3.getName()); // Put the requested location as the label text
		marker3.setLabel(mLabel3);  // install the label into the marker
		
		InfoWindowOptions winOptions3 = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions3.setContent("Route B City 1: " + place3.getName());
		EnhancedInfoWindow infoWin3 = mapCompFac.makeInfoWindow(winOptions3); // make the info window with the options
		// Add info window toggling click behavior to the marker
		marker3.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin3.open(marker3);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin3.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker3.setVisible(true); // Make the marker visible
		
		// create Route B City 2 Marker
		Marker marker4 = mapCompFac.makeMarker(null); // make a marker with default settings
		marker4.setPosition(place4.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel4 = new MarkerLabel(); // make label for the marker
		mLabel4.setText(place4.getName()); // Put the requested location as the label text
		marker4.setLabel(mLabel4);  // install the label into the marker
		
		InfoWindowOptions winOptions4 = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions4.setContent("Route B City 2: " + place4.getName());
		EnhancedInfoWindow infoWin4 = mapCompFac.makeInfoWindow(winOptions4); // make the info window with the options
		// Add info window toggling click behavior to the marker
		marker4.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin4.open(marker4);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin4.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker4.setVisible(true); // Make the marker visible	
		
	}



	/**
	 * submit answer for testing
	 * @param string answer
	 */
	public void submitAnswer(String string) {
		if (questionNum== 0 && string == "A") {
			yourScore += 3;
			String str = Integer.toString(yourScore);
			model2ViewAdpt.displayScore(str); 
			// request the next Question
			
		} else {
			yourScore -= 1;
			String str = Integer.toString(yourScore);
			model2ViewAdpt.displayScore(str); 
		}
		
	}



	/**
	 * drop marker for testing
	 * @param place place 
	 * @param labelText label
	 */
	public void dropMarker(IPlace place, String labelText) {
		
		// create a Marker
				Marker marker = mapCompFac.makeMarker(null); // make a marker with default settings
				marker.setPosition(place.getLatLng());  // Set the marker's position to the result's position.
				MarkerLabel mLabel = new MarkerLabel(); // make label for the marker
				mLabel.setText(place.getName()); // Put the requested location as the label text
				marker.setLabel(mLabel);  // install the label into the marker
				
				InfoWindowOptions winOptions = new InfoWindowOptions(); // make an info window
				// Set the info window options to show the geocoded address of the requested location and its LatLng.
				
				//"Route A City 1: " is an example for labelText
				
				winOptions.setContent(labelText + place.getName());
				EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(winOptions); // make the info window with the options
				// Add info window toggling click behavior to the marker
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
		
	}



	/**
	 * add question marker
	 * @param place1 place 1
	 * @param place2 place 2	
	 * @param place3 place 3
	 * @param place4 place 4
	 */
	public void addQuestionMarker(IPlace place1, IPlace place2, IPlace place3, IPlace place4) {
		// create Route A City 1 Marker
		Marker marker = mapCompFac.makeMarker(null); // make a marker with default settings
		marker.setPosition(place1.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel = new MarkerLabel(); // make label for the marker
		mLabel.setText(place1.getName()); // Put the requested location as the label text
		marker.setLabel(mLabel);  // install the label into the marker
		
		InfoWindowOptions winOptions = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions.setContent("Route A City 1: " + place1.getName());
		EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(winOptions); // make the info window with the options
		// Add info window toggling click behavior to the marker
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
		
		// create Route A City 2 Marker
		Marker marker2 = mapCompFac.makeMarker(null); // make a marker with default settings
		marker2.setPosition(place2.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel2 = new MarkerLabel(); // make label for the marker
		mLabel2.setText(place2.getName()); // Put the requested location as the label text
		marker2.setLabel(mLabel2);  // install the label into the marker
		
		InfoWindowOptions winOptions2 = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions2.setContent("Route A City 2: " + place2.getName());
		EnhancedInfoWindow infoWin2 = mapCompFac.makeInfoWindow(winOptions2); // make the info window with the options
		// Add info window toggling click behavior to the marker
		marker2.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin2.open(marker2);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin2.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker2.setVisible(true); // Make the marker visible
		
		// create Route B City 1 Marker
		Marker marker3 = mapCompFac.makeMarker(null); // make a marker with default settings
		marker3.setPosition(place3.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel3 = new MarkerLabel(); // make label for the marker
		mLabel3.setText(place3.getName()); // Put the requested location as the label text
		marker3.setLabel(mLabel3);  // install the label into the marker
		
		InfoWindowOptions winOptions3 = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions3.setContent("Route B City 1: " + place3.getName());
		EnhancedInfoWindow infoWin3 = mapCompFac.makeInfoWindow(winOptions3); // make the info window with the options
		// Add info window toggling click behavior to the marker
		marker3.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin3.open(marker3);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin3.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker3.setVisible(true); // Make the marker visible
		
		// create Route B City 2 Marker
		Marker marker4 = mapCompFac.makeMarker(null); // make a marker with default settings
		marker4.setPosition(place4.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel4 = new MarkerLabel(); // make label for the marker
		mLabel4.setText(place4.getName()); // Put the requested location as the label text
		marker4.setLabel(mLabel4);  // install the label into the marker
		
		InfoWindowOptions winOptions4 = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions4.setContent("Route B City 2: " + place4.getName());
		EnhancedInfoWindow infoWin4 = mapCompFac.makeInfoWindow(winOptions4); // make the info window with the options
		// Add info window toggling click behavior to the marker
		marker4.addEventListener(IJxMaps_Defs.IEvent.CLICK, new MapEvent() {  
			/**
			 * Used to toggle the visibility of the info window on/off
			 * but this value could be out of sync with the actual info window if it were 
			 * closed manually.
			 */
			private boolean isClosed = true;
			
			@Override
			public void onEvent() {
				if(isClosed) {
					infoWin4.open(marker4);  // Open the info window, attached to the top of the marker.
					isClosed = false; // toggle the flag
				}
				else {
					infoWin4.close();  // close the info window
					isClosed = true; // toggle the flag
				}
			}
			
		});
		marker4.setVisible(true); // Make the marker visible	
		
	}
	
	
	/**
	 * drop question marker for testing
	 * @param place1 place 1
	 */
	public void dropQuestionMarker(IPlace place1) {
		// create Route A City 1 Marker
		Marker marker = mapCompFac.makeMarker(null); // make a marker with default settings
		marker.setPosition(place1.getLatLng());  // Set the marker's position to the result's position.
		MarkerLabel mLabel = new MarkerLabel(); // make label for the marker
		mLabel.setText(place1.getName()); // Put the requested location as the label text
		marker.setLabel(mLabel);  // install the label into the marker
		
		InfoWindowOptions winOptions = new InfoWindowOptions(); // make an info window
		// Set the info window options to show the geocoded address of the requested location and its LatLng.
		winOptions.setContent("Route A City 1: " + place1.getName());
		EnhancedInfoWindow infoWin = mapCompFac.makeInfoWindow(winOptions); // make the info window with the options
		// Add info window toggling click behavior to the marker
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
	}


	/**
	 * add points
	 * @param i point
	 */
	public void addPoints(int i) {
		yourScore += i;
		model2ViewAdpt.displayScore(Integer.toString(yourScore));
	}

	/**
	 * submit score
	 */
	public void submitScore() {
		// send server a submitScore message/cmd
		RoomDataPacket<ISubmitScoreMsg> message;
		ISubmitScoreMsg submitScoreMsg = ISubmitScoreMsg.make(this.serverStub, this.yourScore, this.uuid);	
		message = new RoomDataPacket<ISubmitScoreMsg>(submitScoreMsg, this.serverStub);
		try {
			this.serverStub.receiveMsg(message);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
