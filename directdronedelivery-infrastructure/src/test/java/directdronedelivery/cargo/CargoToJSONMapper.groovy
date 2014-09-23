package directdronedelivery.cargo

import groovy.json.JsonBuilder;

class CargoToJSONMapper {
    
    public static String toLogisticSystemJSON(CargoAggregate cargo, List<String> acceptableDeliveryTimeIntervals) {
        def builder = new JsonBuilder([
            "refnum" : cargo.order.orderID,
            "deliveryTimes" : acceptableDeliveryTimeIntervals,
            "deliverTo" : [
                "city" : cargo.order.deliveryAddress.city,
                "postalCode" : cargo.order.deliveryAddress.postalCode,
                "streetName" : cargo.order.deliveryAddress.streetName,
                "houseNumber" : cargo.order.deliveryAddress.houseNumber,
                "flat" : cargo.order.deliveryAddress.flatNumber
            ],
            "cargo" : cargo.cargoID,
            "dangerous" : cargo.dangerousGoods,
            "fixedOrientation" : cargo.fixedOrientation,
            "fragile" : cargo.fragileCommodity,
            "dimensions" : [
                "length" : cargo.size.length,
                "width" : cargo.size.width,
                "height" : cargo.size.height
            ],
            "weight" : cargo.weightInGrams
        ]
        )
        return builder.toString()
    }
}
