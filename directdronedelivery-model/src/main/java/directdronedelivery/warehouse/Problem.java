package directdronedelivery.warehouse;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Problem {
    
    @Getter ProblemType droneProblemType;
    @Getter String log;
    
}
