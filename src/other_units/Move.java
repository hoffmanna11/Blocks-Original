package other_units;

import main.Main;

public class Move implements Cloneable {
	public String dir;
	public Location loc;
	
	public Move(String dir, int x, int y){
		this.dir = dir;
		this.loc = new Location(x, y);
	}
	
	public Move(String dir, Location loc){
		this.dir = dir;
		this.loc = loc;
	}
	
	public static Move getRandMove(Location loc){
		String dir;
		Move move;

		do {
			dir = Main.getRandDir();
			move = new Move(dir, loc.getX(), loc.getY());
		} while(move.isValid() == false);

		return move;
	}

	
	
	public boolean isValid(){
		Location loc = this.getLoc();
		String dir;
		Location newLoc;
		int[] dirp;
		Location blockLoc;

		dir = this.getDir();
		newLoc = Location.getNewLoc(this);

		if(newLoc.occupiedByPlayer()){
			return false;
		}

		if(newLoc.isOutOfBounds(-1)){
			return false;
		}

		if(newLoc.occupiedByLenOneBlock()){
			dirp = Main.getDirPair(dir);
			blockLoc = new Location(loc.getX() + dirp[0], loc.getY() + dirp[1]);
			Move blockMove = new Move(dir, blockLoc);
			Location newBlockLoc = Location.getNewLoc(blockMove);
			if(newBlockLoc.isOutOfBounds(1)){
				return false;
			}
		}

		return true;
	}
	
	public String getDir() {
		return dir;
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public Location getLoc(){
		return loc;
	}
	
}
