package javacode.dbaev.controller;

import javacode.dbaev.entity.WalletRequest;
import javacode.dbaev.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RequestController {
    @Autowired
    private WalletService walletService;

    @PostMapping("/wallet")
    public ResponseEntity<String> updateWallet(@RequestBody WalletRequest request) {
        try {
//            System.out.println(request.getWalletId().toString() + " " + request.getOperationType() + " " + request.getAmount() + " data");
            walletService.changeBalance(request.getWalletId(), request.getOperationType(), request.getAmount());
            return ResponseEntity.ok("Операция прошла успешно");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Непредвиденная ошибка на стороне сервера");
        }
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<Double> getBalance(@PathVariable UUID walletId) {
        try {
            double balance = walletService.getBalance(walletId);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @ExceptionHandler
    public ResponseEntity<String> handleInvalidJson(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный JSON формат");
    }

}
