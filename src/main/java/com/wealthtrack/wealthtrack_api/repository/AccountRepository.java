package com.wealthtrack.wealthtrack_api.repository;

import com.wealthtrack.wealthtrack_api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
 //inherit a full CRUD surface: save, findById, findAll, deleteById, existsById, 
 // count, and more. We don't need a single custom query for accounts, so 
 // the body stays empty.
public interface AccountRepository extends JpaRepository<Account,Long> {
    
}
