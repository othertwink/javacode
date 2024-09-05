package javacode.dbaev.service.impl;

import jakarta.annotation.PostConstruct;
import javacode.dbaev.dao.WalletDAO;
import javacode.dbaev.entity.Wallet;
import javacode.dbaev.entity.enums.OperationType;
import javacode.dbaev.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WalletServiceImpl implements WalletService {

    @PostConstruct
    public void init() {
            System.out.println("WalletServiceImpl is getting up for the first time... Creating random UUID Wallet");
            Wallet wallet = Wallet.builder()
                    .walletId(UUID.randomUUID())
                    .balance(10000.0)
                    .version(0L)
                    .build();
            walletDAO.save(wallet);
            System.out.println("New Wallet UUID " + wallet.getWalletId() + " created");
    }

    @Autowired
    private WalletDAO walletDAO;

    @Override
    @Transactional
    public void changeBalance(UUID walletId, OperationType operationType, double amount) {
        boolean updated = false;

        int maxRetries = 5;
        int attempt = 0;

        while (!updated) {
            try {
                attempt++;

                Wallet wallet = walletDAO.findByWalletId(walletId)
                        .orElseThrow(() -> new IllegalArgumentException("Счёт не найден"));

                if (operationType.equals(OperationType.DEPOSIT)) {
                    wallet.setBalance(wallet.getBalance() + amount);
                } else if (operationType.equals(OperationType.WITHDRAW)) {
                    if (wallet.getBalance() < amount) {
                        throw new IllegalArgumentException("Сумма списания слишком велика");
                    }
                    wallet.setBalance(wallet.getBalance() - amount);
                } else {
                    throw new IllegalArgumentException("Неизвестный тип операции");
                }


                walletDAO.save(wallet);
                updated = true;

            } catch (OptimisticLockingFailureException e) {
                if (attempt >= maxRetries) {
                    throw new IllegalArgumentException("Не удалось обновить баланс после " + maxRetries + " попыток");
                }
                try {
                    int delay = ThreadLocalRandom.current().nextInt(20, 500);
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Операция была прервана", ie);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double getBalance(UUID walletId) {
        Wallet wallet = walletDAO.findByWalletId(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Счёт не найден"));
        return wallet.getBalance();
    }
}
