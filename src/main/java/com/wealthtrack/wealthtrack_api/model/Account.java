package com.wealthtrack.wealthtrack_api.model;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name = "accounts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Display name of the account itself (e.g. "My Roth IRA").
    // Distinct from ownerName (the person). Both AccountRequest and
    // AccountResponse carry this field, so the entity needs it too.
    @Column(nullable = false)
    private String name;

    @Column (nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private int ownerAge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Setter
    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
}
