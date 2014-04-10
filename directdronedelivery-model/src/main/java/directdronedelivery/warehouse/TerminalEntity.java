package directdronedelivery.warehouse;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of = "terminalID")
@ToString
public class TerminalEntity {
    
    @Getter protected int terminalID;
    
    protected TerminalEntity(Integer terminalID) {
        this.terminalID = terminalID;
    }
    
}
