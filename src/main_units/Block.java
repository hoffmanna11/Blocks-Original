package main_units;
import java.awt.Color;

import java.util.List;

import main.Main;
import other_units.LastMoves;
import other_units.Location;

@SuppressWarnings("serial")
public class Block extends Main {
	Location loc;
	Location origLoc;
	int length;
	int blockIndex;

	public Block(int blockIndex, int x, int y, int length, Location origLoc){
		this.loc = new Location(x, y);
		this.length = length;
		this.blockIndex = blockIndex;
		for(int i=0; i<length; i++){
			cells.getCell(x+i,y).setBlockIndex(blockIndex);
		}
		this.origLoc = origLoc;
	}

	public void setLoc(Location newLoc, List<Integer> playerIndices, List<LastMoves> lastMoves){
		// update cells
		cells.updateCellsBlockStatus(this.getLoc().getX(), this.getLoc().getY(), newLoc.getX(), newLoc.getY(), this.getBlockIndex(), this.getLength());

		Location currentLoc = this.loc;
		if(playerIndices != null){
			// If block was moved upwards
			if((currentLoc.getY()-1) == newLoc.getY()){
				for(int i=0; i<playerIndices.size(); i++){
					rewardBoolList.set(playerIndices.get(i), true);
					//System.out.print(playerIndices.get(i) + " ");
				}
				// If block was moved downwards
			}else if((currentLoc.getY()+1) == newLoc.getY()){
				// Negative reinforcement
				for(int i=0; i<playerIndices.size(); i++){
					punishBoolList.set(playerIndices.get(i), true);
					//System.out.print(playerIndices.get(i) + " ");
				}
			}
		}

		// undraw old one
		drawBlock(this.loc.getX(), this.loc.getY(), length, Color.WHITE);

		this.loc = newLoc;
		drawBlock(this.loc.getX(), this.loc.getY(), length, Color.CYAN);
	}

	public static boolean blocksInaccessible(){
		int length;
		int x, y;

		// Go through each block, check against every other block to see if there is a blockage
		// If every block is either blocked by another block or is at the top or bottom of the
		// grid, then the blocks are inaccessible
		// Return false if you find a single block that is accessible
		for(int i=0; i<blocks.size(); i++){
			x = blocks.get(i).getLoc().getX();
			y = blocks.get(i).getLoc().getY();
			length = blocks.get(i).getLength();
			boolean isAccessible = true;
			for(int l=0; l<length; l++){
				for(int j=0; j<blocks.size(); j++){
					// If one block is accessible, return false

					// If block j is directly below one of the segments of block i, block i is inaccessible
					if( (x+l == blocks.get(j).getLoc().getX()) && ((y-1) == blocks.get(j).getLoc().getY()) ){
						//System.out.println("Block[" + i + "] (" + x + "," + y + "), exit at 1");
						isAccessible = false;
					}
					// If block j is directly above one of the segments of block i, block i is inaccessible
					if( (x+l == blocks.get(j).getLoc().getX()) && ((y+1) == blocks.get(j).getLoc().getY()) ){
						//System.out.println("Block[" + i + "] (" + x + "," + y + "), exit at 2");
						isAccessible = false;
					}
					// If block i is at the top or bottom, block i is inaccessible
					if((y == 0) || (y == (Main.gridHeight-1))){
						//System.out.println("block[" + i + "] (" + x + "," + y + "), exit at 3");
						isAccessible = false;
					}
				}
			}
			// If one block i is accessible, the one block is accessible, therefore blocksInaccessible is false
			if(isAccessible == true){
				//System.out.println("Block[" + i + "] loc: " + x + "," + y + " is accessible, returning");
				//System.out.println("---------------------------------------");
				return false;
			}
		}
		// If none of the blocks are accessible, return true
		return true;
	}//static boolean blocksInaccessible()

	/* Getters and setters */
	public Location getOrigLoc() {
		return origLoc;
	}

	public void setOrigLoc(Location origLoc) {
		this.origLoc = origLoc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setBlockIndex(int blockIndex) {
		this.blockIndex = blockIndex;
	}

	public Location getLoc(){
		return loc;
	}

	public int getBlockIndex(){
		return this.blockIndex;
	}

	public int getLength(){
		return length;
	}
}
