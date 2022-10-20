package com.banking.account.query;

import com.banking.account.query.api.queries.FindAccountByHolderQuery;
import com.banking.account.query.api.queries.FindAccountByIdQuery;
import com.banking.account.query.api.queries.FindAccountWithBalanceQuery;
import com.banking.account.query.api.queries.FindAllAccountsQuery;
import com.banking.account.query.api.queries.QueryHandler;
import com.banking.cqrs.core.infrastructure.QueryDispatcher;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QueryApplication {

  private final QueryDispatcher queryDispatcher;

  private final QueryHandler queryHandler;

  public QueryApplication(QueryDispatcher queryDispatcher, QueryHandler queryHandler) {
    this.queryDispatcher = queryDispatcher;
    this.queryHandler = queryHandler;
  }

  public static void main(String[] args) {
    SpringApplication.run(QueryApplication.class, args);
  }

  @PostConstruct
  public void registerHandlers() {
    queryDispatcher.registerHandler(FindAllAccountsQuery.class, queryHandler::handle);
    queryDispatcher.registerHandler(FindAccountByIdQuery.class, queryHandler::handle);
    queryDispatcher.registerHandler(FindAccountByHolderQuery.class, queryHandler::handle);
    queryDispatcher.registerHandler(FindAccountWithBalanceQuery.class, queryHandler::handle);
  }

}
