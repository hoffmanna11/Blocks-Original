package main_units;

import java.awt.Color;

import main.Main;
import other_units.LastMoves;
import other_units.Location;

@SuppressWarnings("serial")
public class Player extends Main implements Cloneable {
	Location loc;
	LastMoves lastMoves;
	int playerIndex;
	Location origLoc;
	static Color color = Color.RED;
	
	public Player(){
	}

	public Player(int playerIndex, int x, int y, int size, Location origLoc){
		this.loc = new Location(x, y);
		this.lastMoves = new LastMoves(size);
		this.playerIndex = playerIndex;
		cells.getCell(x,y).setPlayerIndex(playerIndex);
		this.origLoc = origLoc;
	}
	
	public static Color getColor() {
		return color;
	}
	
	/*
	public Player clone(){
		Player player = new Player();
		player.loc = loc;
		player.lastMoves = lastMoves.clone();
		player.playerIndex = playerIndex;
		player.origLoc = origLoc;
		return player;
	}
	*/

	public static void setColor(Color color) {
		Player.color = color;
	}
	
	public Location getOrigLoc() {
		return origLoc;
	}

	public void setOrigLoc(Location origLoc) {
		this.origLoc = origLoc;
	}

	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}

	public Location getLoc(){
		return loc;
	}

	public void setLoc(Location newLoc){
		// update cells
		cells.updateCellsPlayerStatus(this.getLoc().getX(), this.getLoc().getY(), newLoc.getX(), newLoc.getY(), this.getPlayerIndex());
		
		// undraw old one
		drawPlayer(this.loc.getX(), this.loc.getY(), Color.WHITE);
		this.loc = newLoc;
		
		// redraw new one		
		drawPlayer(this.loc.getX(), this.loc.getY(), Color.RED);
	}

	public int getPlayerIndex(){
		return this.playerIndex;
	}

	public LastMoves getLastMoves() {
		return lastMoves;
	}

	public void setLastMoves(LastMoves lastMoves) {
		this.lastMoves = lastMoves;
	}
}
