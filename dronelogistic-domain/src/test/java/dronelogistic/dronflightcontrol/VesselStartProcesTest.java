package dronelogistic.dronflightcontrol;

import static dronelogistic.orderinformations.OrderAndCargoInformationBuilder.aCargo;

import java.util.ArrayList;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.TestEvent;
import testing.Testing;
import dronelogistic.orderinformations.DeliveryAddress;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrderAndCargoInformationBuilder;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.VesselStartProcess;
import dronelogistic.warehaus.VesselStartedEvent;
import dronelogistic.warehaus.cargoloadprocess.DroneLoadedEvent;

public class VesselStartProcesTest {
    
    @Inject VesselStartProcess vesselStartProcesTest;
    @Mock OrdersInformationService ordersInformationService;
    @Mock DronFlightControlService droneFlightControlService;
    @Mock DroneCommunicationService droneCommunicationService;
    @Mock DroneTechnicalService droneTechnicalService;
    @Inject TestEvent<VesselStartedEvent> vesselStartedEvent = new TestEvent<>();
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void vesselStartShouldSucceed() throws DroneNotFoundException {
        // VesselStart should succeed, all pre-conditions like upload delivery
        // route and check start list
        // has been performed without any errors
        // given
        Drone drone = createDrone();
        // create Cargo and OrderInformation deliverable with Drone
        OrderAndCargoInformation orderAndCargoInformation = prepareCargoDeliverableWithDrone();
        // create delivery route for upload and answers from drone
        DeliveryRoute route = prepareDeliveryRoute(orderAndCargoInformation.getDeliveryAddress());
        prepareAnswerFromDroneWithUploadSucceed(drone, route);
        prepareAnswerFromDroneWithCheckStartListSucceed(drone);
        // when
        DroneLoadedEvent loadedEvent = new DroneLoadedEvent(drone.getDroneID(), orderAndCargoInformation.getCargoID());
        vesselStartProcesTest.vesselLoaded(loadedEvent);
        // then
        Mockito.verify(droneTechnicalService, Mockito.times(0)).createErrorTicket(Mockito.any(Drone.class),
                Mockito.anyListOf(ErrorInformation.class));
        Assert.assertFalse(vesselStartedEvent.getEvents().isEmpty());
        Assert.assertTrue(vesselStartedEvent.getFirstEvent().getDrone().getDroneID() == drone.getDroneID());
        
    }
    
    @Test
    public void vesselStartShouldFailCauseOfUpload() throws DroneNotFoundException {
        // Vessel start procedure shouldn't succeed since the upload of the
        // delivery route failed
        // an error ticket should be created via DroneTechnicalService
        // given
        Drone drone = createDrone();
        // create Cargo and OrderInformation deliverable with Drone
        OrderAndCargoInformation orderAndCargoInformation = prepareCargoDeliverableWithDrone();
        DeliveryRoute route = prepareDeliveryRoute(orderAndCargoInformation.getDeliveryAddress());
        prepareAnswerFromDroneWithUploadFailed(drone, route);
        // when
        DroneLoadedEvent loadedEvent = new DroneLoadedEvent(drone.getDroneID(), orderAndCargoInformation.getCargoID());
        vesselStartProcesTest.vesselLoaded(loadedEvent);
        // then
        Mockito.verify(droneTechnicalService, Mockito.times(1)).createErrorTicket(Mockito.any(Drone.class),
                Mockito.anyListOf(ErrorInformation.class));
        Assert.assertTrue(vesselStartedEvent.getEvents().isEmpty());
    }
    
    @Test
    public void vesselStartShouldFailCauseOfCheckStartList() throws DroneNotFoundException {
        // Vessel start procedure shouldn't succeed since the check start list
        // procedure failed
        // an error ticket should be created via DroneTechnicalService
        // given
        Drone drone = createDrone();
        // create Cargo and OrderInformation deliverable with Drone
        OrderAndCargoInformation orderAndCargoInformation = prepareCargoDeliverableWithDrone();
        DeliveryRoute route = prepareDeliveryRoute(orderAndCargoInformation.getDeliveryAddress());
        prepareAnswerFromDroneWithUploadSucceed(drone, route);
        prepareAnswerFromDroneWithCheckStartListFailed(drone);
        // when
        DroneLoadedEvent loadedEvent = new DroneLoadedEvent(drone.getDroneID(), orderAndCargoInformation.getCargoID());
        vesselStartProcesTest.vesselLoaded(loadedEvent);
        // then
        Mockito.verify(droneTechnicalService, Mockito.times(1)).createErrorTicket(Mockito.any(Drone.class),
                Mockito.anyListOf(ErrorInformation.class));
        Assert.assertTrue(vesselStartedEvent.getEvents().isEmpty());
    }
    
    private void prepareAnswerFromDroneWithUploadFailed(Drone drone, DeliveryRoute route) {
        drone.status = DroneStatus.UPLOAD_FAILED;
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<ErrorInformation>());
        Mockito.when(droneCommunicationService.uploadDeliveryRoute(drone, route)).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithCheckStartListFailed(Drone drone) {
        drone.status = DroneStatus.HOUSTON_WE_HAVE_A_PROBLEM;
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<ErrorInformation>());
        Mockito.when(
                droneCommunicationService.performStartCheckList(Mockito.any(Drone.class),
                        Mockito.any(CheckStartList.class))).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithUploadSucceed(Drone drone, DeliveryRoute route) {
        drone.status = DroneStatus.ROUTE_UPLOADED;
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<ErrorInformation>());
        Mockito.when(droneCommunicationService.uploadDeliveryRoute(drone, route)).thenReturn(answer);
    }
    
    private void prepareAnswerFromDroneWithCheckStartListSucceed(Drone drone) {
        drone.status = DroneStatus.READY_FOR_TAKE_OFF;
        AnswerFromDrone answer = AnswerFromDrone.newAnswer(drone, new ArrayList<ErrorInformation>());
        Mockito.when(
                droneCommunicationService.performStartCheckList(Mockito.any(Drone.class),
                        Mockito.any(CheckStartList.class))).thenReturn(answer);
    }
    
    private DeliveryRoute prepareDeliveryRoute(DeliveryAddress address) {
        DeliveryRoute route = DeliveryRoute.newDeliveryRoute(new ArrayList<Checkpoint>());
        Mockito.when(droneFlightControlService.calculateDeliveryRoute(address)).thenReturn(route);
        return route;
    }
    
    private OrderAndCargoInformation prepareCargoDeliverableWithDrone() {
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID).
                withDeliveryAddress("22", "Strzegomska", "47-300").build();
        Mockito.when(ordersInformationService.getOrderAndCargoInformation(orderAndCargoInformation.getCargoID()))
                .thenReturn(orderAndCargoInformation);
        
        return orderAndCargoInformation;
    }
    
    private Drone createDrone() throws DroneNotFoundException {
        // create one available Drone
        Drone drone = AvailableDronesBuilder.newDrone();
        Mockito.reset(droneFlightControlService);
        Mockito.when(droneFlightControlService.findDrone(Mockito.<Integer> any())).thenReturn(drone);
        
        return drone;
    }
    
}
