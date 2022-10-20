package com.banking.account.cmd.infrastructure;

import java.util.Comparator;

import com.banking.account.cmd.domain.AccountAggregate;
import com.banking.cqrs.core.domain.AggregateRoot;
import com.banking.cqrs.core.handlers.EventSourcingHandler;
import com.banking.cqrs.core.infrastructure.EventStore;
import org.springframework.stereotype.Service;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

  private final EventStore eventStore;

  public AccountEventSourcingHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @Override
  public void save(AggregateRoot aggregate) {
    eventStore.saveEvents(aggregate.getId(), aggregate.getUncommittedChanges(), aggregate.getVersion());
    aggregate.markChangesAsCommitted();
  }

  @Override
  public AccountAggregate getById(String id) {
    var aggregate = new AccountAggregate();
    var events = eventStore.getEvent(id);
    if (events != null && !events.isEmpty()) {
      aggregate.replayEvents(events);
      var latestVersion = events.stream().map(x -> x.getVersion()).max(Comparator.naturalOrder());
      aggregate.setVersion(latestVersion.get());
    }

    return aggregate;
  }
}
