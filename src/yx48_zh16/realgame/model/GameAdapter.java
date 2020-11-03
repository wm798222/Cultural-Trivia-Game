package yx48_zh16.realgame.model;

import provided.jxMaps.utils.IPlace;

/**
 * Game Adapter to provide service to client 
 *
 */
public class GameAdapter implements IGameAdapter{

	/**
	 * The game model
	 */
	private RealGameModel model;
	
	/**
	 * constructor
	 * @param gameModel game model
	 */
	public GameAdapter(RealGameModel gameModel) {
		model = gameModel;
	}
	
	@Override
	public void dropMarker(IPlace place, String labelText) {
		model.dropMarker(place, labelText);
		
	}


	/**
	 * add questions
	 * @param city1Loc city1
	 * @param city2Loc city2
	 * @param city3Loc city3
	 * @param city4Loc city4
	 */
	public void addQuestion(IPlace city1Loc, IPlace city2Loc, IPlace city3Loc, IPlace city4Loc) {
		model.addQuestionMarker(city1Loc, city2Loc, city3Loc, city4Loc);
	}

}
