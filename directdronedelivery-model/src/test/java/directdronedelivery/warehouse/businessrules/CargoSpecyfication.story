Specification of cargo deliverable by available drone types
Quadrocopter can deliver:
The maximum load capacity 2,5 kg
Box internal size: 250 x 150 x 100 mm
Hexacopter can deliver:
The maximum load capacity 5 kg
Box internal size: 350 x 250 x 250 mm

Some cargos can’t be rotated in box -> called: "fixed orientation" visualised as: "this side up" label
Dangerous Goods can’t be delivered with drone


Scenario: match cargo weight to drone type

Given a cargo with <weight>
When the cargo arrives to warehouse
Then any drone of <dronetypes> could be chosen

Examples:
|weight |dronetypes|
|2 kg   |QUADROCOPTER,HEXACOPTER|
|2.5 kg |QUADROCOPTER,HEXACOPTER|
|2.6 kg |HEXACOPTER|
|4.9 kg |HEXACOPTER|
|5 kg   |HEXACOPTER|
|5.1 kg ||


Scenario: match cargo size to drone type

Given a cargo with <size>
When the cargo arrives to warehouse
Then any drone of <dronetypes> could be chosen

Examples:
|size               |dronetypes|
|-- quadrocopter size boundary values
|250 x 150 x 100 mm |QUADROCOPTER,HEXACOPTER|
|251 x 150 x 100 mm |HEXACOPTER|
|250 x 151 x 100 mm |HEXACOPTER|
|250 x 150 x 101 mm |QUADROCOPTER,HEXACOPTER|
|-- hexacopter size boundary values
|350 x 250 x 250 mm |HEXACOPTER|
|351 x 250 x 250 mm ||
|350 x 251 x 250 mm ||
|350 x 250 x 251 mm ||


Scenario: support fixed orientation / "this side up" label

Given a cargo with <size> and <fixed> orientation
When the cargo arrives to warehouse
Then any drone of <dronetypes> could be chosen

Examples:
|size               |fixed|dronetypes|
|-- length x width x height - height can be fixed
|250 x 150 x 100 mm |yes  |QUADROCOPTER,HEXACOPTER|
|250 x 150 x 100 mm |no   |QUADROCOPTER,HEXACOPTER|
|250 x 100 x 150 mm |yes  |HEXACOPTER|
|250 x 100 x 150 mm |no   |QUADROCOPTER,HEXACOPTER|
|100 x 150 x 250 mm |yes  |HEXACOPTER|
|100 x 150 x 250 mm |no   |QUADROCOPTER,HEXACOPTER|
|150 x 250 x 100 mm |yes  |QUADROCOPTER,HEXACOPTER|
|100 x 150 x 250 mm |yes  |QUADROCOPTER,HEXACOPTER|


Scenario: exclude dangerous goods from drone delivery

Given a cargo with dangerous goods
When the cargo arrives to warehouse
Then it can't be deliver by drone
