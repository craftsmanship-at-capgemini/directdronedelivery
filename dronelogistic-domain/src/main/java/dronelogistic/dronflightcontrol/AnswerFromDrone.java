package dronelogistic.dronflightcontrol;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AnswerFromDrone {
    
    @Getter protected Drone drone;
    @Getter protected List<ErrorInformation> errors;
    
    private AnswerFromDrone() {
    }
    
    public static AnswerFromDrone newAnswer(Drone drone, List<ErrorInformation> errors) {
        AnswerFromDrone answerFromDrone = new AnswerFromDrone();
        answerFromDrone.drone = drone;
        answerFromDrone.errors = errors;
        
        return answerFromDrone;
    }
    
}
