package testing;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

import lombok.ToString;

import org.fest.assertions.api.Assertions;

@ToString
public class TestEvent<T> implements Event<T> {
    
    private List<T> events = new LinkedList<>();
    
    public List<T> getEvents() {
        return events;
    }
    
    public T getFirstEvent() {
        Assertions.assertThat(events).hasSize(1);
        return events.get(0);
    }
    
    @Override
    public void fire(T event) {
        this.getEvents().add(event);
    }
    
    @Override
    public Event<T> select(Annotation... arg0) {
        return this;
    }
    
    @Override
    public <U extends T> Event<U> select(Class<U> eventSubtype, Annotation... annotations) {
        return safeCastToSubtype(eventSubtype);
    }
    
    @Override
    public <U extends T> Event<U> select(TypeLiteral<U> eventSubtype, Annotation... annotations) {
        return safeCastToSubtype(eventSubtype.getRawType());
    }
    
    @SuppressWarnings("unchecked")
    private <U extends T> Event<U> safeCastToSubtype(Class<U> eventSubtype) {
        if (eventSubtype.isInstance(this)) {
            return (Event<U>) this;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
}
