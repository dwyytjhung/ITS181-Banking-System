package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.CardRequestData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface CardRequestDataRepository extends CrudRepository<CardRequestData, String> {
    List<CardRequestData> findByCustomerId(String customerId);
}
