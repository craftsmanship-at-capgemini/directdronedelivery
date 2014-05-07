package directdronedelivery.warehouse.process;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static directdronedelivery.drone.DroneBuilder.aDrone;

import java.util.ArrayList;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.TestEvent;
import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.DeliveryAddress;
import directdronedelivery.cargo.OrderAndCargoInformationBuilder;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.DroneStatus;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.communication.AnswerFromDrone;
import directdronedelivery.drone.management.communication.CheckStartList;
import directdronedelivery.drone.management.communication.Checkpoint;
import directdronedelivery.drone.management.communication.DeliveryRoute;
import directdronedelivery.drone.management.communication.DroneCommunicationProtocol;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.process.DroneLoadedEvent;
import directdronedelivery.warehouse.process.DroneStartProcessService;
import directdronedelivery.warehouse.process.DroneStartedEvent;

public class DroneStartProcessServiceTest {
    
    @Inject DroneStartProcessService droneStartProcessService;
    @Mock CargoRepository cargoRepository;
    @Mock DroneRepository droneRepository;
    @Mock DronControlService droneControlService;
    @Mock DroneCommunicationProtocol droneCommunicationService;
    @Inject TestEvent<DroneStartedEvent> droneStartedEvent = new TestEvent<>();
    
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
                .withDroneStatus(DroneStatus.READY_FOR_TAKE_OFF).build();
        Mockito.when(droneRepository.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        // create Cargo and OrderInformation deliverable with Drone
        CargoAggregate cargo = prepareCargoDeliverableWithDrone();
        // create delivery route for upload and answers from drone
        DeliveryRoute route = prepareDeliveryRoute(cargo.getOrder().getDeliveryAddress());
        prepareAnswerFromDroneWithUploadSucceed(drone, route);
        prepareAnswerFromDroneWithCheckStartListSucceed(drone);
        // when
        DroneLoadedEvent loadedEvent = new DroneLoadedEvent(drone.getDroneID(), cargo.getCargoID());
        droneStartProcessService.initDroneStartProcess(loadedEvent);
        // then
        Mockito.verify(droneControlService, Mockito.times(0)).handleDroneProblems(Mockito.any(Integer.class),
                Mockito.anyListOf(Problem.class));
        Assert.assertFalse(droneStartedEvent.getEvents().isEmpty());
        Assert.assertEquals(droneStartedEvent.getFirstEvent().getDroneID(), drone.getDroneID());
        
    }
    
    @Test
    public void vesselStartShouldFailCauseOfUpload() {
        // Vessel start procedure shouldn't succeed since the upload of the
        // delivery route failed
        // an error ticket should be created via DroneTechnicalService
        // given
        DroneAggregate drone = aDrone().likeDocked4RotorsDrone()
                .withDroneStatus(DroneStatus.UPLOAD_FAILED).build();
        Mockito.when(droneRepository.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        // create Cargo and OrderInformation deliverable with Drone
        CargoAggregate cargo = prepareCargoDeliverableWithDrone();
        DeliveryRoute route = prepareDeliveryRoute(cargo.getOrder().getDeliveryAddress());
        prepareAnswerFromDroneWithUploadFailed(drone, route);
        // when
        DroneLoadedEvent loadedEvent = new DroneLoadedEvent(drone.getDroneID(), cargo.getCargoID());
        droneStartProcessService.initDroneStartProcess(loadedEvent);
        // then
        Mockito.verify(droneControlService, Mockito.times(1)).handleDroneProblems(Mockito.any(Integer.class),
                Mockito.anyListOf(Problem.class));
        Assert.assertTrue(droneStartedEvent.getEvents().isEmpty());
    }
    
    @Test
    public void vesselStartShouldFailCauseOfCheckStartList() {
        // Vessel start procedure shouldn't succeed since the check start list
        // procedure failed
        // an error ticket should be created via DroneTechnicalService
        // given
        DroneAggregate drone = aDrone().likeDocked4RotorsDrone()
                .withDroneStatus(DroneStatus.HOUSTON_WE_HAVE_A_PROBLEM).build();
        Mockito.when(droneRepository.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        // create Cargo and OrderInformation deliverable with Drone
        CargoAggregate cargo = prepareCargoDeliverableWithDrone();
        DeliveryRoute route = prepareDeliveryRoute(cargo.getOrder().getDeliveryAddress());
        prepareAnswerFromDroneWithUploadSucceed(drone, route);
        prepareAnswerFromDroneWithCheckStartListFailed(drone);
        // when
        DroneLoadedEvent loadedEvent = new DroneLoadedEvent(drone.getDroneID(), cargo.getCargoID());
        droneStartProcessService.initDroneStartProcess(loadedEvent);
        // then
        Mockito.verify(droneControlService, Mockito.times(1)).handleDroneProblems(Mockito.any(Integer.class),
                Mockito.anyListOf(Problem.class));
        Assert.assertTrue(droneStartedEvent.getEvents().isEmpty());
    }
    
    private void prepareAnswerFromDroneWithUploadFailed(DroneAggregate drone, DeliveryRoute route) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<Problem>());
        Mockito.when(droneCommunicationService.uploadDeliveryRoute(drone, route)).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithCheckStartListFailed(DroneAggregate drone) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<Problem>());
        Mockito.when(
                droneCommunicationService.performStartCheckList(Mockito.any(DroneAggregate.class),
                        Mockito.any(CheckStartList.class))).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithUploadSucceed(DroneAggregate drone, DeliveryRoute route) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<Problem>());
        Mockito.when(droneCommunicationService.uploadDeliveryRoute(drone, route)).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithCheckStartListSucceed(DroneAggregate drone) {
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<Problem>());
        Mockito.when(
                droneCommunicationService.performStartCheckList(Mockito.any(DroneAggregate.class),
                        Mockito.any(CheckStartList.class))).thenReturn(answer);
    }
    
    private DeliveryRoute prepareDeliveryRoute(DeliveryAddress address) {
        DeliveryRoute route = DeliveryRoute.newDeliveryRoute(new ArrayList<Checkpoint>());
        Mockito.when(droneControlService.calculateDeliveryRoute(address)).thenReturn(route);
        return route;
    }
    
    private CargoAggregate prepareCargoDeliverableWithDrone() {
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID).
                withDeliveryAddress("22", "Strzegomska", "47-300").build();
        Mockito.when(cargoRepository.findCargo(cargo.getCargoID()))
                .thenReturn(cargo);
        
        return cargo;
    }
    
}
