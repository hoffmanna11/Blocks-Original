package main_units;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import main.Main;
import other.DrawingComponent;
import other_units.Location;

@SuppressWarnings("serial")
public class Cell extends Main{
	public static double overallHighestWeight;
	public static double overallLowestWeight;
	public static double nullWeight = -1;
	public static ArrayList<Double> weights;

	/* the weights assigned to each action */
	private double up;
	private double down;
	private double left;
	private double right;

	String localHighestWeightDir = "-";
	double localHighestWeight = nullWeight;
	boolean changed = false;

	/* x,y coordinate of cell */
	Location loc;

	/* denote which player/block is currently on this cell
	 * will be -1 if no player/block is on this cell */
	int playerIndex;
	int blockIndex;

	public Cell(Cell cell){
		this.loc = cell.loc;
		this.up = cell.up;
		this.down = cell.down;
		this.left = cell.left;
		this.right = cell.right;
		playerIndex = cell.playerIndex;
		blockIndex = cell.blockIndex;
		localHighestWeightDir = cell.localHighestWeightDir;
	}

	public Cell(int x, int y) {
		this.loc = new Location(x, y);
		this.up = nullWeight;
		this.down = nullWeight;
		this.left = nullWeight;
		this.right = nullWeight;
		playerIndex = -1;
		blockIndex = -1;
	}

	/* updates the directional weight of the passed in direction by adding the reward */
	public void addReward(String dir, double reward){
		if(changed == false){
			if(reward != 0){
				changed = true;
				nonZeroCells++;
			}
		}
		switch(dir){
		case "up":
			updateNonZero(up,reward);
			if(this.up == nullWeight){
				this.up = reward;
			}else{
				this.up += reward;
			}
			//System.out.println("Adding reward: " + reward + " to up, new result: " + up);
			if(up > overallHighestWeight) overallHighestWeight = up;
			if(up < overallLowestWeight) overallLowestWeight = up;
			break;
		case "down":
			updateNonZero(down,reward);
			if(this.down == nullWeight){
				this.down = reward;
			}else{
				this.down += reward;
			}
			//System.out.println("Adding reward: " + reward + " to up, new result: " + down);
			if(down > overallHighestWeight) overallHighestWeight = down;
			if(down < overallLowestWeight) overallLowestWeight = down;
			break;
		case "left":
			updateNonZero(left,reward);
			if(this.left == nullWeight){
				this.left = reward;
			}else{
				this.left += reward;
			}
			//System.out.println("Adding reward: " + reward + " to up, new result: " + left);
			if(left > overallHighestWeight) overallHighestWeight = left;
			if(left < overallLowestWeight) overallLowestWeight = left;
			break;
		case "right":
			updateNonZero(right,reward);
			if(this.right == nullWeight){
				this.right = reward;
			}else{
				this.right += reward;
			}
			//System.out.println("Adding reward: " + reward + " to up, new result: " + right);
			if(right > overallHighestWeight) overallHighestWeight = right;
			if(right < overallLowestWeight) overallLowestWeight = right;
			break;
		}
		updateLocalHighestWeightAndDir();
		weightSum += reward;
		if(nonZeroCells > 0) {
			//topPercentile = (weightSum / nonZeroCells);
			//System.out.println("topPecentile: " + topPercentile + " = " + overallLowestWeight + " + " + percentile + " * abs( " + overallHighestWeight + " - " + overallLowestWeight + " )");
			//topPercentile = (overallLowestWeight + (percentile * Math.abs(overallHighestWeight - overallLowestWeight)));
			setTopPercentile();
			DrawingComponent.topPercentileT.setText(Double.toString(Math.round(topPercentile)));
		}
	}

	public static void setTopPercentile(){
		setWeights();
		sortWeights();
		// If the percentile is near 1, then the following statement will be true
		if( Math.abs( (percentile * weights.size()) - weights.size() ) < 1) {
			// If so, set this to the maximum percentile, i.e. the highest weight in the cell matrix
			topPercentile = weights.get(weights.size()-1);
		}else{
			topPercentile = weights.get((int) (percentile * (double)(weights.size())));
		}
	}

	public static void setWeights(){
		weights = new ArrayList<Double>();
		for(int i=0; i<cells.size(); i++){
			for(int j=0; j<cells.getRow(0).size(); j++){
				double weight = cells.getCell(i, j).getLocalHighestWeight();
				if(weight != nullWeight){
					weights.add(weight);
				}
			}
		}
		if(weights.size() == 0){
			weights.add(nullWeight);
		}
	}

