package directdronedelivery.warehouse.process;

import static directdronedelivery.cargo.AcceptableDeliveryTimeBuilder.aTime;
import static directdronedelivery.cargo.ConsignmentBuilder.aConsignment;
import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static directdronedelivery.drone.management.AvailableDronesBuilder.anAvaliableDrones;
import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.TestEvent;
import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.ConsignmentChangedEvent;
import directdronedelivery.cargo.ConsignmentAggregate;
import directdronedelivery.cargo.OrderAndCargoInformationBuilder;
import directdronedelivery.cargo.OrderUpdatedEvent;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneBuilder;
import directdronedelivery.drone.DroneType;
import directdronedelivery.drone.management.AvailableDrones;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.drone.management.DroneAvaliableEvent;
import directdronedelivery.drone.management.DroneNotAvaliableException;
import directdronedelivery.warehouse.businessrules.CargoSpecyfication;
import directdronedelivery.warehouse.businessrules.DeliveryTimeAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.OrderPriorityCalculator;
import directdronedelivery.warehouse.businessrules.PlaceOfDeliverySpecyfication;
import directdronedelivery.warehouse.businessrules.ProfitabilityAndPriorityAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.ProfitabilityCalculator;
import directdronedelivery.warehouse.businessrules.TestDeliveryTimeAcceptanceStrategy;
import directdronedelivery.warehouse.businessrules.WeatherSpecyfication;
import directdronedelivery.warehouse.process.DroneDeliveryDecisionEvent;
import directdronedelivery.warehouse.process.NewCargoInWarehausEvent;
import directdronedelivery.warehouse.process.VesselChooseProcessCargoStateRepository;
import directdronedelivery.warehouse.process.VesselChooseProcessService;
import directdronedelivery.weather.Weather;
import directdronedelivery.weather.WeatherBuilder;
import directdronedelivery.weather.WeatherService;

public class VesselChooseProcessExampleScenariosTest {
    
    @Inject VesselChooseProcessService processUnderTest;
    
    @Mock CargoRepository cargoRepository;
    @Mock WeatherService weatherService;
    @Mock DronControlService dronControlService;
    @Inject VesselChooseProcessCargoStateRepository vesselChooseProcessCargoStateRepository = new VesselChooseProcessCargoStateRepositoryInMem();
    @Inject TestEvent<DroneDeliveryDecisionEvent> droneDeliveryDecisionEvent = new TestEvent<>();
    
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
        
