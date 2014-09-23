package directdronedelivery.warehouse.process;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static directdronedelivery.drone.DroneBuilder.aDrone;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.DeliveryAddress;
import directdronedelivery.cargo.OrderAndCargoInformationBuilder;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.StartCheckList;
import directdronedelivery.drone.management.communication.Waypoint;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.drone.management.communication.DroneCommunicationProtocol;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.ProblemType;
import directdronedelivery.warehouse.TerminalEntity;
import directdronedelivery.warehouse.process.CargoLoadedEvent;
import directdronedelivery.warehouse.process.DroneStartProcessService;

public class DroneStartProcessServiceTest {
    
    @Inject DroneStartProcessService droneStartProcessService;
    @Mock CargoRepository cargoRepository;
    @Mock DroneRepository droneRepository;
    @Mock DronControlService droneControlService;
    @Mock DroneCommunicationProtocol droneCommunicationProtocol;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void vesselStartShouldSucceed() {
        // VesselStart should succeed, all pre-conditions like upload delivery
        // route and check start list
        // has been performed without any errors
        // given
        DroneAggregate drone = aDrone().likeDocked4RotorsDrone()
                .withDroneCommunicationProtocol(droneCommunicationProtocol, "194.13.82.11").build();
        Mockito.when(droneRepository.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        // create Cargo and OrderInformation deliverable with Drone
        CargoAggregate cargo = prepareCargoDeliverableWithDrone();
        // create delivery route for upload and answers from drone
        DeliveryRoute route = prepareDeliveryRoute(drone.getDockingTerminal(), cargo.getOrder().getDeliveryAddress());
        
        prepareAnswerFromDroneWithUploadSucceed(drone, route);
        prepareAnswerFromDroneWithStartCheckListSucceed(drone);
        // when
        CargoLoadedEvent loadedEvent = new CargoLoadedEvent(drone.getDroneID(), cargo.getCargoID());
        droneStartProcessService.initDroneStartProcess(loadedEvent);
        // then
        Mockito.verify(droneControlService, Mockito.times(0)).handleDroneProblems(Mockito.any(Integer.class),
                Mockito.anyListOf(Problem.class));
        Mockito.verify(droneControlService).takeOff(Mockito.eq(drone));
    }
    
    @Test
    public void vesselStartShouldFailCauseOfUpload() {
        // Vessel start procedure shouldn't succeed since the upload of the
        // delivery route failed
        // an error ticket should be created via DroneTechnicalService
        // given
        DroneAggregate drone = aDrone().likeDocked4RotorsDrone()
                .withDroneCommunicationProtocol(droneCommunicationProtocol, "194.13.82.11").build();
        Mockito.when(droneRepository.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        // create Cargo and OrderInformation deliverable with Drone
        CargoAggregate cargo = prepareCargoDeliverableWithDrone();
        DeliveryRoute route = prepareDeliveryRoute(drone.getDockingTerminal(), cargo.getOrder().getDeliveryAddress());
        prepareAnswerFromDroneWithUploadFailed(drone, route);
        // when
        CargoLoadedEvent loadedEvent = new CargoLoadedEvent(drone.getDroneID(), cargo.getCargoID());
        droneStartProcessService.initDroneStartProcess(loadedEvent);
        // then
        Mockito.verify(droneControlService, Mockito.times(1)).handleDroneProblems(Mockito.any(Integer.class),
                Mockito.anyListOf(Problem.class));
        Mockito.verify(droneControlService, Mockito.times(0)).takeOff(Mockito.any(DroneAggregate.class));
    }
    
    @Test
    public void vesselStartShouldFailCauseOfCheckStartList() {
        // Vessel start procedure shouldn't succeed since the check start list
        // procedure failed
        // an error ticket should be created via DroneTechnicalService
        // given
        DroneAggregate drone = aDrone().likeDocked4RotorsDrone()
                .withDroneCommunicationProtocol(droneCommunicationProtocol, "194.13.82.11").build();
        Mockito.when(droneRepository.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        // create Cargo and OrderInformation deliverable with Drone
        CargoAggregate cargo = prepareCargoDeliverableWithDrone();
        DeliveryRoute route = prepareDeliveryRoute(drone.getDockingTerminal(), cargo.getOrder().getDeliveryAddress());
        prepareAnswerFromDroneWithUploadSucceed(drone, route);
        prepareAnswerFromDroneWithStartCheckListFailed(drone);
        // when
        CargoLoadedEvent loadedEvent = new CargoLoadedEvent(drone.getDroneID(), cargo.getCargoID());
        droneStartProcessService.initDroneStartProcess(loadedEvent);
        // then
        Mockito.verify(droneControlService, Mockito.times(1)).handleDroneProblems(Mockito.any(Integer.class),
                Mockito.anyListOf(Problem.class));
        Mockito.verify(droneControlService, Mockito.times(0)).takeOff(Mockito.any(DroneAggregate.class));
    }
    
    private void prepareAnswerFromDroneWithUploadFailed(DroneAggregate drone, DeliveryRoute route) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, Collections.singletonList(new Problem(ProblemType.DATA_UPLOAD_IMPOSSIBLE, "Delivery route transfer failed")));
        Mockito.when(droneCommunicationProtocol.uploadDeliveryRoute(Mockito.anyString(), Mockito.eq(route))).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithStartCheckListFailed(DroneAggregate drone) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, Collections.singletonList(new Problem(ProblemType.ROTORS_JAMMED, "Rotor 1 not working correctly")));
        Mockito.when(
                droneCommunicationProtocol.performStartCheckList(Mockito.anyString(),
                        Mockito.any(StartCheckList.class))).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithUploadSucceed(DroneAggregate drone, DeliveryRoute route) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<Problem>());
        Mockito.when(droneCommunicationProtocol.uploadDeliveryRoute(Mockito.anyString(), Mockito.eq(route))).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithStartCheckListSucceed(DroneAggregate drone) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<Problem>());
        Mockito.when(
                droneCommunicationProtocol.performStartCheckList(Mockito.anyString(),
                        Mockito.any(StartCheckList.class))).thenReturn(answer);
    }
    
    private DeliveryRoute prepareDeliveryRoute(TerminalEntity terminalEntity, DeliveryAddress address) {
        DeliveryRoute route = DeliveryRoute.newDeliveryRoute(new ArrayList<Waypoint>());
        Mockito.when(droneControlService.calculateDeliveryRoute(Mockito.eq(terminalEntity.getPosition()), Mockito.eq(address))).thenReturn(route);
        return route;
    }
    
    private CargoAggregate prepareCargoDeliverableWithDrone() {
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID).
                withDeliveryAddress("Wrocław", "47-300", "Strzegomska", "22", "1").build();
        Mockito.when(cargoRepository.findCargo(cargo.getCargoID()))
                .thenReturn(cargo);
        
        return cargo;
    }
    
}
