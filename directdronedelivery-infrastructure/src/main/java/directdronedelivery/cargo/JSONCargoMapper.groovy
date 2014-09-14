package directdronedelivery.cargo;

import groovy.json.JsonParserType
import groovy.json.JsonSlurper

public class JSONCargoMapper {

    public static OrderUpdatedEvent createOrderUpdatedEvent(String json) {
        def jsonObject = parse(json)
        OrderUpdatedEvent event = new OrderUpdatedEvent()
        event.cargoID = ID jsonObject.cargo
        return event
    }
    
    public static CargoAggregate createCargo(String json) {
        def jsonObject = parse(json)
        CargoAggregate cargo = new CargoAggregate()
        OrderAggregate order = new OrderAggregate()
        
        order.orderID = ID jsonObject.refnum
        order.acceptableDeliveryTime = createAcceptableDeliveryTime(NotEmpty (jsonObject.deliveryTimes))
        order.deliveryAddress = DeliveryAddress.newAddress(
            NotEmpty (jsonObject.deliverTo.city),
            NotEmpty (jsonObject.deliverTo.postalCode),
            NotEmpty (jsonObject.deliverTo.streetName),
            NotEmpty (jsonObject.deliverTo.houseNumber)
        )
        
        cargo.cargoID = ID jsonObject.cargoID
        cargo.dangerousGoods = jsonObject.isDangerous
        cargo.fixedOrientation = jsonObject.fixedOrientation
        cargo.fragileCommodity = jsonObject.isFragile
        cargo.size = Size.newSizeInMilimeters(
            jsonObject.dimensions.length,
            jsonObject.dimensions.width,
            jsonObject.dimensions.height
        )
        cargo.weightInGrams = jsonObject.weight
        cargo.order = order
        return cargo;
    }
    
    public static ConsignmentChangedEvent createConsignmentChangedEvent(String json) {
        def jsonObject = parse(json)
        ConsignmentChangedEvent event = new ConsignmentChangedEvent()
        event.consignmentID = ID jsonObject.consignment
        return event
    }
    
    public static AcceptableDeliveryTime createAcceptableDeliveryTime(def jsonArray) {
        // TODO reuse AcceptableDeliveryTimeBuilder
        return null
    }
    
    public static def ID = { id -> id }
    public static def NotEmpty = { val -> if (val == null || val.size == 0) val else null }
    
    private static def parse(String json) {
        def slurper = new JsonSlurper()
        slurper.setType(JsonParserType.INDEX_OVERLAY)
        return slurper.parseText(json)
    }
}
