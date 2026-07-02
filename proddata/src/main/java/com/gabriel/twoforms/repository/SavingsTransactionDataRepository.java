package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.SavingsTransactionData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SavingsTransactionDataRepository extends CrudRepository<SavingsTransactionData, String> {
    List<SavingsTransactionData> findByGoalIdOrderByTimestampDesc(String goalId);
}
