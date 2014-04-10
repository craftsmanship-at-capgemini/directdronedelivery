package dronelogistic.dronflightcontrol;

public interface DroneCommunicationService {
    
    AnswerFromDrone uploadDeliveryRoute(Drone drone, DeliveryRoute route);
    
    AnswerFromDrone performStartCheckList(Drone drone, CheckStartList checkList);
    
}
