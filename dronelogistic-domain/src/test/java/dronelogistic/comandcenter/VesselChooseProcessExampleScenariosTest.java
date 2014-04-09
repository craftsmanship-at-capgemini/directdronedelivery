package dronelogistic.comandcenter;

import static dronelogistic.dronflightcontrol.AvaliableDronesBuilder.anAvaliableDrones;
import static dronelogistic.dronflightcontrol.AvaliableDronesBuilder.newDrone;
import static dronelogistic.orderinformations.AcceptableDeliveryTimeBuilder.aTime;
import static dronelogistic.orderinformations.ConsignmentInformationBuilder.aConsignment;
import static dronelogistic.orderinformations.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.TestEvent;
import testing.Testing;
import dronelogistic.comandcenter.businessrules.CargoSpecyfication;
import dronelogistic.comandcenter.businessrules.DeliveryTimeAcceptanceStrategy;
import dronelogistic.comandcenter.businessrules.OrderPriorityCalculator;
import dronelogistic.comandcenter.businessrules.PlaceOfDeliverySpecyfication;
import dronelogistic.comandcenter.businessrules.ProfitabilityAndPriorityAcceptanceStrategy;
import dronelogistic.comandcenter.businessrules.ProfitabilityCalculator;
import dronelogistic.comandcenter.businessrules.TestDeliveryTimeAcceptanceStrategy;
import dronelogistic.comandcenter.businessrules.WeatherSpecyfication;
import dronelogistic.dronflightcontrol.AvaliableDrones;
import dronelogistic.dronflightcontrol.DronFlightControlService;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneAvaliableEvent;
import dronelogistic.dronflightcontrol.DroneNotAvaliableException;
import dronelogistic.orderinformations.ConsignmentChangedEvent;
import dronelogistic.orderinformations.ConsignmentInformation;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrderAndCargoInformationBuilder;
import dronelogistic.orderinformations.OrderUpdatedEvent;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.NewCargoInWarehausEvent;
import dronelogistic.weather.Weather;
import dronelogistic.weather.WeatherBuilder;
import dronelogistic.weather.WeatherService;

public class VesselChooseProcessExampleScenariosTest {
    
    @Inject VesselChooseProcess processUnderTest;
    
    @Mock OrdersInformationService ordersInformationService;
    @Mock WeatherService weatherService;
    @Mock DronFlightControlService dronFlightControlService;
    @Inject TakeOffDecisionRepository takeOffDecisionRepository = new TestInMemoryTakeOffDecisionRepository();
    @Inject TestEvent<DroneTakeOffDecision> droneTakeOffDecisionEvent = new TestEvent<>();
    
    @Inject CargoSpecyfication cargoSpecyfication;
    @Inject PlaceOfDeliverySpecyfication placeOfDeliverySpecyfication;
    @Inject ProfitabilityCalculator profitabilityCalculator;
    @Inject OrderPriorityCalculator orderPriorityCalculator;
    @Inject ProfitabilityAndPriorityAcceptanceStrategy profitabilityAndPriorityAcceptanceStrategy;
    @Inject DeliveryTimeAcceptanceStrategy deliveryTimeAcceptanceStrategy = new TestDeliveryTimeAcceptanceStrategy();
    @Inject WeatherSpecyfication weatherSpecyfication;
    
    private Integer warehausID = 1;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
        
