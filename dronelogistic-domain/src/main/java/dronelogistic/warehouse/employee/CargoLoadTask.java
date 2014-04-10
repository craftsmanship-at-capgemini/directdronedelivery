package dronelogistic.warehouse.employee;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import dronelogistic.dronflightcontrol.DroneProblem;
import dronelogistic.dronflightcontrol.DroneProblemType;
import dronelogistic.warehaus.BoxType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CargoLoadTask {
    
    @Getter private int taskID;
    @Getter private int cargoID;
    @Getter private int droneID;
    @Getter private int terminalID;
    @Getter private BoxType boxType;
    private List<DroneProblem> problems;
    
    public CargoLoadTask(int taskID, int cargoID, int droneID, int terminalID, BoxType boxType) {
        this.taskID = taskID;
        this.cargoID = cargoID;
        this.droneID = droneID;
        this.terminalID = terminalID;
        this.boxType = boxType;
        this.problems = new LinkedList<>();
    }
    
    public boolean hasProblems() {
        return !problems.isEmpty();
    }
    
    public List<DroneProblem> getProblems() {
        return Collections.unmodifiableList(problems);
    }
    
    public void addProblem(DroneProblemType droneProblemType, String log) {
        problems.add(new DroneProblem(droneProblemType, log));
    }
}
