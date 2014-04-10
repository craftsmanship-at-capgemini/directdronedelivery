package directdronedelivery.warehouse.employee;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.ProblemType;
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
    private List<Problem> problems;
    
    public CargoLoadTask(int taskID, int cargoID, BoxType boxType, int terminalID, int droneID) {
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
    
    public List<Problem> getProblems() {
        return Collections.unmodifiableList(problems);
    }
    
    public void addProblem(ProblemType droneProblemType, String log) {
        problems.add(new Problem(droneProblemType, log));
    }
}
