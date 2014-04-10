package dronelogistic.warehouse;

import static dronelogistic.orderinformations.OrderAndCargoInformationBuilder.aCargo;
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
import dronelogistic.comandcenter.DroneTakeOffDecision;
import dronelogistic.comandcenter.VesselChooseProcess;
import dronelogistic.dronflightcontrol.AvaliableDronesBuilder;
import dronelogistic.dronflightcontrol.DronFlightControlService;
import dronelogistic.dronflightcontrol.Drone;
import dronelogistic.dronflightcontrol.DroneProblem;
import dronelogistic.dronflightcontrol.DroneProblemType;
import dronelogistic.dronflightcontrol.DroneRepository;
import dronelogistic.orderinformations.OrderAndCargoInformation;
import dronelogistic.orderinformations.OrdersInformationService;
import dronelogistic.warehaus.BoxStockRepository;
import dronelogistic.warehaus.BoxSpecification;
import dronelogistic.warehaus.BoxType;
import dronelogistic.warehaus.Terminal;
import dronelogistic.warehaus.cargoloadprocess.CargoLoadProcessService;
import dronelogistic.warehaus.cargoloadprocess.DroneLoadedEvent;
import dronelogistic.warehouse.employee.CargoLoadTask;
import dronelogistic.warehouse.employee.WarehouseEmployeeService;

public class CargoLoadProcessServiceTest {
    
    @Inject CargoLoadProcessService processUnderTest;
    
    @Mock OrdersInformationService ordersInformationService;
    @Mock WarehouseEmployeeService warehouseEmployeeService;
    @Mock DronFlightControlService dronFlightControlService;
    @Mock VesselChooseProcess vesselChooseProcess;
    @Mock BoxStockRepository boxStockRepository;
    @Mock DroneRepository droneRepository;
    @Inject TestEvent<DroneLoadedEvent> droneLoadedEvent = new TestEvent<>();
    @Captor ArgumentCaptor<LinkedList<DroneProblem>> problemsCaptor;
    
    private String droneType = "T4 v1";
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldCreateCargoLoadTaskForEmployeeAndDecrementBoxStocks() {
        // given
        // TODO MM: create DroneBuilder
        Drone drone = AvaliableDronesBuilder.newDrone(droneType);
        Terminal terminal = new Terminal(1);
        drone.dockInTerminal(terminal);
        Mockito.when(droneRepository.findDrone(drone.getDroneID())).thenReturn(drone);
        
        OrderAndCargoInformation orderAndCargoInformation = aCargo().likeSmallGift().build();
        Mockito.when(ordersInformationService.getOrderAndCargoInformation(orderAndCargoInformation.getCargoID()))
                .thenReturn(orderAndCargoInformation);
        Mockito.when(boxStockRepository.decrementStockOfAppropriateBoxes(Mockito.<BoxSpecification> any())).thenReturn(
                BoxType.SMALL);
        
        // when
        DroneTakeOffDecision droneTakeOffDecisionEvent = new DroneTakeOffDecision(drone,
                orderAndCargoInformation.getCargoID());
        processUnderTest.startCargoLoadProcess(droneTakeOffDecisionEvent);
        
        // then
        Mockito.verify(warehouseEmployeeService).addCargoLoadTask(orderAndCargoInformation, drone, BoxType.SMALL);
        Mockito.verify(boxStockRepository).decrementStockOfAppropriateBoxes(Mockito.<BoxSpecification> any());
    }
    
    @Test
    public void shouldCloseTaskAttachCargoToDroneAndEmitDroneLoadEventAfterTaskConfirmation() {
        // given
        int taskID = 1313;
        int terminalID = 1;
        int cargoID = 11;
        Integer droneID = 66;
        
        Drone drone = AvaliableDronesBuilder.newDrone(droneID, droneType);
        Terminal terminal = new Terminal(1);
        drone.dockInTerminal(terminal);
        Mockito.when(droneRepository.findDrone(drone.getDroneID())).thenReturn(drone);
        
        // when
        CargoLoadTask task = new CargoLoadTask(taskID, cargoID, droneID, terminalID, BoxType.SMALL);
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
        int taskID = 1313;
        int terminalID = 1;
        int cargoID = 11;
        Integer droneID = 66;
        
        Drone drone = AvaliableDronesBuilder.newDrone(droneID, droneType);
        Terminal terminal = new Terminal(1);
        drone.dockInTerminal(terminal);
        Mockito.when(droneRepository.findDrone(drone.getDroneID())).thenReturn(drone);
        
        // when
        CargoLoadTask task = new CargoLoadTask(taskID, cargoID, droneID, terminalID, BoxType.SMALL);
        task.addProblem(DroneProblemType.LOGISTIC, "Drone stolen...");
        processUnderTest.abortManuallCargoLoadTask(task);
        
        // then
        Mockito.verify(boxStockRepository).revertStockOfBoxes(BoxType.SMALL);
        Mockito.verify(warehouseEmployeeService).closeTask(taskID);
        
        Mockito.verify(vesselChooseProcess).handleCargoProblems(Mockito.eq(cargoID), problemsCaptor.capture());
        assertThat(problemsCaptor.getValue()).containsOnly(new DroneProblem(DroneProblemType.LOGISTIC, "Drone stolen..."));
        Mockito.verify(dronFlightControlService).handleDroneProblems(Mockito.eq(droneID), problemsCaptor.capture());
        assertThat(problemsCaptor.getValue()).containsOnly(new DroneProblem(DroneProblemType.LOGISTIC, "Drone stolen..."));
        
        // TODO MM: create DroneAssert
        assertThat(droneLoadedEvent.getEvents()).isEmpty();
    }
}
