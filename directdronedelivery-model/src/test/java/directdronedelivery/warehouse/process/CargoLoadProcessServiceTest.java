package directdronedelivery.warehouse.process;

import static directdronedelivery.cargo.OrderAndCargoInformationBuilder.aCargo;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.LinkedList;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.TestEvent;
import testing.Testing;
import directdronedelivery.cargo.CargoAggregate;
import directdronedelivery.cargo.CargoRepository;
import directdronedelivery.drone.DroneAggregate;
import directdronedelivery.drone.DroneBuilder;
import directdronedelivery.drone.DroneRepository;
import directdronedelivery.drone.management.DronControlService;
import directdronedelivery.warehouse.BoxStockRepository;
import directdronedelivery.warehouse.BoxType;
import directdronedelivery.warehouse.Problem;
import directdronedelivery.warehouse.ProblemType;
import directdronedelivery.warehouse.TerminalEntity;
import directdronedelivery.warehouse.WarehouseTopologyFactory;
import directdronedelivery.warehouse.businessrules.BoxChooseRule;
import directdronedelivery.warehouse.employee.CargoLoadTask;
import directdronedelivery.warehouse.employee.WarehouseEmployeeTaskService;
import directdronedelivery.warehouse.process.CargoLoadProcessService;
import directdronedelivery.warehouse.process.DroneDeliveryDecisionEvent;
import directdronedelivery.warehouse.process.DroneLoadedEvent;
import directdronedelivery.warehouse.process.VesselChooseProcessService;

public class CargoLoadProcessServiceTest {
    
    @Inject CargoLoadProcessService processUnderTest;
    
    @Mock CargoRepository ordersInformationService;
    @Mock WarehouseEmployeeTaskService warehouseEmployeeService;
    @Mock DronControlService dronFlightControlService;
    @Mock VesselChooseProcessService vesselChooseProcess;
    @Mock BoxStockRepository boxStockRepository;
    @Mock DroneRepository droneRepository;
    @Inject TestEvent<DroneLoadedEvent> droneLoadedEvent = new TestEvent<>();
    @Captor ArgumentCaptor<LinkedList<Problem>> problemsCaptor;
    
    TerminalEntity terminal = WarehouseTopologyFactory.newTerminal(1);
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldCreateCargoLoadTaskForEmployeeAndDecrementBoxStocks() {
        // given
        DroneAggregate drone = DroneBuilder.aDrone().likeDocked4RotorsDrone()
                .dockedInTerminal(terminal).build();
        Mockito.when(droneRepository.findDrone(drone.getDroneID())).thenReturn(drone);
        
        CargoAggregate orderAndCargoInformation = aCargo().likeSmallGift().build();
        Mockito.when(ordersInformationService.findCargo(orderAndCargoInformation.getCargoID()))
                .thenReturn(orderAndCargoInformation);
        Mockito.when(boxStockRepository.decrementStockOfAppropriateBoxes(Mockito.<BoxChooseRule> any())).thenReturn(
                BoxType.SMALL);
        
        // when
        DroneDeliveryDecisionEvent droneTakeOffDecisionEvent = new DroneDeliveryDecisionEvent(drone.getDroneID(),
                orderAndCargoInformation.getCargoID());
        processUnderTest.startCargoLoadProcess(droneTakeOffDecisionEvent);
        
        // then
        Mockito.verify(warehouseEmployeeService).addCargoLoadTask(orderAndCargoInformation, drone, BoxType.SMALL);
        Mockito.verify(boxStockRepository).decrementStockOfAppropriateBoxes(Mockito.<BoxChooseRule> any());
    }
    
    @Test
    public void shouldCloseTaskAttachCargoToDroneAndEmitDroneLoadEventAfterTaskConfirmation() {
        // given
        Integer taskID = 1313;
        Integer cargoID = 11;
        Integer droneID = 66;
        DroneAggregate drone = DroneBuilder.aDrone().likeDocked4RotorsDrone()
                .withDroneID(droneID).dockedInTerminal(terminal).build();
        Mockito.when(droneRepository.findDrone(drone.getDroneID())).thenReturn(drone);
        
        // when
        CargoLoadTask task = new CargoLoadTask(taskID, cargoID, BoxType.SMALL, terminal.getTerminalID(), droneID);
        processUnderTest.confirmManuallCargoLoadDone(task);
        
        // then
        Mockito.verify(warehouseEmployeeService).closeTask(taskID);
        // TODO MM: create DroneAssert
        assertThat(droneLoadedEvent.getEvents()).isNotEmpty();
        assertThat(droneLoadedEvent.getFirstEvent().getCargoID()).isEqualTo(cargoID);
        assertThat(droneLoadedEvent.getFirstEvent().getDroneID()).isEqualTo(droneID);
    }
    
    @Test
    public void shouldRevertBoxStocksAndStartAbortProcedure() {
        // given
        Integer taskID = 1313;
        Integer cargoID = 11;
        Integer droneID = 66;
        DroneAggregate drone = DroneBuilder.aDrone().likeDocked4RotorsDrone()
                .withDroneID(droneID).dockedInTerminal(terminal).build();
        Mockito.when(droneRepository.findDrone(drone.getDroneID())).thenReturn(drone);
        
        // when
        CargoLoadTask task = new CargoLoadTask(taskID, cargoID, BoxType.SMALL, terminal.getTerminalID(), droneID);
        task.addProblem(ProblemType.DRONE_MISSING, "Drone stolen...");
        processUnderTest.abortManuallCargoLoadTask(task);
        
        // then
        Mockito.verify(boxStockRepository).revertStockOfBoxes(BoxType.SMALL);
        Mockito.verify(warehouseEmployeeService).closeTask(taskID);
        
        Mockito.verify(vesselChooseProcess).handleCargoProblems(Mockito.eq(cargoID), problemsCaptor.capture());
        assertThat(problemsCaptor.getValue()).containsOnly(
                new Problem(ProblemType.DRONE_MISSING, "Drone stolen..."));
        Mockito.verify(dronFlightControlService).handleDroneProblems(Mockito.eq(droneID), problemsCaptor.capture());
        assertThat(problemsCaptor.getValue()).containsOnly(
                new Problem(ProblemType.DRONE_MISSING, "Drone stolen..."));
        
        // TODO MM: create DroneAssert
        assertThat(droneLoadedEvent.getEvents()).isEmpty();
    }
}