        // prepare TakeOffDecisionRepository
        TestInMemoryTakeOffDecisionRepository.configure(takeOffDecisionRepository)
                .withPositiveCargoIndependentSubDecisions();
    }
    
    @Test
    public void shouldDecideToStartDronWhenCargoIsDeliverableDronesAreAvaliableAndWeatherIsOK()
            throws DroneNotAvaliableException {
        Drone drone = createOneAvailableDrone();
        cheatTheCurrentTime("18:00");
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID)
                .withAcceptableDeliveryTime(aTime().addInterval("17:00 - 21:00")).build();
        prepareCargoDeliverableWithDrone(orderAndCargoInformation);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        ConsignmentInformation consignementInformation = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(orderAndCargoInformation);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignementInformation.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        
        // final decision should be taken
        DroneTakeOffDecision outcomeEvent = droneTakeOffDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDrone()).isEqualTo(drone);
    }
    
    @Test
    public void shouldDecideToStartDronsWhenWeatherChangesToAcceptable() throws DroneNotAvaliableException {
        Drone drone = createOneAvailableDrone();
        cheatTheCurrentTime("18:00");
        createBADWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID)
                .withAcceptableDeliveryTime(aTime().addInterval("17:00 - 21:00")).build();
        prepareCargoDeliverableWithDrone(orderAndCargoInformation);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Consignment with not profitable Truck Delivery
        ConsignmentInformation consignementInformation = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(orderAndCargoInformation);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignementInformation.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // change Weather conditions
        createNiceWeatherConditions();
        
        // STEP 3. Weather evaluation:
        processUnderTest.periodicalWeatherCheck();
        
        // final decision should be taken
        DroneTakeOffDecision outcomeEevent =
                droneTakeOffDecisionEvent.getFirstEvent();
        assertThat(outcomeEevent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEevent.getDrone()).isEqualTo(drone);
    }
    
    @Test
    public void shouldDecideToStartDronsWhenDeliveryTimeIsChangedToAcceptable() throws DroneNotAvaliableException {
        Drone drone = createOneAvailableDrone();
        cheatTheCurrentTime("15:00");
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID)
                .withAcceptableDeliveryTime(aTime().addInterval("17:00 - 21:00")).build();
        prepareCargoDeliverableWithDrone(orderAndCargoInformation);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Consignment with not profitable Truck Delivery
        ConsignmentInformation consignementInformation = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(orderAndCargoInformation);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignementInformation.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // STEP 3. time flies:
        cheatTheCurrentTime("17:00");
        processUnderTest.periodicalDeliveryTimeAcceptanceCheck();
        
        // final decision should be taken
        DroneTakeOffDecision outcomeEevent =
                droneTakeOffDecisionEvent.getFirstEvent();
        assertThat(outcomeEevent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEevent.getDrone()).isEqualTo(drone);
    }
    
    @Test
    public void shouldDecideToStartDronWhenOrderInformationsAreUpdatedAndNowCargoIsDeliverable()
            throws DroneNotAvaliableException {
        Drone drone = createOneAvailableDrone();
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo NOT deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID)
                .but().withDangerousGoods(true).withWeightInKilos(16).build();
        prepareCargoNOTDeliverableWithDrone(orderAndCargoInformation);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        ConsignmentInformation consignementInformation = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(orderAndCargoInformation);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignementInformation.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // change Informations to Cargo deliverable with Drone
        OrderAndCargoInformation actualisedOrderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID)
                .build();
        prepareCargoDeliverableWithDrone(actualisedOrderAndCargoInformation);
        
        // STEP 3. Cargo informations changed
        OrderUpdatedEvent orderUpdatedEvent = new OrderUpdatedEvent(cargoID);
        processUnderTest.orderUpdated(orderUpdatedEvent);
        
        // final decision should be taken
        DroneTakeOffDecision outcomeEvent = droneTakeOffDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDrone()).isEqualTo(drone);
    }
    
    @Test
    public void shouldDecideToStartDronWhenDroneIsBackToWarehouse()
            throws DroneNotAvaliableException {
        noAvailableDrone();
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID).build();
        prepareCargoDeliverableWithDrone(orderAndCargoInformation);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        ConsignmentInformation consignementInformation = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(orderAndCargoInformation);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignementInformation.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneTakeOffDecisionEvent.getEvents()).isEmpty();
        
        // STEP 3. Drone is now available:
        Drone drone = createOneAvailableDrone();
        DroneAvaliableEvent droneAvaliableEvent = new DroneAvaliableEvent(drone.getDroneType());
        processUnderTest.droneAvaliable(droneAvaliableEvent);
        
        // final decision should be taken
        DroneTakeOffDecision outcomeEvent = droneTakeOffDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDrone()).isEqualTo(drone);
    }
    
    private Drone createOneAvailableDrone() throws DroneNotAvaliableException {
        // create one available Drone
        Drone drone = newDrone();
        AvaliableDrones avaliableDrones = anAvaliableDrones().likeNoDronesAvaliable().but().withDrone(drone).build();
        Mockito.reset(dronFlightControlService); // yep, I know...
        Mockito.when(dronFlightControlService.getAvaliableDrones()).thenReturn(avaliableDrones);
        Mockito.when(dronFlightControlService.reserveDrone(drone.getDroneType())).thenReturn(drone);
        return drone;
    }
    
    private void noAvailableDrone() throws DroneNotAvaliableException {
        AvaliableDrones avaliableDrones = anAvaliableDrones().likeNoDronesAvaliable().build();
        Mockito.reset(dronFlightControlService); // yep, I know...
        Mockito.when(dronFlightControlService.getAvaliableDrones()).thenReturn(avaliableDrones);
        Mockito.when(dronFlightControlService.reserveDrone(Mockito.<String> any())).thenThrow(
                new DroneNotAvaliableException());
    }
    
    private void cheatTheCurrentTime(String hHmm) {
        // cheat the current time to given
        TestDeliveryTimeAcceptanceStrategy.configure(deliveryTimeAcceptanceStrategy).withFixedCurrentTime(hHmm);
    }
    
    private void createNiceWeatherConditions() {
        // change to nice Weather conditions
        Weather niceWeather =
                WeatherBuilder.aWeather().likeNiceWeather().build();
        Mockito.when(weatherService.getActualWeather()).thenReturn(niceWeather);
        // be sure Weather is acceptable
        assertThat(weatherSpecyfication.isAcceptable(niceWeather))
                .describedAs("test definition is wrong, Weather should be acceptable for drone flight").isTrue();
    }
    
    private void createBADWeatherConditions() {
        // create a BAD Weather conditions
        Weather badWeather =
                WeatherBuilder.aWeather().likeNiceWeather().but().withWindInMetersPerSecond(1000).build();
        Mockito.when(weatherService.getActualWeather()).thenReturn(badWeather);
        // be sure Weather is NOT acceptable
        assertThat(weatherSpecyfication.isAcceptable(badWeather))
                .describedAs("test definition is wrong, Weather should be NOT acceptable for drone flight").isFalse();
        TestInMemoryTakeOffDecisionRepository.configure(takeOffDecisionRepository).withWeatherAcceptable(false);
    }
    
    private void prepareCargoDeliverableWithDrone(OrderAndCargoInformation orderAndCargoInformation) {
        Mockito.when(ordersInformationService.getOrderAndCargoInformation(orderAndCargoInformation.getCargoID()))
                .thenReturn(orderAndCargoInformation);
        // be sure Cargo is deliverable with Drone,
        // otherwise test definition is wrong
        assertThat(cargoSpecyfication.possibleDronTypes(orderAndCargoInformation))
                .describedAs("test definition is wrong, drone delivery should be possible").isNotEmpty();
        assertThat(placeOfDeliverySpecyfication.isAcceptable(orderAndCargoInformation))
                .describedAs("test definition is wrong, drone delivery should be possible").isTrue();
    }
    
    private void prepareCargoNOTDeliverableWithDrone(OrderAndCargoInformation orderAndCargoInformation) {
        Mockito.when(ordersInformationService.getOrderAndCargoInformation(orderAndCargoInformation.getCargoID()))
                .thenReturn(orderAndCargoInformation);
        // be sure Cargo is NOT deliverable with Drone,
        // otherwise test definition is wrong
        assertThat(!cargoSpecyfication.possibleDronTypes(orderAndCargoInformation).isEmpty()
                && placeOfDeliverySpecyfication.isAcceptable(orderAndCargoInformation))
                .describedAs("test definition is wrong, drone delivery should be NOT possible").isFalse();
    }
    
    private ConsignmentInformation assignCargoToNewConsignmentWithNotProfitableTruckDelivery(
            OrderAndCargoInformation orderAndCargoInformation) {
        ConsignmentInformation consignementInformation = aConsignment().likeEmptyConsignment()
                .but().withCargo(orderAndCargoInformation).build();
        Mockito.when(ordersInformationService.getConsignmentInformation(consignementInformation.getConsignmentID()))
                .thenReturn(consignementInformation);
        // be sure Drone delivery is profitable and/or justified by priority,
        // otherwise test definition is wrong
        assertThat(profitabilityAndPriorityAcceptanceStrategy.isPositive(
                profitabilityCalculator.evaluateProfitability(orderAndCargoInformation, consignementInformation),
                orderPriorityCalculator.evaluatePriority(orderAndCargoInformation, consignementInformation)))
                .describedAs(
                        "test definition is wrong, Drone delivery should be profitable and/or justified by priority")
                .isTrue();
        return consignementInformation;
    }
    
}
