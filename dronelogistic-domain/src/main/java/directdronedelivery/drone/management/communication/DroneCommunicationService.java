package directdronedelivery.drone.management.communication;

import directdronedelivery.drone.DroneAggregate;

public interface DroneCommunicationService {
    
    AnswerFromDrone uploadDeliveryRoute(DroneAggregate drone, DeliveryRoute route);
    
    AnswerFromDrone performStartCheckList(DroneAggregate drone, CheckStartList checkList);
    
}
