package factories;

public class VirtueEthicsData {
    
    private int negativeContextIndex;
    private int positiveContextIndex;
    private StateProfile trajectoryState;
    private String trajectoryAction;
    private StateProfile trajectorySuccessorState;

    public VirtueEthicsData(int negativeContextIndex, int positiveContextIndex, StateProfile trajectoryState, String trajectoryAction, StateProfile trajectorySuccessorState){

        this.negativeContextIndex     = negativeContextIndex;
        this.positiveContextIndex     = positiveContextIndex;
        this.trajectoryState          = trajectoryState;
        this.trajectoryAction         = trajectoryAction;
        this.trajectorySuccessorState = trajectorySuccessorState;

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

    public StateProfile getTrajectoryState() {
        return trajectoryState;
    }

    public StateProfile getTrajectorySuccessorState() {
        return trajectorySuccessorState;
    }
}