	public static void sortWeights(){
		Collections.sort(weights);
	}

	public void setWeightsOld(){
		/*
		for(int i=0; i<cells.size(); i++){
			for(int j=0; j<cells.getRow(0).size(); j++){
				weights[i*cells.getRow(0).size() + j] = cells.getCell(i, j).getLocalHighestWeight();
			}
		}
		 */
	}

	public void sortWeightsOld(){
		/*
		System.out.println(Arrays.toString(weights));
		int colSize = cells.getRow(0).size();
		for(int i=0; i<cells.size(); i++){
			for(int j=0; j<cells.getRow(0).size()-1; j++){
				for(int i2=0; i2<cells.size(); i2++){
					for(int j2=0; j2<cells.getRow(0).size()-1; j2++){
						//System.out.println("weight " + (i2*colSize + j2));
						boolean thing = false;
						if(!((weights[i2*colSize + j2] == nullWeight) && (weights[i2*colSize + j2+1] == nullWeight))){
							thing = true;
						}
						if(thing == true){
							System.out.println("Comparing " + weights[i2*colSize + j2] + " and " + weights[i2*colSize + j2 + 1]);
						}
						if((weights[i2*colSize + j2 + 1] == nullWeight) || (weights[i2*colSize + j2] > weights[i2*colSize + j2 +1])){
							swapWeights(i2*colSize + j2, i2*colSize + j2+1);
							if(thing) System.out.println("Swapped!");
						}else{
							if(thing) System.out.println("Not swapped!");
						}
						if(thing) System.out.println("-------------------------");
					}
				}
			}
		}
		 */
	}

	public void updateLocalHighestWeightAndDir(){
		localHighestWeight = up; localHighestWeightDir = "up";
		if(down > localHighestWeight){ localHighestWeight = down; localHighestWeightDir = "down"; }
		if(left > localHighestWeight){ localHighestWeight = left; localHighestWeightDir = "left"; }
		if(right > localHighestWeight){ localHighestWeight = right; localHighestWeightDir = "right"; }
		if(localHighestWeight == nullWeight){
			// Then there is no highest weight
			localHighestWeight = nullWeight;
			localHighestWeightDir = "-";
		}
	}

	public void updateNonZero(double weight, double reward){
		/*
		if(weight == nullWeight && reward != 0){
			nonZeroCells++;
		}else{
			if((weight+reward) == nullWeight){
				nonZeroCells--;
			}
		}
		 */
	}

	public boolean isInhabited(){
		if((playerIndex != -1) || (blockIndex != -1)){
			return true;
		}else{
			return false;
		}
	}

	public boolean hasPlayer(){
		return playerIndex != -1;
	}

	public boolean hasBlock(){
		return blockIndex != -1;
	}

	public static double getOverallHighestWeight() {
		return overallHighestWeight;
	}

	public static void setOverallHighestWeight(double overallHighestWeight) {
		Cell.overallHighestWeight = overallHighestWeight;
	}

	public String getLocalHighestWeightDir() {
		return localHighestWeightDir;
	}

	public void setLocalHighestWeightDir(String localHighestWeightDir) {
		this.localHighestWeightDir = localHighestWeightDir;
	}

	public double getLocalHighestWeight() {
		return localHighestWeight;
	}

	public void setLocalHighestWeight(double localHighestWeight) {
		this.localHighestWeight = localHighestWeight;
	}

	public void setUp(double up) {
		this.up = up;
	}

	public void setDown(double down) {
		this.down = down;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public void setRight(double right) {
		this.right = right;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	/* returns the weight associated with the passed in direction */
	public double getReward(String dir){
		switch(dir){
		case "up":
			return up;
		case "down":
			return down;
		case "left":
			return left;
		case "right":
			return right;
		}
		System.out.println("Error, Cell: getReward");
		System.exit(0);
		return -1;
	}

	/* getters and setters */
	public double getLeft() {
		return left;
	}

	public double getRight() {
		return right;
	}

	public int getPlayerIndex(){
		return this.playerIndex;
	}

	public int getBlockIndex(){
		return this.blockIndex;
	}

	public void setPlayerIndex(int playerIndex){
		this.playerIndex = playerIndex;
	}

	public void setBlockIndex(int blockIndex){
		this.blockIndex = blockIndex;
	}

	public Location getLoc(){
		return this.loc;
	}

	public double getUp() {
		return up;
	}

	public double getDown() {
		return down;
	}

}
