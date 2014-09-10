# Business Context
Direct Delivery Company is the industry leader of transport services in terms of quality, profitability and innovation. Because the offer to the customers is based on new technologies, Direct Delivery Company launched a pilot project to use drones (small unmanned flying vehicles) to transport small packages in the cities. With drones the packages can be quickly delivered regardless of the current road traffic.

The Program is after testing phase (phase 1). Currently all processes associated with the drone transport are manual and are not supported by our systems. Promising results from phase 1. lead to decision of implementing the new automation component of the system called Direct Drone Delivery. As a core software component in undiscovered business area it will be implemented based on Domain-Driven Design principles.

In phase 2. Direct Drone Delivery should support following business processes: Vessel Choose Process, Cargo Load Process and Drone Start Process.

The Vessel Choose Process Service is the first one in the warehouse processes chain related to drone delivery. The process checks and defines if delivery of a cargo with a drone is possible and profitable. After cargo is assigned to a consignment (truck delivery) profitability of drone delivery can be compared to to delivery with conventional means of transport (e.g. a truck). There are lot of factors which can influence the decision about the way of delivery. These are:
- the cargo specification:
- the weight of the cargo must be below maximum load capacity of a drone
- the cargo size (length, height, width) must be below maximum allowed size to be able to deliver by drone 
- the cargo must not dangerous to be delivered by a drone
- weather conditions:
- if the wind is stronger than 5,56 mps then the cargo can’t be delivered by a drone
- if the temperature is lower than 10 C or higher than 30 C  then the cargo can’t be delivered by a drone
- if the air humidity is higher than 55% then the cargo can’t be delivered by a drone
- the drone availability on Terminal in Warehouse

If a positive decision about delivery with a drone is taken, the Vessel Choose Process Service broadcasts the event DroneDeliveryDeciosonEvent which can be received by further processes of the process chain. The cargo is ready for the load process.

Cargo Load Process service supports the load process of the cargo on drones. The process consists mainly of the automatic steps but contains some manual actions as well, which have to be done by a warehouse employee. The Cargo Load Process begins with choosing an appropriate box for the cargo. The system chooses a box for the cargo according to the box specification:
- if the cargo is small the small box is chosen
- if the cargo is big the big box is chosen

Then the system delegates creation of a task (load the cargo) for a warehouse employee to the Warehouse Employee Service. After the task "load the cargo" has been completed, the warehouse employee confirms that the cargo has been loaded on the drone. The box is physically attached to the drone and the load task is closed (also via Warehouse Employee Service). At the end the service fires an event that the cargo is loaded. The warehouse employee has also a possibility to report a problem. In this case service notifies the Drone Control Service, which is responsible for processing of the reported problem. The load process is aborted. The Vessel Choose Process Service handles the problem, if it concerns the cargo.

The last element of the whole process in the warehouse is the Drone Start Process. The process is triggered by the event DroneLoadedEvent. The process calculates the route for the delivery in the first step and uploads it into the drone system. The upload occurs via Drone Communication Protocol which is responsible for the whole communication between the warehouse system and the onboard system of the drones. If the upload of the route succeeded, the start procedure begins. Before the drone takes off, the check list must be completed. The possible problems are forwarded to the Drone Control Service, which handles it.
