package structures.basic;

import java.util.ArrayList;

public class Board {

	private Tile[][] tiles;
	public ArrayList<Unit> activeUnits;
	
	public Board() {
		tiles = new Tile[9][5];
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j++) {
				tiles[i][j] = utils.BasicObjectBuilders.loadTile(i, j);
			}
		}
		activeUnits = new ArrayList<Unit>();
	}
	
	public void addUnit(Unit u) {
		activeUnits.add(u);
	}
	
	public Tile getTile(int i, int j) {
		return tiles[i][j];
	}
}
