package factories;

public class VirtueEthicsData {
    
    private int negativeContextIndex;
    private int positiveContextIndex;
    private StateProfile trajectoryStateProfile;
    private String trajectoryAction;
    private StateProfile trajectorySuccessorStateProfile;
    private boolean positiveTrajectory;

    public VirtueEthicsData(int negativeContextIndex, int positiveContextIndex, StateProfile trajectoryState, String trajectoryAction, StateProfile trajectorySuccessorState, boolean positiveTrajectory){

        this.negativeContextIndex     = negativeContextIndex;
        this.positiveContextIndex     = positiveContextIndex;
        this.trajectoryStateProfile          = trajectoryState;
        this.trajectoryAction         = trajectoryAction;
        this.trajectorySuccessorStateProfile = trajectorySuccessorState;
        this.positiveTrajectory = positiveTrajectory;

    }

    public int getNegativeContextIndex() {
        return negativeContextIndex;
    }

    public int getPositiveContextIndex() {
        return positiveContextIndex;
    }

    public String getTrajectoryAction() {
        return trajectoryAction;
    }

    public StateProfile getTrajectoryStateProfile() {
        return trajectoryStateProfile;
    }

    public StateProfile getTrajectorySuccessorStateProfile() {
        return trajectorySuccessorStateProfile;
    }

    public boolean isPositiveTrajectory(){
        return positiveTrajectory;
    }
}
