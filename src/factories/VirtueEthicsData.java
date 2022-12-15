package factories;

public class VirtueEthicsData {
    

    private StateProfile trajectoryStateProfile;
    private String trajectoryAction;
    private StateProfile trajectorySuccessorStateProfile;

    public VirtueEthicsData(StateProfile trajectoryState, String trajectoryAction, StateProfile trajectorySuccessorState){
        this.trajectoryStateProfile          = trajectoryState;
        this.trajectoryAction         = trajectoryAction;
        this.trajectorySuccessorStateProfile = trajectorySuccessorState;

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

}
