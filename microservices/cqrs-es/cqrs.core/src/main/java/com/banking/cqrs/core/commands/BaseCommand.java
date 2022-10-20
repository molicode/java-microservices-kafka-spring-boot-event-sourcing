package com.banking.cqrs.core.commands;

import com.banking.cqrs.core.messages.Messages;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class BaseCommand extends Messages {

  public BaseCommand(String id) {
    super(id);
  }

}
