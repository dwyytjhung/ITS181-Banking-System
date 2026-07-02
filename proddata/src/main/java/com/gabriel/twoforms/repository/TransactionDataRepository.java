package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.TransactionData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TransactionDataRepository extends CrudRepository<TransactionData, String> {
    List<TransactionData> findByAccountId(String accountId);
}