        VesselChooseProcessCargoStateRepositoryInMem.configure(vesselChooseProcessCargoStateRepository)
                .withPositiveCargoIndependentSubDecisions();
    }
    
    @Test
    public void shouldDecideToStartDronWhenCargoIsDeliverableDronesAreAvaliableAndWeatherIsOK()
            throws DroneNotAvaliableException {
        DroneAggregate drone = createOneAvailableDrone();
        cheatTheCurrentTime("18:00");
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID)
                .withAcceptableDeliveryTime(aTime().addInterval("17:00 - 21:00")).build();
        prepareCargoDeliverableWithDrone(cargo);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        ConsignmentAggregate consignement = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(cargo);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignement.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        
        // final decision should be taken
        DroneDeliveryDecisionEvent outcomeEvent = droneDeliveryDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDroneID()).isEqualTo(drone.getDroneID());
    }
    
    @Test
    public void shouldDecideToStartDronsWhenWeatherChangesToAcceptable() throws DroneNotAvaliableException {
        DroneAggregate drone = createOneAvailableDrone();
        cheatTheCurrentTime("18:00");
        createBADWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID)
                .withAcceptableDeliveryTime(aTime().addInterval("17:00 - 21:00")).build();
        prepareCargoDeliverableWithDrone(cargo);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Consignment with not profitable Truck Delivery
        ConsignmentAggregate consignement = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(cargo);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignement.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // change Weather conditions
        createNiceWeatherConditions();
        
        // STEP 3. Weather evaluation:
        processUnderTest.periodicalWeatherCheck();
        
        // final decision should be taken
        DroneDeliveryDecisionEvent outcomeEvent =
                droneDeliveryDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDroneID()).isEqualTo(drone.getDroneID());
    }
    
    @Test
    public void shouldDecideToStartDronsWhenDeliveryTimeIsChangedToAcceptable() throws DroneNotAvaliableException {
        DroneAggregate drone = createOneAvailableDrone();
        cheatTheCurrentTime("15:00");
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID)
                .withAcceptableDeliveryTime(aTime().addInterval("17:00 - 21:00")).build();
        prepareCargoDeliverableWithDrone(cargo);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Consignment with not profitable Truck Delivery
        ConsignmentAggregate consignement = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(cargo);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignement.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // STEP 3. time flies:
        cheatTheCurrentTime("17:00");
        processUnderTest.periodicalDeliveryTimeAcceptanceCheck();
        
        // final decision should be taken
        DroneDeliveryDecisionEvent outcomeEvent =
                droneDeliveryDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDroneID()).isEqualTo(drone.getDroneID());
    }
    
    @Test
    public void shouldDecideToStartDronWhenOrderInformationsAreUpdatedAndNowCargoIsDeliverable()
            throws DroneNotAvaliableException {
        DroneAggregate drone = createOneAvailableDrone();
        createNiceWeatherConditions();
        
        // STEP 0. Weather evaluation
        // right now there is no cargo in Warehouse:
        processUnderTest.periodicalWeatherCheck();
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo NOT deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID)
                .but().withDangerousGoods(true).withWeightInKilos(16).build();
        prepareCargoNOTDeliverableWithDrone(cargo);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        ConsignmentAggregate consignement = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(cargo);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignement.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // change Informations to Cargo deliverable with Drone
        CargoAggregate actualisedCargo = aCargo().likeSmallGift().withCargoID(cargoID)
                .build();
        prepareCargoDeliverableWithDrone(actualisedCargo);
        
        // STEP 3. Cargo informations changed
        OrderUpdatedEvent orderUpdatedEvent = new OrderUpdatedEvent(cargoID);
        processUnderTest.orderUpdated(orderUpdatedEvent);
        
        // final decision should be taken
        DroneDeliveryDecisionEvent outcomeEvent = droneDeliveryDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDroneID()).isEqualTo(drone.getDroneID());
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
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        CargoAggregate cargo = aCargo().likeSmallGift().withCargoID(cargoID).build();
        prepareCargoDeliverableWithDrone(cargo);
        
        // STEP 1. Cargo is scanned in Warehouse:
        NewCargoInWarehausEvent newCargoInWarehausEvent = new NewCargoInWarehausEvent(cargoID, warehausID);
        processUnderTest.newCargoInWarehaus(newCargoInWarehausEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        ConsignmentAggregate consignement = assignCargoToNewConsignmentWithNotProfitableTruckDelivery(cargo);
        
        // STEP 2. Cargo is added to Consignment
        // assigned to concrete Truck Delivery:
        ConsignmentChangedEvent consignmentChangedEvent = new ConsignmentChangedEvent(
                consignement.getConsignmentID());
        processUnderTest.consignmentChanged(consignmentChangedEvent);
        // Decision can not be taken for now
        assertThat(droneDeliveryDecisionEvent.getEvents()).isEmpty();
        
        // STEP 3. Drone is now available:
        DroneAggregate drone = createOneAvailableDrone();
        DroneAvaliableEvent droneAvaliableEvent = new DroneAvaliableEvent(drone.getDroneType());
        processUnderTest.droneAvaliable(droneAvaliableEvent);
        
        // final decision should be taken
        DroneDeliveryDecisionEvent outcomeEvent = droneDeliveryDecisionEvent.getFirstEvent();
        assertThat(outcomeEvent.getCargoID()).isEqualTo(cargoID);
        assertThat(outcomeEvent.getDroneID()).isEqualTo(drone.getDroneID());
    }
    
    private DroneAggregate createOneAvailableDrone() throws DroneNotAvaliableException {
        // create one available Drone
        DroneAggregate drone = DroneBuilder.aDrone().likeDocked4RotorsDrone().build();
        AvailableDrones avaliableDrones = anAvaliableDrones().likeNoDronesAvaliable().but().withDrone(drone).build();
        Mockito.reset(dronControlService); // yep, I know...
        Mockito.when(dronControlService.getAvailableDrones()).thenReturn(avaliableDrones);
        Mockito.when(dronControlService.reserveDrone(drone.getDroneType())).thenReturn(drone);
        return drone;
    }
    
    private void noAvailableDrone() throws DroneNotAvaliableException {
        AvailableDrones avaliableDrones = anAvaliableDrones().likeNoDronesAvaliable().build();
        Mockito.reset(dronControlService); // yep, I know...
        Mockito.when(dronControlService.getAvailableDrones()).thenReturn(avaliableDrones);
        Mockito.when(dronControlService.reserveDrone(Mockito.<DroneType> any())).thenThrow(
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
        assertThat(weatherSpecyfication.isSatisfiedBy(niceWeather))
                .describedAs("test definition is wrong, Weather should be acceptable for drone flight").isTrue();
    }
    
    private void createBADWeatherConditions() {
        // create a BAD Weather conditions
        Weather badWeather =
                WeatherBuilder.aWeather().likeNiceWeather().but().withWindInMetersPerSecond(1000).build();
        Mockito.when(weatherService.getActualWeather()).thenReturn(badWeather);
        // be sure Weather is NOT acceptable
        assertThat(weatherSpecyfication.isSatisfiedBy(badWeather))
                .describedAs("test definition is wrong, Weather should be NOT acceptable for drone flight").isFalse();
        VesselChooseProcessCargoStateRepositoryInMem.configure(vesselChooseProcessCargoStateRepository)
                .withWeatherAcceptable(false);
    }
    
    private void prepareCargoDeliverableWithDrone(CargoAggregate cargo) {
        Mockito.when(cargoRepository.findCargo(cargo.getCargoID()))
                .thenReturn(cargo);
        // be sure Cargo is deliverable with Drone,
        // otherwise test definition is wrong
        assertThat(cargoSpecyfication.isSatisfiedForDronTypes(cargo))
                .describedAs("test definition is wrong, drone delivery should be possible").isNotEmpty();
        assertThat(placeOfDeliverySpecyfication.isSatisfiedBy(cargo))
                .describedAs("test definition is wrong, drone delivery should be possible").isTrue();
    }
    
    private void prepareCargoNOTDeliverableWithDrone(CargoAggregate cargo) {
        Mockito.when(cargoRepository.findCargo(cargo.getCargoID()))
                .thenReturn(cargo);
        // be sure Cargo is NOT deliverable with Drone,
        // otherwise test definition is wrong
        assertThat(!cargoSpecyfication.isSatisfiedForDronTypes(cargo).isEmpty()
                && placeOfDeliverySpecyfication.isSatisfiedBy(cargo))
                .describedAs("test definition is wrong, drone delivery should be NOT possible").isFalse();
    }
    
    private ConsignmentAggregate assignCargoToNewConsignmentWithNotProfitableTruckDelivery(
            CargoAggregate cargo) {
        ConsignmentAggregate consignement = aConsignment().likeEmptyConsignment()
                .but().withCargo(cargo).build();
        Mockito.when(cargoRepository.findConsignment(consignement.getConsignmentID()))
                .thenReturn(consignement);
        // be sure Drone delivery is profitable and/or justified by priority,
        // otherwise test definition is wrong
        assertThat(profitabilityAndPriorityAcceptanceStrategy.isPositive(
                profitabilityCalculator.evaluateProfitability(cargo, consignement),
                orderPriorityCalculator.evaluatePriority(cargo, consignement)))
                .describedAs(
                        "test definition is wrong, Drone delivery should be profitable and/or justified by priority")
                .isTrue();
        return consignement;
    }
    
}
