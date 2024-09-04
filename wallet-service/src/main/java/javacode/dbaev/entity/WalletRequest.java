package javacode.dbaev.entity;

import javacode.dbaev.entity.enums.OperationType;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(exclude = "walletId")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequest {
    private UUID walletId;
    private OperationType operationType;
    private double amount;
}
