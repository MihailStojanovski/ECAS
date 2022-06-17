package rewards;

public interface Reward{
    
    public Double getTaskReward(String state, String action, String successorState);
    
    public EthicalRewardQuad getEthicalReward(String state, String action, String successorState);
}
