package javacode.dbaev.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javacode.dbaev.dao.WalletDAO;
import javacode.dbaev.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class WalletServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletDAO walletDAO;

    @Test
    @Transactional
    void testChangeBalance() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(1000.0);
        walletDAO.save(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType("application/json")
                        .content("{ \"walletId\": \"" + walletId + "\", \"operationType\": \"DEPOSIT\", \"amount\": 500.0 }"))
                .andExpect(status().isOk());

        Wallet updatedWallet = walletDAO.findByWalletId(walletId).orElseThrow();
        assertThat(updatedWallet.getBalance()).isEqualTo(1500.0);
    }

    @Test
    @Transactional
    void testGetBalance() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(1000.0);
        walletDAO.save(wallet);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"balance\":1000.0}"));
    }

    @Test
    @Transactional
    void testGetBalanceNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound());
    }
}
