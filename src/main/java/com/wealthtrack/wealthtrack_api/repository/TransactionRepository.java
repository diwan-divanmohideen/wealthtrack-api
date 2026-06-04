package com.wealthtrack.wealthtrack_api.repository;

import com.wealthtrack.wealthtrack_api.enums.TransactionType;
import com.wealthtrack.wealthtrack_api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount_IdOrderByTimestampDesc(Long accountId);

    List<Transaction> findByAccount_IdAndTypeOrderByTimestampDesc(Long accountId, TransactionType type);
}
