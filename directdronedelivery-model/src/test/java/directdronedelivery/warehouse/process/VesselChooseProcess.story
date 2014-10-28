The Vessel Choose Process is the first one in the warehouse processes chain related to drone delivery.
In scope of process first we checks if delivery of a cargo with a drone is possible.
After cargo is assigned to a consignment (related to truck delivery) profitability of drone delivery
can be compared to delivery with conventional means of transport (exactly a truck delivery of consignment).
There is lot of factors* which influence drone delivery decision.
*)factors: cargo spec., delivery place, acceptable delivery time, profitability, weather conditions, currently avaliable drones

Narrative:
In order to evaluate effectively and optimaly thousands of cargos incomming to warehause each day
As an employee responsible for deciding about drone delivery
I want to fully automate Vessel Choose Process

Scenario: dron delivery decision is taken after assignement to contingent

Given nice weather conditions
And a drone is avaliable in warehaus
When a cargo deliverable by the drone arrive warehaus
Then no decision can be taken for now
When cargo is assigned to consignment
Then process take decision to deliver the cargo with the drone

Scenario: dron delivery decision is taken after weather conditions changes to acceptable

Given really BAD weather conditions
And a drone is avaliable in warehaus
When a cargo deliverable by the drone is scaned in warehaus
Then no decision can be taken for now
When cargo is assigned to consignment
Then no decision can be taken for now
When weather conditions changes to acceptable
Then process take decision to deliver the cargo with the drone

