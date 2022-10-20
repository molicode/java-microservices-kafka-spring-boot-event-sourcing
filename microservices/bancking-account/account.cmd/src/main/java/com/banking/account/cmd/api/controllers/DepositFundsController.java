package com.banking.account.cmd.api.controllers;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.account.cmd.api.command.DepositFundsCommand;
import com.banking.account.common.dto.BaseResponse;
import com.banking.cqrs.core.exceptions.AggregateNotFoundException;
import com.banking.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/depositFunds")
public class DepositFundsController {

  private final Logger logger = Logger.getLogger(DepositFundsController.class.getName());

  private final CommandDispatcher commandDispatcher;

  public DepositFundsController(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<BaseResponse> depositFunds(@PathVariable(value = "id") String id,
      @RequestBody DepositFundsCommand command) {

    try {
      command.setId(id);
      commandDispatcher.send(command);
      return new ResponseEntity<>(new BaseResponse("El deposito de dinero fue exitoso"), HttpStatus.OK);
    } catch (IllegalStateException | AggregateNotFoundException e) {
      logger.log(Level.WARNING, MessageFormat.format("El cliente envio un request con errores {0} ", e.toString()));
      return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      var safeErrorMessage = MessageFormat.format("Errores mientras procesaba el request {id}", id);
      return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

}
