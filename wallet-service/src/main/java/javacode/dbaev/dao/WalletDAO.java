package javacode.dbaev.dao;
import jakarta.persistence.LockModeType;
import javacode.dbaev.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface WalletDAO extends JpaRepository<Wallet, UUID> {
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        Optional<Wallet> findByWalletId(UUID walletId);
}
