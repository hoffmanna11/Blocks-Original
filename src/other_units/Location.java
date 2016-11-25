package other_units;

import main.Main;

public class Location implements Cloneable {
	int x;
	int y;

	public Location(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean occupiedByPlayer(){
		for(int i=0; i<Main.players.size(); i++){
			if(Main.players.get(i).getLoc().equals(this)){
				return true;
			}
		}
		return false;
	}
	
	// Future player loc
	public boolean occupiedByLenOneBlock(){
		Location temp;
		for(int i=0; i<Main.blocks.size(); i++){
			if(Main.blocks.get(i).getLength() == 1){
				temp = new Location(Main.blocks.get(i).getLoc().getX(), Main.blocks.get(i).getLoc().getY());
				if(temp.equals(this)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean isOutOfBounds(int length){
		boolean isPlayer;
		if(length == -1){
			isPlayer = true;
		}else{
			isPlayer = false;
		}
		if(isPlayer){
			if( (this.getX() < 0)          || 
					(this.getX() >= Main.gridWidth) ||
					(this.getY() < 0)          ||
					(this.getY() >= Main.gridHeight) ){
				return true;
			}
			return false;
		}else{
			for(int i=0; i<length; i++){
				if( ((this.getX()+i) < 0)          || 
						((this.getX()+i) >= Main.gridWidth) ||
						(this.getY() < 0)          ||
						(this.getY() >= Main.gridHeight) ){
					return true;
				}
			}
			return false;
		}
	}

	public static Location getNewLoc(Move move){
		String dir = move.getDir();
		int [] dirPair = Main.getDirPair(dir);

		Location newLoc;

		int x = move.getLoc().getX() + dirPair[0];
		int y = move.getLoc().getY() + dirPair[1];

		newLoc = new Location(x, y);

		return newLoc;
	}

	public void setLoc(int x, int y){
		this.x = x;
		this.y = y;
	}

	public boolean equals(Location loc){
		if((this.x == loc.getX()) &&
				(this.y == loc.getY())){
			return true;
		}return false;
	}

	public boolean inRewardState(){
		if(this.y == 0){
			return true;
		}else{
			return false;
		}
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}
}
