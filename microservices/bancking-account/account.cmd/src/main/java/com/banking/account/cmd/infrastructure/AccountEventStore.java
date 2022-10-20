package com.banking.account.cmd.infrastructure;

import static java.util.stream.Collectors.toList;

import java.util.Date;
import java.util.List;

import com.banking.account.cmd.domain.AccountAggregate;
import com.banking.account.cmd.domain.EventStoreRepository;
import com.banking.cqrs.core.events.BaseEvent;
import com.banking.cqrs.core.events.EventModel;
import com.banking.cqrs.core.exceptions.AggregateNotFoundException;
import com.banking.cqrs.core.exceptions.ConcurrencyException;
import com.banking.cqrs.core.infrastructure.EventStore;
import com.banking.cqrs.core.producers.EventProducer;
import org.springframework.stereotype.Service;

@Service
public class AccountEventStore implements EventStore {

  private final EventStoreRepository eventStoreRepository;

  private final EventProducer eventProducer;

  public AccountEventStore(EventStoreRepository eventStoreRepository, EventProducer eventProducer) {
    this.eventStoreRepository = eventStoreRepository;
    this.eventProducer = eventProducer;
  }

  @Override
  public void saveEvents(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
    var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
    if (expectedVersion != -1 && eventStream.get(eventStream.size() - 1).getVersion() != expectedVersion) {
      throw new ConcurrencyException();
    }

    var version = expectedVersion;
    for (var event : events) {
      version++;
      event.setVersion(version);
      var eventModel = EventModel.builder()
          .timeStamp(new Date())
          .aggregateIdentifier(aggregateId)
          .aggregateType(AccountAggregate.class.getTypeName())
          .version(version)
          .eventType(event.getClass().getTypeName())
          .eventData(event)
          .build();

      var persistedEvent = eventStoreRepository.save(eventModel);

      if (persistedEvent != null) {
        eventProducer.produce(event.getClass().getSimpleName(), event);
      }
    }

  }

  @Override
  public List<BaseEvent> getEvent(String aggregateId) {
    var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
    if (eventStream == null || eventStream.isEmpty()) {
      throw new AggregateNotFoundException("LA cuenta del banco es incorrecta");
    }
    return eventStream.stream()
        .map(x -> x.getEventData())
        .collect(toList());
  }
}
