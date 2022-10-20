package com.banking.account.query.api.queries;

import static com.banking.account.query.api.dto.EqualityType.GREATER_THAN;

import java.util.ArrayList;
import java.util.List;

import com.banking.account.query.domain.AccountRepository;
import com.banking.account.query.domain.BankAccount;
import com.banking.cqrs.core.domain.BaseEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountQueryHandler implements QueryHandler {

  private final AccountRepository accountRepository;

  public AccountQueryHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public List<BaseEntity> handle(FindAllAccountsQuery query) {
    Iterable<BankAccount> bankAccounts = accountRepository.findAll();
    List<BaseEntity> bankAccountList = new ArrayList<>();

    bankAccounts.forEach(bankAccountList::add);

    return bankAccountList;
  }

  @Override
  public List<BaseEntity> handle(FindAccountByIdQuery query) {
    var bankAccount = accountRepository.findById(query.getId());
    if (bankAccount.isEmpty()) {
      return null;
    }

    List<BaseEntity> bankAccountList = new ArrayList<>();
    bankAccountList.add(bankAccount.get());
    return bankAccountList;
  }

  @Override
  public List<BaseEntity> handle(FindAccountByHolderQuery query) {
    var bankAccount = accountRepository.findByAccountHolder(query.getAccountHolder());
    if (bankAccount.isEmpty()) {
      return null;
    }

    List<BaseEntity> bankAccountList = new ArrayList<>();
    bankAccountList.add(bankAccount.get());
    return bankAccountList;
  }

  @Override
  public List<BaseEntity> handle(FindAccountWithBalanceQuery query) {
    return query.getEqualityType() == GREATER_THAN
        ? accountRepository.findByBalanceGreaterThan(query.getBalance())
        : accountRepository.findByBalanceLessThan(query.getBalance());
  }
}