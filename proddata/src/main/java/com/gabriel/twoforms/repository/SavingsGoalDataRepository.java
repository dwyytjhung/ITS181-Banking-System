package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.SavingsGoalData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SavingsGoalDataRepository extends CrudRepository<SavingsGoalData, String> {
    List<SavingsGoalData> findByCustomerId(String customerId);
    boolean existsByCustomerIdAndName(String customerId, String name);
}
