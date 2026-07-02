package com.gabriel.twoforms.repository;

import com.gabriel.twoforms.entity.UserData;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserDataRepository extends CrudRepository<UserData, String> {
    Optional<UserData> findByUsername(String username);
}
