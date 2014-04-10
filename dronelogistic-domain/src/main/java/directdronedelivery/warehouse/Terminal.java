package directdronedelivery.warehouse;

import lombok.Getter;

public class Terminal {
    
    @Getter int terminalID;
    
    public Terminal(int terminaID) {
        this.terminalID = terminaID;
    }
    
}
