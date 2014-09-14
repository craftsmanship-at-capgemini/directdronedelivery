package logisticsystem.facade;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.Queue;

import directdronedelivery.warehouse.process.DroneDeliveryDecisionEvent;
import directdronedelivery.warehouse.process.JSONWarehouseProcessesIOMapper;

@Stateless
@LocalBean
public class JMSOutcomingMessages {
    
    @Resource JMSContext jmsContext;
    @Resource(lookup = "jms/LogisticSystemQueue") Queue queue;
    
    void externalizeAsJMSMessage(@Observes DroneDeliveryDecisionEvent event) {
        String json = JSONWarehouseProcessesIOMapper.marshallDroneDeliveryDecisionEventForLogisticSystem(event);
        
        jmsContext.createProducer()
                .setProperty("event-kind", "external-delivery-method")
                .setDeliveryMode(DeliveryMode.PERSISTENT)
                .setPriority(2)
                .send(queue, json);
    }
    
}
