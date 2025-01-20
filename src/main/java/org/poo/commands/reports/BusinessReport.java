package org.poo.commands.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.builder.ObjectNodeBuilder;
import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;

public final class BusinessReport {
    private final Mappers mappers;
    private final ExecutionCommand input;

    public BusinessReport(final ExecutionCommand input, final Mappers mappers) {
        this.mappers = mappers;
        this.input = input;
    }
    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (!account.getAccountType().equals("business")) {
            throw new IllegalArgumentException("Account type not business");
        }
        if (input.getType().equals("transaction")) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            ObjectNode outputNode = objectMapper.createObjectNode();
            try {
                objectNode.put("command", "businessReport");
                objectNode.put("timestamp", input.getTimestamp());
                outputNode.put("IBAN", account.getIban());
                outputNode.put("balance", account.getBalance());
                outputNode.put("currency", account.getCurrency());
                objectNode.put("spending limit", account
                        .getBusinessEntities()
                        .getFirst()
                        .getSpendingLimit());
                objectNode.put("deposit limit", account
                        .getBusinessEntities()
                        .getFirst()
                        .getDepositLimit());
            } catch (RuntimeException e) {
                return;
            }
            double totalSpent = 0;
            double totalDeposited = 0;
            ArrayNode managersArray = objectMapper.createArrayNode();
            ArrayNode employeesArray = objectMapper.createArrayNode();
            for (BusinessEntity businessEntity : account.getBusinessEntities()) {
                double moneySpent = businessEntity.getMoneySpent();
                double moneyAdded = businessEntity.getMoneyAdded();
                totalSpent += moneySpent;
                totalDeposited += moneyAdded;
                String name = businessEntity.getBusinessUser().getFirstName() + " "
                            + businessEntity.getBusinessUser().getLastName();
                ObjectNode managerNode =  new ObjectNodeBuilder().put("name", name)
                        .put("spent", moneySpent)
                        .put("deposited", moneyAdded).build();
                if (businessEntity.getRole().equals("manager")) {
                    managersArray.add(managerNode);
                } else {
                    employeesArray.add(managerNode);
                }
            }
            objectNode.set("managers", managersArray);
            objectNode.set("employees", employeesArray);
            objectNode.put("total spent", totalSpent);
            objectNode.put("total deposited", totalDeposited);
        }
    }
}
