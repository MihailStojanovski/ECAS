package rewards;

public interface Reward {
    
    public Integer getReward(String state, String action, String successorState);

    public EthicalReward getEthicalReward(String state, String action, String successorState);
}
