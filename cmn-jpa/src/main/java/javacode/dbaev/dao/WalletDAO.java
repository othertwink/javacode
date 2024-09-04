package javacode.dbaev.dao;
import javacode.dbaev.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletDAO extends JpaRepository<Wallet, UUID> {
        Optional<Wallet> findByWalletId(UUID walletId);
}
