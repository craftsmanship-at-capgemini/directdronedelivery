package dronelogistic.warehouse;

import static dronelogistic.orderinformations.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.TestEvent;
import testing.Testing;
import dronelogistic.comandcenter.DroneTakeOffDecision;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneLoadedEvent;
import dronelogistic.dronflightcontrol.DroneRepository;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrderAndCargoInformationBuilder;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.orderinformations.Size;
import dronelogistic.warehaus.Box;
import dronelogistic.warehaus.BoxRepository;
import dronelogistic.warehaus.BoxSpecification;
import dronelogistic.warehaus.Terminal;
import dronelogistic.warehaus.cargoloadprocess.CargoLoadServiceImpl;
import dronelogistic.warehouse.employee.WarehouseEmployeeService;

public class CargoLoadServiceTest {
    
    @Inject CargoLoadServiceImpl serviceundertest;
    
    @Mock OrdersInformationService ordersInformationService;
    @Mock WarehouseEmployeeService warehouseEmployeeService;
    
    public OrderAndCargoInformation orderAndCargoInformation;
    
    @Mock BoxRepository boxRepository;
    @Mock DroneRepository droneRepository;
    
    private Drone drone;
    private Terminal terminal;
    private AtomicInteger droneId = new AtomicInteger(1000);
    private AtomicInteger cargoId = new AtomicInteger(1000);
    private AtomicInteger terminalId = new AtomicInteger(1000);
    private AtomicInteger taskID = new AtomicInteger(1000);
    private AtomicInteger boxID = new AtomicInteger(1000);
    private String droneType = "T4 v1";
    
    @Inject TestEvent<DroneLoadedEvent> droneLoadedEvents = new TestEvent<>();
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldCreateTaskForEmployee() {
        
        //create drone
        drone = new Drone(nextDroneID(), droneType);
        terminal = new Terminal(nextTerminaID());
        drone.dockInTerminal(terminal);
        
        // create Cargo deliverable with Drone
        Integer cargoID = OrderAndCargoInformationBuilder.nextCargoID();
        orderAndCargoInformation = aCargo().likeSmallGift().withCargoID(cargoID).build();
        prepareCargoDeliverableWithDrone(orderAndCargoInformation);
        // drone can take off
        DroneTakeOffDecision droneTakeOffDecisionEvent = new DroneTakeOffDecision(drone, cargoID);
        
        // find an appropiate box
        Box boxInMem = boxInMem(orderAndCargoInformation.getSize(), orderAndCargoInformation.getWeightInGrams());
        prepareBoxRepositoryfindAppropriateBox(boxInMem);
        
        //test the cargo load service, business method choose a Box
        serviceundertest.startCargoLoadProcess(droneTakeOffDecisionEvent);
        
        Mockito.verify(warehouseEmployeeService).addNewTask(cargoID, terminal.getTerminalID(), Mockito.anyInt());
    }
    
    @Test
    public void  testConfirmLoad() {
        
        Integer droneID = nextDroneID();
        Integer boxID = nextBoxID();
        prepareDroneRepositoryFindDrone(droneID);
        prepareBoxRepositoryfindBox(boxID);
        
        serviceundertest.confirmLoad(droneID, nextCargoID(), nextTaskID(), boxID);
        assertThat(droneLoadedEvents.getEvents()).isNotEmpty();
             
    }
    
    private void prepareCargoDeliverableWithDrone(OrderAndCargoInformation orderAndCargoInformation) {
        Mockito.when(ordersInformationService.getOrderAndCargoInformation(orderAndCargoInformation.getCargoID()))
                .thenReturn(orderAndCargoInformation);
    }
    
    private void prepareBoxRepositoryfindAppropriateBox(Box box){  
        Mockito.when(boxRepository.findAppropriateBox(Mockito.<BoxSpecification>any())).thenReturn(box);   
    }
    
    private void prepareBoxRepositoryfindBox(int boxID){
        Size size = Size.newSizeInMilimeters(1000, 1000, 1000);
        int weightInGrams = 1000;
        
        Mockito.when(boxRepository.findBox(boxID)).thenReturn(boxInMem(size, weightInGrams));   
    }
    
    private void prepareDroneRepositoryFindDrone(Integer droneID){
        drone = new Drone(droneID, droneType);
        Mockito.when(droneRepository.findDrone(droneID)).thenReturn(drone);
    }
    
    private Box boxInMem(Size size, int weightInGrams){
        
        //create a box specification
        BoxSpecification boxSpecification = new BoxSpecification(size, weightInGrams);
        
        //create a box for test according to the specification 
        return new Box(nextBoxID(), boxSpecification.getSize(), boxSpecification.getWeightInGrams(), boxSpecification.getBoxType());
    }
    
    private int nextDroneID(){
        return droneId.incrementAndGet();
    }
    
    private int nextCargoID(){
        return cargoId.incrementAndGet();
    }
    
    private int nextTerminaID(){
        return terminalId.incrementAndGet();
    }
    
    private int nextTaskID(){
        return taskID.incrementAndGet();
    }
    
    private int nextBoxID(){
        return boxID.incrementAndGet();
    }
    
}
