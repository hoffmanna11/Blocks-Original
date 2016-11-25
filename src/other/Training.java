package other;

public class Training {
	String trainStr = "", useBasicNetStr = "", changePercentileStr = "";
	boolean training = false;
	double percentile = 0;
	public int rewardNum = 0;
	public int prevRewardIndex = 0;
	public int rewardRate = 0;
	public int totalCycles = 0;

	public Training(){
	}

	public Training(String trainStr, String useBasicNetStr, String changePercentileStr, boolean training,
			boolean useBasicNet, double percentile, int rewardNum, int prevRewardIndex, int rewardRate, int totalCycles) {
		super();
		this.trainStr = trainStr;
		this.useBasicNetStr = useBasicNetStr;
		this.changePercentileStr = changePercentileStr;
		this.training = training;
		this.percentile = percentile;
		this.rewardNum = rewardNum;
		this.prevRewardIndex = prevRewardIndex;
		this.rewardRate = rewardRate;
		this.totalCycles = totalCycles;
	}
	
	public void updateValues(int index){
		rewardNum++;
		rewardRate = totalCycles / rewardNum;
		prevRewardIndex = index;
	}
	
	/* Getters and setters */
	public String getTrainStr() {
		return trainStr;
	}

	public void setTrainStr(String trainStr) {
		this.trainStr = trainStr;
	}

	public String getUseBasicNetStr() {
		return useBasicNetStr;
	}

	public void setUseBasicNetStr(String useBasicNetStr) {
		this.useBasicNetStr = useBasicNetStr;
	}

	public String getChangePercentileStr() {
		return changePercentileStr;
	}

	public void setChangePercentileStr(String changePercentileStr) {
		this.changePercentileStr = changePercentileStr;
	}

	public boolean isTraining() {
		return training;
	}

	public void setTraining(boolean training) {
		this.training = training;
	}

	public double getPercentile() {
		return percentile;
	}

	public void setPercentile(double percentile) {
		this.percentile = percentile;
	}

	public int getRewardNum() {
		return rewardNum;
	}

	public void setRewardNum(int rewardNum) {
		this.rewardNum = rewardNum;
	}

	public int getPrevRewardIndex() {
		return prevRewardIndex;
	}

	public void setPrevRewardIndex(int prevRewardIndex) {
		this.prevRewardIndex = prevRewardIndex;
	}

	public int getRewardRate() {
		return rewardRate;
	}

	public void setRewardRate(int rewardRate) {
		this.rewardRate = rewardRate;
	}

	public int getTotalCycles() {
		return totalCycles;
	}

	public void setTotalCycles(int totalCycles) {
		this.totalCycles = totalCycles;
	}
}
