package dronelogistic.dronesystem.api;

import java.rmi.Remote;

import dronelogistic.comandcenter.Route;

public interface DroneSytem extends Remote {
    
    public boolean uploadRoute(Integer droneId, Route route);
    
    public boolean getDroneStatus();
    
    public boolean startDrone();
    
}
