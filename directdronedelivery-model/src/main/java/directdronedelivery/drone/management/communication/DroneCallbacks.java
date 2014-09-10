package directdronedelivery.drone.management.communication;

public interface DroneCallbacks {
    
    void batteryLow(double level);
    
    void positionChanged(int position);
    
    void allert(String info);
    
}
