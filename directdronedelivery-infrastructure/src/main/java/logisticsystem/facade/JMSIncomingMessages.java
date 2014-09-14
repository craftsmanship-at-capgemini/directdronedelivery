package logisticsystem.facade;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import directdronedelivery.cargo.ConsignmentChangedEvent;
import directdronedelivery.cargo.JSONCargoMapper;
import directdronedelivery.cargo.OrderUpdatedEvent;
import directdronedelivery.warehouse.process.JSONWarehouseProcessesIOMapper;
import directdronedelivery.warehouse.process.NewCargoInWarehausEvent;
import directdronedelivery.warehouse.process.TruckDeliveryStartedEvent;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/LogisticSystemTopic"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "messageSelector",
                propertyValue = "event-kind IN ('first-cargo-scan', 'order-changed', 'consignment-changed', 'consignment-dispatched')")
})
public class JMSIncomingMessages implements MessageListener {
    
    @Resource MessageDrivenContext jmsContext;
    
    @EJB RESTCachedCargoRepository restCachedCargoRepository;
    @Inject Event<NewCargoInWarehausEvent> newCargoInWarehausEvent;
    @Inject Event<OrderUpdatedEvent> orderUpdatedEvent;
    @Inject Event<ConsignmentChangedEvent> consignmentChangedEvent;
    @Inject Event<TruckDeliveryStartedEvent> truckDeliveryStartedEvent;
    
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String eventKind = message.getStringProperty("event-kind");
                String json = message.getBody(String.class);
                switch (eventKind) {
                case "first-cargo-scan": {
                    NewCargoInWarehausEvent event =
                            JSONWarehouseProcessesIOMapper.createNewCargoInWarehausEvent(json);
                    this.newCargoInWarehausEvent.fire(event);
                    break;
                }
                case "order-changed": {
                    OrderUpdatedEvent event = JSONCargoMapper.createOrderUpdatedEvent(json);
                    restCachedCargoRepository.invalidateCacheForCargo(event.getCargoID());
                    this.orderUpdatedEvent.fire(event);
                    break;
                }
                case "consignment-changed": {
                    ConsignmentChangedEvent event =
                            JSONCargoMapper.createConsignmentChangedEvent(json);
                    this.consignmentChangedEvent.fire(event);
                    break;
                }
                case "consignment-dispatched": {
                    TruckDeliveryStartedEvent event =
                            JSONWarehouseProcessesIOMapper.createTruckDeliveryStartedEvent(json);
                    this.truckDeliveryStartedEvent.fire(event);
                    break;
                }
                }
            }
        } catch (JMSException e) {
            jmsContext.setRollbackOnly();
        }
    }
    
}
