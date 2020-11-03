package yx48_zh16.realgame.model;

import provided.jxMaps.utils.IPlace;

/**
 * Adapter to provide game services
 *
 */
public interface IGameAdapter {
	
	/**
	 * Drop a marker on the Game View
	 * @param place location of the marker
	 * @param labelText label
	 */
	void dropMarker(IPlace place, String labelText);
	
}
