package javacode.dbaev.service;

import javacode.dbaev.entity.enums.OperationType;

import java.util.UUID;

public interface WalletService {
    void changeBalance(UUID walletId, OperationType operationType, double amount);
    Double getBalance(UUID walletId);
}
