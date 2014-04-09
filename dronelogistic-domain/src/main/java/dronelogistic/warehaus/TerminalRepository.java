package dronelogistic.warehaus;

import java.util.List;

public interface TerminalRepository {
    
    public Terminal findTerminal(int terminalID);
    
    public List<Terminal> findAll();
    
}
