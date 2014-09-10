package directdronedelivery.drone.management.communication;

public interface DroneCommunicationProtocol {
    
    AnswerFromDrone uploadDeliveryRoute(String ipAddress, DeliveryRoute route);
    
    AnswerFromDrone performStartCheckList(String ipAddress, StartCheckList checkList);
    
    boolean ping(String ipAddress);
    
    int currentGPSPosition(String ipAddress);
    
    void addCallbacks(String ipAddress, DroneCallbacks droneCallbacks);
    
    void removeCallbacks(String ipAddress, DroneCallbacks droneCallbacks);
    
}
