package directdronedelivery.cargo;

import groovy.json.JsonParserType
import groovy.json.JsonSlurper

public class JSONCargoMapper {
    
    /**
     * @param json example content: <pre>
     * {
     *     "cargo": 1314
     * }
     * </pre>
     * @return valid OrderUpdatedEvent
     */
    public static OrderUpdatedEvent createOrderUpdatedEvent(String json) {
        def jsonObject = parse(json)
        OrderUpdatedEvent event = new OrderUpdatedEvent()
        event.cargoID = ID jsonObject.cargo
        return event
    }
    
    /**
     * Creates CargoAggregate instance based on JSON representation from LogisticSystem.
     * 
     * @param json example content: <pre>
     * {
     *     "refnum": 1314,
     *     "deliveryTimes": [
     *         "07:30-09:00",
     *         "17:00-23:30"
     *     ],
     *     "deliverTo": {
     *         "city": "Wroc\u0142aw",
     *         "postalCode": "50-540",
     *         "streetName": "Jableczna",
     *         "houseNumber": "13",
     *         "flat": "123A"
     *     },
     *     "cargo": 1314,
     *     "dangerous": false,
     *     "fixedOrientation": false,
     *     "fragile": false,
     *     "dimensions": {
     *         "length": 250,
     *         "width": 100,
     *         "height": 10
     *     },
     *     "weight": 850
     * }
     * </pre>
     * @return valid CargoAggregate
     */
    public static CargoAggregate createCargo(String json) {
        def jsonObject = parse(json)
        CargoAggregate cargo = new CargoAggregate()
        OrderAggregate order = new OrderAggregate()
        cargo.order = order
        
        order.orderID = ID jsonObject.refnum
        order.acceptableDeliveryTime = createAcceptableDeliveryTime(NotEmpty (jsonObject.deliveryTimes))
        order.deliveryAddress = DeliveryAddress.newAddress(
                NotEmpty (jsonObject.deliverTo.city),
                Matches (jsonObject.deliverTo.postalCode, /\d\d-\d\d\d/),
                NotEmpty (jsonObject.deliverTo.streetName),
                OptionalStringVal (jsonObject.deliverTo.houseNumber),
                OptionalStringVal (jsonObject.deliverTo.flat)
                )
        
        cargo.cargoID = ID jsonObject.cargo
        cargo.dangerousGoods = BooleanVal jsonObject.dangerous
        cargo.fixedOrientation = BooleanVal jsonObject.fixedOrientation
        cargo.fragileCommodity = BooleanVal jsonObject.fragile
        cargo.size = Size.newSizeInMilimeters(
                IntegerVal (jsonObject.dimensions.length),
                IntegerVal (jsonObject.dimensions.width),
                IntegerVal (jsonObject.dimensions.height)
                )
        cargo.weightInGrams = IntegerVal jsonObject.weight
        
        return cargo;
    }
    
    /**
     * @param json example content: <pre>
     * {
     *     "consignment": 1314
     * }
     * </pre>
     * @return valid ConsignmentChangedEvent
     */
    public static ConsignmentChangedEvent createConsignmentChangedEvent(String json) {
        def jsonObject = parse(json)
        ConsignmentChangedEvent event = new ConsignmentChangedEvent()
        event.consignmentID = ID jsonObject.consignment
        return event
    }
    
    /**
     * @param json example content: <pre>
     * [
     *         "07:30-09:00",
     *         "17:00-23:30"
     * ]
     * </pre>
     * @return valid AcceptableDeliveryTime
     */
    public static AcceptableDeliveryTime createAcceptableDeliveryTime(def jsonArray) {
        def builder = AcceptableDeliveryTimeBuilder.aTime();
        jsonArray.each { interval ->
            builder.addInterval(Matches.call(interval, /\d\d:\d\d/))
        }
        return builder.build()
    }
    
    public static def ID = { val -> IntegerVal.call(NotNull.call(val)) }
    public static def NotNull = { val -> if (val != null) val else val }
    public static def NotEmpty = { val -> if (val != null && val.size() > 0) val else val }
    public static def IntegerVal = { val -> if (NotNull.call(val) instanceof Integer) val else val }
    public static def BooleanVal = { val -> if (NotNull.call(val) instanceof Boolean) val else val }
    public static def OptionalStringVal = { val -> if (val == null || val instanceof String) val else val }
    public static def Matches = { val, regexp -> if (NotNull.call(val) =~ regexp) val else val }
    
    private static def parse(String json) {
        def slurper = new JsonSlurper()
        slurper.setType(JsonParserType.INDEX_OVERLAY)
        return slurper.parseText(json)
    }
}
