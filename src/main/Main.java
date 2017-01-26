package main;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import main_units.Block;
import main_units.Cell;
import main_units.CellMatrix;
import main_units.Player;
import other.DrawingComponent;
import other.Printing;
import other.Training;
import other._Math;
import other_units.LastMoves;
import other_units.Location;
import other_units.Move;

//import org.apache.commons.math3.fitting.PolynomialCurveFitter;
//import org.apache.commons.math3.fitting.WeightedObservedPoints;

@SuppressWarnings("serial")
public class Main extends DrawingComponent {
	/* delay between drawing the moves of players */
	public static int delay = 100;
	/* Units */
	public static List<Player> players;
	public static List<Block> blocks;
	public static CellMatrix cells;
	public static List<LastMoves> lastMoves;
	/* Directional pairs */
	static final int[] up = {0,-1};
	static final int[] down = {0,1};
	static final int[] left = {-1,0};
	static final int[] right = {1,0};
	/* Configuration variables */
	static final int lastMovesNum = 8;
	static final int numberOfPlayers = 2;
	static final int numberOfBlocks = 4;
	static final int maxBlockLength = 2;
	public static final int gridWidth = 6;
	public static final int gridHeight = 6;
	/* Training variables */
	public static boolean training = true;
	public static boolean useBasicWeights = false;
	/* Weighting the nodes */
	public static double topPercentile = 0;
	public static double weightSum = 0;
	public static int nonZeroCells = 0;
	public static double percentile = .60;
	public static double rewardDepreciation = 1;
	/* Keeps track of which players were successful in the past round */
	public static List<Boolean> rewardBoolList = new ArrayList<>();
	public static List<Boolean> punishBoolList = new ArrayList<>();
	/* Number of cycles it takes to reset the board */
	public static int resetCycles = 50;
	/* Default number of cycles to run */
	public static int cycles = 500000;
	/* Variable that tracks if cycling is paused */
	public static boolean paused = false;
	/* Stops cycling */
	public static boolean stopped = true;
	/* Formatter for two-precision doubles */
	public static DecimalFormat df = new DecimalFormat("#.##");
	/* Stores if we've run the thread yet */
	public static boolean firstRun = true;
	/* For when user clicks the Reset button */
	public static boolean killThread = false;
	/* Reward players only when block reaches the top spot */
	public static boolean rewardOnlyAtTop = true;

	public static void main(String args[]){
		initAll();
		generateUnits();
		resetUnitLocations();
		//resetWeights();
	}

