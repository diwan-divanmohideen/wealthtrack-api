package com.wealthtrack.wealthtrack_api.model;

import com.wealthtrack.wealthtrack_api.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Builder // Builder pattern - instead of new Account(a,b,c,d)
// Can use Account.builder().name(..).type(..).build()
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //many transactions belong to one account. 
    // LAZY means the parent Account isn't loaded from the DB unless 
    //   you actually touch it (avoids needless joins).
    //optional = false enforces that a transaction must have an account
    //   (translates to a NOT NULL FK).
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    //Time stamp of a transaction is always fixed.
    @Column(nullable = false, updatable = false)
    private Instant timestamp;

}
