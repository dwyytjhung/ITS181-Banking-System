package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.AccountData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface AccountDataRepository extends CrudRepository<AccountData, String> {
    List<AccountData> findByCustomerId(String customerId);
}
