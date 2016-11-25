package other_units;
import java.util.*;

public class LastMoves implements Cloneable {
	public int maxSize;
	public int currentSize;
	public List<Move> list;
	
	/*
	public LastMoves clone(){
		LastMoves l = new LastMoves(this.maxSize);
		l.currentSize = this.currentSize;
		for(int i=0; i<l.list.size(); i++){
			l.list.set(i, this.list.get(i).clone());
		}
	}
	*/
	
	public LastMoves(int maxSize){
		this.currentSize = 0;
		this.maxSize = maxSize;
		this.list = new ArrayList<Move>();
	}
	
	public int getCurrentSize(){
		return this.currentSize;
	}
	
	public int getMaxSize(){
		return this.maxSize;
	}
	
	public Move getMove(int index){
		return list.get(index);
	}
	
	public void addMove(Move move){
		if(currentSize == maxSize){
			list.remove(0);
			list.add(move);
		}else{
			list.add(move);
			currentSize++;
		}
	}
}
