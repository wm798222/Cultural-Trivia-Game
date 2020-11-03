package yx48_zh16.server.serverMVC.serverController;


import provided.mixedData.MixedDataKey;

/**
 * Definition of the adapter for the mini to access main MVC methods.
 *
 */
public interface IMiniAccessMainAdapter {

	/**
	 * Method for processing leaving the chat room.
	 */
	public void leave();

//	public HashMap<Integer, Integer> getValueFromDB(MixedDataKey key);
//
//	public ArrayList<Integer> getTeamAList();
//
//	public ArrayList<Integer> getTeamBList();

	/**
	 * put item in DB
	 * @param <T> T Type
	 * @param key key for this item
	 * @param value value of this item
	 */
	public <T> void putInServerDB(MixedDataKey<T> key, T value);

	/**
	 * get item from DB
	 * @param <T> T Type
	 * @param key key for this item
	 * @return T Type
	 */
	public <T> T getFromServerDB(MixedDataKey<T> key);


//	public void createRoomAndGetRoom();

}
