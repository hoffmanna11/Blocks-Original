package main_units;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import other_units.LastMoves;
import other_units.Location;
import other_units.Move;

public class Players implements Cloneable {
	ArrayList<Player> players;
	
	public Players(int size){
		players = new ArrayList<Player>(size);
	}
	
	public void add(Player p){
		players.add(p);
	}
	
	public void move(CellMatrix cells, ArrayList<Block> blocks, boolean training, boolean useBasicWeights, double topPercentile){
		ArrayList<LastMoves> lm = new ArrayList<LastMoves>(Main.lastMoves);
		ArrayList<Move> playerMoves = new ArrayList<Move>(players.size());
		ArrayList<Location> playerLocs = new ArrayList<Location>(players.size());

		int x, y;
		double greatest = 0;
		String dir;
		for(int i=0; i<players.size(); i++){
			// Training mode sets it so that each move is random
			if(training == true){
				playerMoves.add(Move.getRandMove(players.get(i).getLoc()));
			}else{
				// Use the weights on the cell grid, according to whichever
				// weight schema is being used (basic weights or those that
				// are above a certain percentile)
				greatest = 0;
				dir = "";
				x = players.get(i).getLoc().getX();
				y = players.get(i).getLoc().getY();

				Cell tempCell = cells.getCell(x, y);
				greatest = tempCell.getLocalHighestWeight();
				dir = tempCell.getLocalHighestWeightDir();

				// If non-null, i.e. if reward has been placed
				if (greatest != Cell.nullWeight){
					// Choose move based on weights
					Move move = new Move(dir, x, y);

					if((move.isValid()) && (useBasicWeights || (greatest > topPercentile))){
						playerMoves.add(move);
					}else{
						move = Move.getRandMove(players.get(i).getLoc());
						playerMoves.add(move);
					}
				}else{
					// No weights available, generate random move
					playerMoves.add(Move.getRandMove(players.get(i).getLoc()));
				}
			}
			playerLocs.add(Location.getNewLoc(playerMoves.get(i)) );
		}

		for(int i=0; i<players.size(); i++){
			lm.get(i).addMove(playerMoves.get(i));
		}
		
		// Indices used for keeping track of players
		ArrayList<Integer> playerIndices = new ArrayList<Integer>(players.size());
		for(int i=0; i<players.size(); i++){
			playerIndices.add(players.get(i).playerIndex);
		}
		
		for(int i=0; i<playerIndices.size(); i++){
			// Check if new location is uninhabited
			// Create a method in CellMatrix that checks if a cell is uninhabited
			Location newPlayerLoc = playerLocs.get(playerIndices.get(i));
			
			if(newPlayerLoc.isOutOfBounds(1)){
				// (this shouldn't ever happen), but if it does, do nothing
				System.out.println("Error: Out of bounds? Do nothing [Method 'Players.move']");
				playerIndices.remove(i); playerLocs.remove(i); playerMoves.remove(i);
			}else if(!(cells.getCell(newPlayerLoc).isInhabited())){
				// Move there since uninhabited
				players.get(playerIndices.get(i)).setLoc(playerLocs.get(0));
				playerIndices.remove(i); playerLocs.remove(i); playerMoves.remove(i);
			}else if(cells.getCell(newPlayerLoc).hasPlayer()){
				// Do nothing
				playerIndices.remove(i); playerLocs.remove(i); playerMoves.remove(i);
			}else if(cells.getCell(newPlayerLoc).hasBlock()){
				// Attempt to move it
				// Check if 
				//ArrayList<Integer> playerIndicesThatCanMoveBlock = canMoveBlock(players, playerMoves, playerLocs, lm);
			}
		}
		
		/*
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
				ArrayList<Integer> playerIndices = canMoveBlock(ps, playerMoves, playerLocs, locStatus, lm);
				// Block cannot be moved
				if(null == playerIndices){
					ps.remove(0);
					playerMoves.remove(0);
					playerLocs.remove(0);
					continue;
				}
				break;
			}
			
		}
		*/
	}
}