	public static void startThread(){
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				Main.whileThing(Integer.parseInt(startT.getText()));
			}
		});
		t1.start();
	}

	public static void whileThing(int cyclesToRun){
		// Set method variables
		// Number of cycles that the program will run
		cycles = cyclesToRun;
		// List that tracks the player indices rewarded
		List<Integer> rewardIndices;
		List<Integer> punishIndices;
		// Object that stores a list of training/bookkeeping variables
		Training t = new Training();
		// Reset last moves to blank slate
		lastMoves = new ArrayList<LastMoves>(numberOfPlayers);

		// Move all units to their original locations
		resetUnitLocations();

		for(int i=1; i<=cycles; i++){
			// Checks if the user has paused execution
			// If so, the thread will periodically sleep + check if unpaused until unpaused
			checkForAndImplementPause();
			// If user has clicked the stop button, stop this thread, break for good measure
			if(stopped){
				Thread.currentThread().interrupt();
				break;
			}
			// Redraw the cycle count
			DrawingComponent.redrawCycles(i, cycles);
			// Reset the unit locations if needed
			if(i % resetCycles == 0){
				resetUnitLocations();
				//holdup(500);
				// Blank slate last moves since unit locations reset
				lastMoves = new ArrayList<LastMoves>(numberOfPlayers);
			}
			// If blocks inaccessible, reset block and player positions to original
			if(Block.blocksInaccessible()){
				resetUnitLocations();
				// Set lastMoves to a blank slate since we're resetting player positions
				lastMoves = new ArrayList<LastMoves>(numberOfPlayers);
			}
			// Sets these boolean arrays to all false
			rewardBoolList = resetReward(rewardBoolList);
			punishBoolList = resetReward(punishBoolList);

			// * Moves the players, and updates the lastMoves object *
			lastMoves = move(players, blocks, lastMoves);
			// Players.move(cells, blocks, training, useBasicWeights, topPercentile);

			// Now that everything has moved, check if any players were rewarded/punished
			rewardIndices = checkIfReward(rewardBoolList);
			punishIndices = checkIfPunish(punishBoolList);

			// If these indices aren't empty, then there are cell weights to reward/punish
			if(!( (rewardIndices.isEmpty()) && (punishIndices.isEmpty()) )){
				// Update cell values
				updateCellWeights(rewardIndices, punishIndices, lastMoves);
				t.updateValues(i);
				Printing.printRewardInformation(t, i);
				Printing.printCells();
			}
			t.totalCycles++;
			// Sleep for the delay value in milliseconds
			holdup(delay);
		} // End of for(i<cycles)
		stopB.setEnabled(false);
		startB.setText("Start");
		stopped = true;
	}

	public static List<LastMoves> move(List<Player> ps_, List<Block> blocks_, List<LastMoves> last_Moves_){
		List<Player> ps = new ArrayList<>(ps_);
		List<LastMoves> last_Moves;
		// If the clone of the passed in list of last moves (one lastMove object for each player) is empty, then blank slate it 
		if(last_Moves_.isEmpty()){
			last_Moves = new ArrayList<>(players.size());
			for(int i=0; i<players.size(); i++){
				last_Moves.add(new LastMoves(lastMovesNum));
			}
		}else{
			last_Moves = new ArrayList<>(last_Moves_);
		}

		ArrayList<Move> playerMoves = new ArrayList<>(ps.size());
		ArrayList<Location> playerLocs = new ArrayList<>(ps.size());

		int x, y;
		double greatest = 0;
		String dir;
		for(int i=0; i<ps.size(); i++){
			// Training mode sets it so that each move is random
			if(training == true){
				playerMoves.add(Move.getRandMove(ps.get(i).getLoc()));
			}else{
				// Use the weights on the cell grid, according to whichever
				// weight schema is being used (basic weights or those that
				// are above a certain percentile)
				greatest = 0;
				dir = "";
				x = ps.get(i).getLoc().getX();
				y = ps.get(i).getLoc().getY();

				Cell tempCell = cells.getCell(x, y);
				greatest = tempCell.getLocalHighestWeight();
				dir = tempCell.getLocalHighestWeightDir();

				// If non-null, i.e. if reward has been placed
				//if( ! (dir.isEmpty())){ //OLDWAY//
				if (tempCell.getLocalHighestWeight() != Cell.nullWeight){ //NEWWAY//
					// Choose move based on weights
					Move move = new Move(dir, x, y);

					// Check if move is in the top percentile and if valid
					boolean condition = false;
					if(useBasicWeights == true){
						condition = true;
					}else{
						condition = (greatest > topPercentile);
					}

					if((move.isValid()) && condition){
						//System.out.println("top: " + ++top + ", " + "noTop: " + noTop);
						//System.out.println("In top percentile, adding weighted move");
						//System.out.println("Best move for P" + ps.get(i).getPlayerIndex() + ": dir: " + dir);
						playerMoves.add(move);
					}else{
						if((move.isValid()) && (!(greatest > topPercentile))){
							//System.out.println("top: " + top + ", " + "noTop: " + ++noTop);
							//System.out.println("Not in top percentile, adding random move");
						}
						move = Move.getRandMove(ps.get(i).getLoc());
						playerMoves.add(move);
					}

				}else{
					// No weights available, generate random move
					playerMoves.add(Move.getRandMove(ps.get(i).getLoc()));
				}
			}
			playerLocs.add(Location.getNewLoc(playerMoves.get(i)) );
		}

		int locStatus;
		int playerIndex;
		for(int i=0; i<players.size(); i++){
			last_Moves.get(i).addMove(playerMoves.get(i));
		}

		while( ! (ps.isEmpty()) ){
			locStatus = getLocStatus(players, playerLocs.get(0));
			playerIndex = ps.get(0).getPlayerIndex();

			switch(locStatus){
			// New player location is uninhabited, move there
			case -1:
				players.get(playerIndex).setLoc(playerLocs.get(0));
				ps.remove(0);
				playerMoves.remove(0);
				playerLocs.remove(0);
				break;
				// New player location has another player, don't move
			case -2:
				players.get(playerIndex).setLoc(ps.get(0).getLoc());

				ps.remove(0);
				playerMoves.remove(0);
				playerLocs.remove(0);
				break;
				// Out of bounds, don't move
			case -3:
				players.get(playerIndex).setLoc(ps.get(0).getLoc());
				ps.remove(0);
				playerMoves.remove(0);
				playerLocs.remove(0);
				break;
				// New player location has a block
			default:
				ArrayList<Integer> playerIndices = canMoveBlock(ps, playerMoves, playerLocs, locStatus, last_Moves);

				// Block cannot be moved
				if(null == playerIndices){
					ps.remove(0);
					playerMoves.remove(0);
					playerLocs.remove(0);
					continue;
				}else if(playerIndices.get(0) == -1){
				}else{
					// Do comment
				}
				break;
			}
		}
		return last_Moves;
	}

	public static void checkForAndImplementPause(){
		if(paused){
			startB.setEnabled(true);
			while(paused){
				if(stopped == true){
					break;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void resetUnitLocations(){
		for(int j=0; j<players.size(); j++){
			players.get(j).setLoc(players.get(j).getOrigLoc());
		}
		for(int j=0; j<blocks.size(); j++){
			blocks.get(j).setLoc(blocks.get(j).getOrigLoc(), null, null);
		}
	}

	public static void initAll(){
		/* Initialization process */

		// Initializes the JFrame
		DrawingComponent.initFrame();

		// Draws in whitespace for each cell
		drawInit();

		// Draws the initial cycle count
		g.setColor(Color.black);
		g.drawString("Cycle: " + 0 + " of " + cycles, 10,45);

		// Create 2d array list of all cells
		cells = new CellMatrix(gridWidth, gridHeight);

		// Create players, blocks, and save their initial locations for resetting
		players = new ArrayList<Player>(numberOfPlayers);
		blocks = new ArrayList<Block>(numberOfBlocks);

		// Draw the generated players and blocks
		d.drawPlayers(players);
		d.drawBlocks(blocks);

		//holdup(100); //!TEMP//

		// Create the last moves list for each player
		lastMoves = new ArrayList<LastMoves>(numberOfPlayers);

		// Initialize the global boolean list for tracking if players achieved a reward
		rewardBoolList = new ArrayList<Boolean>(numberOfPlayers);
		// Initialize list to false
		for(int i=0; i<numberOfPlayers; i++){
			rewardBoolList.add(false);
		}

		// Initialize the global boolean list for tracking if players achieved a reward
		punishBoolList = new ArrayList<Boolean>(numberOfPlayers);
		// Initialize list to false
		for(int i=0; i<numberOfPlayers; i++){
			punishBoolList.add(false);
		}
	}

	public static void generateUnits(){
		/* Generates random players and blocks
		 * Makes sure that blocks can't get stuck by being stacked on top of each other
		 * Or start out in other unusable locations (top and bottom of board) */
		int x, y;
		Boolean isValid;
		for(int i=0; i<numberOfPlayers; i++){
			x = _Math.random(0,gridWidth-1);
			y = _Math.random(0,gridHeight-1);

			isValid = cells.isValidGenLoc(x,y,-1);

			while(isValid == false){
				x = _Math.random(0,gridWidth-1);
				y = _Math.random(0,gridHeight-1);
				isValid = cells.isValidGenLoc(x,y,-1);
			}
			players.add(new Player(i, x, y, lastMovesNum, new Location(x,y)));
		}

		int length;
		for(int i=0; i<numberOfBlocks; i++){
			length = _Math.random(1,maxBlockLength);
			x = _Math.random(0,gridWidth-length);
			// Don't want blocks on top or bottom (can't move vertically, i.e. can't get reward)
			y = _Math.random(1,gridHeight-2);
			isValid = cells.isValidGenLoc(x,y,length);

			while(isValid == false){
				length = _Math.random(1,maxBlockLength);
				x = _Math.random(0,gridWidth-length);
				y = _Math.random(1,gridHeight-2);
				isValid = cells.isValidGenLoc(x,y,length);
			}
			blocks.add(new Block(i, x, y, length, new Location(x,y)));
		}
	}

	static void updateCellWeights(List<Integer> rewardIndices, List<Integer> punishIndices, List<LastMoves> lm){
		int x, y, index, size;
		double reward;
		String dir;
		size = lm.get(0).getCurrentSize();

		for(int i=0; i<rewardIndices.size(); i++){
			index = rewardIndices.get(i);
			reward = 0;
			
			Location playerLoc = players.get(index).getLoc();
			int blockX = playerLoc.getX();
			int blockY = playerLoc.getY() - 1;

			int blockIndex = cells.getCell(blockX, blockY).getBlockIndex();
			if(blockIndex == -1){
				System.out.println("Block index failed, stopping [Main.updateCellWeights,reward]");
				System.out.println("Player loc: " + playerLoc.getX() + ", " + playerLoc.getY() + ", lastmove: " + lm.get(index).getMove(lm.get(index).currentSize-1).getDir() + ", " + lm.get(index).getMove(lm.get(index).currentSize-1).getLoc().getX() + ", " + lm.get(index).getMove(lm.get(index).currentSize-1).getLoc().getY());
				System.out.println("Block loc: " + blockX + ", " + blockY);
				continue;
				//Main.paused = true;
			}else{
				int blockLength = blocks.get(blockIndex).getLength();
				reward = 10 * Math.pow(blockLength, 3);
			}
			
			if(rewardOnlyAtTop){
				if(blockY != 0){
					reward = 0;
				}
			}

			for(int j=size-1; j>=0; j--){
				x = lm.get(index).getMove(j).getLoc().getX();
				y = lm.get(index).getMove(j).getLoc().getY();
				dir = lm.get(index).getMove(j).getDir();
				// Get the highest weight beforehand
				// Cell oldCell = new Cell(cells.getCell(x, y)); //FIX//
				String prevWeight = cells.getCell(x, y).getLocalHighestWeightDir();
				cells.getCell(x,y).addReward(dir, reward);
				if(! (prevWeight.equals(cells.getCell(x, y).getLocalHighestWeightDir()) )){
					// need to update this
					// need to redraw the old cell, then redraw the diagonal
					redrawCell(cells.getCell(x, y));
					drawDirectional(cells.getCell(x, y));
				}
				reward *= rewardDepreciation;
			}
		}

		for(int i=0; i<punishIndices.size(); i++){
			index = punishIndices.get(i);
			
			Location playerLoc = players.get(index).getLoc();
			int blockX = playerLoc.getX();
			int blockY = playerLoc.getY() + 1;

			reward = 0;
			
			int blockIndex = cells.getCell(blockX, blockY).getBlockIndex();
			if(blockIndex == -1){
				System.out.println("Block index failed, stopping [Main.updateCellWeights,punish]");
				System.out.println("Player loc: " + playerLoc.getX() + ", " + playerLoc.getY() + ", lastmove: " + lm.get(index).getMove(lm.get(index).currentSize-1).getDir() + ", " + lm.get(index).getMove(lm.get(index).currentSize-1).getLoc().getX() + ", " + lm.get(index).getMove(lm.get(index).currentSize-1).getLoc().getY());
				System.out.println("Block loc: " + blockX + ", " + blockY);
				continue;
				//Main.paused = true;
			}else{
				int blockLength = blocks.get(blockIndex).getLength();
				reward = -10; // * Math.pow(blockLength, 2);
			}
			
			for(int j=size-1; j>=0; j--){
				x = lm.get(index).getMove(j).getLoc().getX();
				y = lm.get(index).getMove(j).getLoc().getY();
				dir = lm.get(index).getMove(j).getDir();
				String prevWeight = cells.getCell(x, y).getLocalHighestWeightDir();
				cells.getCell(x,y).addReward(dir, reward);
				if(! (prevWeight.equals(cells.getCell(x, y).getLocalHighestWeightDir()) )){
					// need to update this
					// need to redraw the old cell, then redraw the diagonal
					redrawCell(cells.getCell(x, y));
					drawDirectional(cells.getCell(x, y));
				}
				reward *= rewardDepreciation;
			}
		}
	}

	static List<Integer> checkIfReward(List<Boolean> rewardList){
		ArrayList<Integer> indices = new ArrayList<>();
		for(int i=0; i<rewardList.size(); i++){
			if(rewardList.get(i) == true){
				indices.add(i);
			}
		}
		return indices;
	}

	static List<Integer> checkIfPunish(List<Boolean> punishList){
		ArrayList<Integer> indices = new ArrayList<>();
		for(int i=0; i<punishList.size(); i++){
			if(punishList.get(i) == true){
				indices.add(i);
			}
		}
		return indices;
	}

	static List<Boolean> resetReward(List<Boolean> rewardList){
		for(int i=0; i<rewardList.size(); i++){
			rewardList.set(i,false);
		}
		return rewardList;
	}

	static ArrayList<Integer> canMoveBlock(List<Player> ps_, List<Move> playerMoves_, List<Location> playerLocs_, int blockIndex, List<LastMoves> lastMoves){
		/* If it can move, returns the list of indices of the players that are moving the block
		 * If can't move block, returns null */

		List<Player> ps = new ArrayList<>(ps_);
		List<Move> playerMoves = new ArrayList<>(playerMoves_);
		List<Location> playerLocs = new ArrayList<>(playerLocs_);

		// First check if the move is valid
		Move move = new Move(playerMoves.get(0).getDir(), blocks.get(blockIndex).getLoc());
		Location newBlockLoc = Location.getNewLoc(move);
		if(newBlockLoc.isOutOfBounds(blocks.get(blockIndex).getLength())){
			return null;
		}

		// Now check if the new location of the block move is available
		if(cells.blockLocIsFree(blocks.get(blockIndex).getLength(), newBlockLoc)){
			// Since the space is available, check if there are players in position to move the block
			if(blocks.get(blockIndex).getLength() == 1){
				ArrayList<Integer> eh = new ArrayList<>();
				eh.add(ps.get(0).getPlayerIndex());
				blocks.get(blockIndex).setLoc(newBlockLoc, eh, lastMoves);
				players.get(ps.get(0).getPlayerIndex()).setLoc(playerLocs.get(0));
				ArrayList<Integer> hi = new ArrayList<>();
				hi.add(-1);
				return hi;
			}else{
				String dir = playerMoves.get(0).getDir();
				int dirNum;
				if(dir.equals("up")){
					dirNum = 1;
				}else{
					dirNum = -1;
				}

				int x, y;
				x = blocks.get(blockIndex).getLoc().getX();
				y = blocks.get(blockIndex).getLoc().getY() + dirNum;
				boolean check;
				ArrayList<Integer> hi = new ArrayList<>();
				for(int i=0; i<blocks.get(blockIndex).getLength(); i++){
					x += i;
					check = false;
					for(int j=0; j<ps.size(); j++){
						if((x == ps.get(j).getLoc().getX()) && (y == ps.get(j).getLoc().getY()) ){
							if(playerMoves.get(j).getDir().equals(dir)){
								check = true;
								hi.add(ps.get(j).getPlayerIndex());
							}
						}
					}
					if(!(check)){ return null; }
				}
				blocks.get(blockIndex).setLoc(newBlockLoc, hi, lastMoves);
				for(int i=0; i<hi.size(); i++){
					players.get(hi.get(i)).setLoc(new Location(players.get(hi.get(i)).getLoc().getX(), players.get(hi.get(i)).getLoc().getY()+(dirNum*-1)));
				}
				return hi;
			}
		}else{
			return null;
		}
	}

	static int getLocStatus(List<Player> ps_, Location loc){
		/* Returns -1 if uninhabited
		 * Returns -2 if occupied by another player
		 * Returns index of block in block array if inhabited by a block */

		// -3 for out of bounds
		if(loc.isOutOfBounds(-1)){
			return -3;
		}
		List<Player> ps = new ArrayList<>(ps_);
		// Check if occupied by another player
		for(int i=0; i<ps.size(); i++){
			if(ps.get(i).getLoc().equals(loc)){
				return -2;
			}
		}
		// Check if occupied by a block
		Location temp;
		for(int i=0; i<blocks.size(); i++){
			for(int j=0; j<blocks.get(i).getLength(); j++){
				temp = new Location(blocks.get(i).getLoc().getX()+j, blocks.get(i).getLoc().getY());
				if(temp.equals(loc)){
					return i;
				}
			}
		}
		// -1 for uninhabited
		return -1;
	}

	public static int[] getDirPair(String dir){
		switch(dir){
		case "up"   : return up;
		case "down" : return down;
		case "left" : return left;
		case "right": return right;
		default:
			System.out.println("Error, method getMove");
			System.exit(-1);
			return null;
		}
	}

	public static String getRandDir(){
		Random rand = new Random();
		int randNum = rand.nextInt(3+1);
		switch(randNum) {
		case 0: return "up";
		case 1: return "down";
		case 2: return "left";
		case 3: return "right";
		default:
			System.out.println("Error, getRandDir");
			System.exit(-1);
			return null;
		}
	}

	public static void holdup(int milliseconds){
		try {
			TimeUnit.MILLISECONDS.sleep((int) (milliseconds));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}