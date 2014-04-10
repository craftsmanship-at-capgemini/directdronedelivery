package dronelogistic.dronflightcontrol;

import java.util.List;

public interface DroneTechnicalService {
    
    void createErrorTicket(Drone drone, List<ErrorInformation> errorInformation);
    
}
