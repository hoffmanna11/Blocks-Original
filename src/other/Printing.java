package other;

import main.Main;
import main_units.Cell;

@SuppressWarnings("serial")
public class Printing extends Main {

	public static void printRewardInformation(Training t, int i){
		/*
		System.out.print("Top percentile: " + topPercentile);
		System.out.print(" | Reward Rate: " + t.rewardRate);
		System.out.print(" | # of success trials: " + t.rewardNum);
		System.out.print(" | Cycles taken: " + (i - t.prevRewardIndex));
		System.out.println(" | Total cycles: " + t.totalCycles + "\n");
		*/
	}

	public static void printCells(){
		
		String dir = "";

		int dirPadding = 2;
		int weightPadding = 2;
		if(Cell.overallHighestWeight > 0){
			weightPadding = (int) Math.log10(Cell.overallHighestWeight) + 2;
		}
		String strFormat = "%" + dirPadding + "s";
		String intWeightFormat = "%" + weightPadding + "d";
		String strWeightFormat = "%" + weightPadding + "s";

		//System.out.println("Weight sum: " + weightSum + ", nonZeroCells: " + nonZeroCells + ", average: " + weightSum/nonZeroCells);
		System.out.println("Basic Weights");
		for(int j=0; j<cells.getRow(0).size(); j++){
			for(int i=0; i<cells.size(); i++){
				dir = cells.getCell(i, j).getLocalHighestWeightDir();
				if(dir.equals("-")){
					System.out.printf(strFormat, "-");
				}else{
					System.out.printf(strFormat, dir.substring(0,1));
				}
			}

			System.out.print("  ");

			for(int i=0; i<cells.size(); i++){
				double weight = cells.getCell(i, j).getLocalHighestWeight();
				if(weight == Cell.nullWeight){
					System.out.printf(strWeightFormat, "-");
				}else{
					System.out.printf(intWeightFormat, (int)weight);
				}
			}System.out.println();
		}System.out.println();
		
		System.out.println("Sorted Weights [Percentile: " + df.format(percentile) + ", Break Point: " + df.format(topPercentile) + "]");
		for(int j=0; j<cells.getRow(0).size(); j++){
			for(int i=0; i<cells.size(); i++){
				if(cells.getCell(i, j).getLocalHighestWeight() > topPercentile){
					System.out.printf(strFormat, cells.getCell(i, j).getLocalHighestWeightDir().substring(0,1));
				}else{
					System.out.printf(strFormat, "-");
				}
			}

			System.out.print("  ");

			for(int i=0; i<cells.size(); i++){
				double weight = cells.getCell(i, j).getLocalHighestWeight();
				if(cells.getCell(i, j).getLocalHighestWeight() > topPercentile){
					System.out.printf(intWeightFormat, (int)weight);
				}else{
					System.out.printf(strWeightFormat, "-");
				}
			}System.out.println();
		}System.out.println();

		System.out.println("--------------------------------------------------------------------------------------------------\n");
		
	}
}
