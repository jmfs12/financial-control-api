package com.jmfs.financial_control_api.controller;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.service.spec.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountDTO> createAccount(@RequestHeader String token, @RequestBody AccountDTO accountDTO) {
        accountService.createAccount(token.substring(7), accountDTO);
        return ResponseEntity.status(HttpStatus.OK).body(accountDTO);
    }

    @GetMapping("/{name}")
    public ResponseEntity<AccountDTO> getAccount(@RequestHeader String token, @PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(
                accountService.getAccount(token.substring(7), name)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<Page<AccountDTO>> getAllAccounts(@RequestHeader String token, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
                accountService.getAllAccountsByUser(token, pageable)
        );
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<AccountDTO> deleteAccount(@RequestHeader String token, @PathVariable String name) {
        accountService.deleteAccount(token.substring(7), name);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<AccountDTO> updateAccount(@RequestHeader String token, @RequestBody AccountDTO accountDTO) {
        accountService.updateAccount(token.substring(7), accountDTO);
        return ResponseEntity.status(HttpStatus.OK).body(accountDTO);
    }

}
