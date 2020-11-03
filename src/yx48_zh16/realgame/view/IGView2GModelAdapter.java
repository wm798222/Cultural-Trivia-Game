package yx48_zh16.realgame.view;

import provided.jxMaps.utils.IPlace;

/**
 * The adapter from the JxMaps demo view to the model
 * @author swong
 *
 * @param <TPlacesItem>  The type of object on the places drop list on the view
 */
public interface IGView2GModelAdapter<TPlacesItem> {

	/**
	 * Add a marker to the map
	 */
	void addMarkerAt();

	/**
	 * Move to the place designated by the given place object
	 * @param place  The object drop list that represents a particular place on the map
	 */
	void addMoveTo(TPlacesItem place);

	/**
	 * Ask the model to geocode the given location
	 * @param location The location to geocode
	 */
	void geocodeLocation(String location);

	/**
	 * Create a polygon using a stored list of LatLngs
	 */
	void makePolygon();

	/**
	 * Create a polyline using a stored list of LatLngs
	 */
	void makePolyline();
	
	/**
	 * Clear the list of LatLngs used to make a polygons and polylines
	 */
	void clearPolygonPoints();

	/**
	 * Search nearby the center of the map with the given parameters
	 * @param placeType The type of place to search for
	 * @param radius The radius (meters) to search within
	 */
	void searchNearby(String placeType, String radius);

	/**
	 * submit answer
	 * @param text - text to submit
	 */
	void submitAns(String text);

	/**
	 * drop marker
	 * @param text lag
	 * @param text2 lng
	 */
	void dropMarker(String text, String text2);

	/**
	 * start game
	 */
	void StartGame();

	/**
	 * submit answer
	 * @param string answer
	 */
	void submitAnswer(String string);

	/**
	 * add points
	 * @param i point
	 */
	void addPoints(int i);

	/**
	 * drop question marker
	 * @param place1 place to drop
	 */
	void dropQuestionMarker(IPlace place1);

	/**
	 * submit score
	 */
	void submitScore();


}
