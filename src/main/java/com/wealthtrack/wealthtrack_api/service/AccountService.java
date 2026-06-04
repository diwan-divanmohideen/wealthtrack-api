package com.wealthtrack.wealthtrack_api.service;

import com.wealthtrack.wealthtrack_api.dto.AccountRequest;
import com.wealthtrack.wealthtrack_api.dto.AccountResponse;
import com.wealthtrack.wealthtrack_api.dto.BalanceResponse;
import com.wealthtrack.wealthtrack_api.dto.ContributionRequest;
import com.wealthtrack.wealthtrack_api.dto.TransactionResponse;
import com.wealthtrack.wealthtrack_api.dto.WithdrawalRequest;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest request);
    List<AccountResponse> getAllAccounts();
    AccountResponse getAccount(Long id);
    BalanceResponse getBalance(Long id);
    TransactionResponse contribute(Long id, ContributionRequest request);
    List<TransactionResponse> getContributions(Long id);
    TransactionResponse withdraw(Long id, WithdrawalRequest request);
    void deleteAccount(Long id);    
}
