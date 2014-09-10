Real translation of business process into executable software is complicated and expensive. The translation in the opposite direction is even more difficult. In addition, minor changes to the business process can lead to drastic changes in the software. Can we minimize these problems? Yes we can! With Domain-Driven Design you can implement the software in business language. Domain-Driven Design allows you to extract the business processes and elements of these processes. Domain-Driven Design allows you to define a static model, use cases, data structures and functions.

Direct Delivery Company is the industry leader of transport services in terms of quality, profitability and innovation. Because the offer to the customers is based on new technologies, Direct Delivery Company launched a pilot project to use drones (small unmanned flaying vehicles) to transport small packages in the cities. With drones the packages can be quickly delivered regardless of the current road traffic. An important element is the new automation component of the system called Direct Drone Delivery. This component will be implemented based on Domain-Driven Design. This component consists of the following business processes: Vessel Choose Process, Cargo Load Process and Drone Start Process.

The Vessel Choose Process Service is the first one in the whole warehouse processes chain. The process checks and defines if delivery of a cargo with a drone is possible and profitable. If it is not, then the cargo is assigned to a consignment and delivered with conventional means of transport (e.g. a truck). There are some factors which can influence the decision about the way of delivery. These are:
-	the cargo specification:
-	the weight of the cargo must be below maximum load capacity of a drone
-	the cargo size (length, height, width) must be below maximum allowed size to be able to deliver by drone 
-	the cargo must not be fragile or dangerous to be delivered by a drone
-	weather conditions:
-	if the wind is stronger than 5,56 MPS then the cargo can’t be delivered by a drone
-	if the temperature is lower than 10 C or higher than 30 C  then the cargo can’t be delivered by a drone
-	if the air humidity is higher than 55% then the cargo can’t be delivered by a drone
-	the drone availability on Terminal in Warehouse

If a positive decision about delivery with a drone is taken, the Vessel Choose Process Service broadcasts the event DroneDeliveryDeciosonEvent which can be received by further processes of the process chain. The cargo is ready for the load process.

Cargo Load Process service supports the load process of the cargo on drones. The process consists mainly of the automatic steps but contains some manual actions as well, which have to be done by a warehouse employee. The Cargo Load Process begins with choosing an appropriate box for the cargo. The system chooses a box for the cargo according to the box specification:
-	if the cargo is small (size length < 300mm, height < 300mm, width < 300mm) the small box is chosen
-	if the cargo is big (size length < 500mm, height < 500mm, width < 500mm) the big box is chosen
-	otherwise box with type unknown is chosen 

Then the system delegates creation of a task (load the cargo) for a warehouse employee to the Warehouse Employee Service. After the task "load the cargo" has been completed, the warehouse employee confirms that the cargo has been loaded on the drone. The box is physically attached to the drone and the load task is closed (also via Warehouse Employee Service). At the end the service fires an event that the cargo is loaded. The warehouse employee has also a possibility to report a problem. In this case service notifies the Drone Control Service, which is responsible for processing of the reported problem. The load process is aborted. The Vessel Choose Process Service handles the problem, if it concerns the cargo.

The last element of the whole process in the warehouse is the Drone Start Process. The process is triggered by the event DroneLoadedEvent. The process calculates the route for the delivery in the first step and uploads it into the drone system. The upload occurs via Drone Communication Protocol which is responsible for the whole communication between the warehouse system and the onboard system of the drones. If the upload of the route succeeded, the start procedure begins. Before the drone takes off, the check list must be completed. The possible problems are forwarded to the Drone Control Service, which handles it.


