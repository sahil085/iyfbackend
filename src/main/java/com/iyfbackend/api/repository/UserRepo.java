package com.iyfbackend.api.repository;

import com.iyfbackend.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    @Query(value = "select distinct registeredBy from User")
    List<String> findAllDistinctRegisteredBy();
}
