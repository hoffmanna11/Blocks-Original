package main_units;
import java.util.ArrayList;
import java.util.List;

import main.Main;
import other_units.Location;

public class CellMatrix {
	int width;
	int height;
	List<List<Cell>> cells;
	
	public CellMatrix(int width, int height){
		this.width = width;
		this.height = height;
		cells = new ArrayList<List<Cell>>();
		
		// Cells sometimes fail to draw correctly without the holdup
		Main.holdup(20);

		for(int i=0; i<width; i++){
			cells.add(new ArrayList<Cell>());
			for(int j=0; j<height; j++){
				cells.get(i).add(new Cell(i,j));
			}
		}
	}

	/* Check if this is a valid location for creating a new player/block */
	public boolean isValidGenLoc(int x, int y, int length){
		boolean isPlayer;
		if(length == -1){
			isPlayer = true;
		}else{
			isPlayer = false;
		}

		// Check if positions overlap with other players or blocks
		if(isPlayer){
			// If a block is there, return false
			if(this.getCell(x,y).getBlockIndex() != -1){
				return false;
			}
			// If a player is there, return false
			if(this.getCell(x,y).getPlayerIndex() != -1){
				return false;
			}

			// Since we're checking to see if a BLOCK will fit in this location, 
			// Return false if:
		}else{
			for(int i=0; i<length; i++){
				// There is a player in this location
				if(this.getCell(x+i,y).getPlayerIndex() != -1){
					return false;
				}
				// A block is already placed in this location
				if(this.getCell(x+i,y).getBlockIndex() != -1){
					return false;
				}
				// There is a block directly above
				if(this.getCell(x+i,y+1).getBlockIndex() != -1){
					return false;
				}
				// There is a block directly below
				if(this.getCell(x+i,y-1).getBlockIndex() != -1){
					return false;
				}
			}
		}
		return true;
	}
	
	/* Check if future block location is free */
	public boolean blockLocIsFree(int blockLength, Location newBlockLoc){
		int x, y;
		y = newBlockLoc.getY();
		for(int i=0; i<blockLength; i++){
			x = newBlockLoc.getX() + i;
			if((this.getCell(x,y).getPlayerIndex() != -1) || 
					(this.getCell(x,y).getBlockIndex() != -1)){
				return false;
			}
		}
		return true;
	}
	
	/* when a player/block moves to another cell, these methods are used to update their position in the cell matrix */
	public void updateCellsPlayerStatus(int oldX, int oldY, int newX, int newY, int index){
		getCell(oldX,oldY).setPlayerIndex(-1);
		getCell(newX,newY).setPlayerIndex(index);
	}

	public void updateCellsBlockStatus(int oldX, int oldY, int newX, int newY, int index, int length){
		for(int i=0; i<length; i++){
			getCell(oldX+i,oldY).setBlockIndex(-1);
			getCell(newX+i,newY).setBlockIndex(index);
		}
	}
	
	/* getters and setters */
	public List<Cell> getRow(int i){
		return cells.get(i);
	}
	
	public Cell getCell(int x, int y){
		//if(x > 7 || y > 7){
		//	System.out.println("x: " + x + ", y: " + y);
		//}
		
		return this.cells.get(x).get(y);
	}

	public Cell getCell(Location loc){
		return this.cells.get(loc.getX()).get(loc.getY());
	}
	
	public int size(){
		return cells.size();
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
