package com.suffolk.library_management.repository;


import com.suffolk.library_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1 and u.id = ?2")
    User findByUserIdAndVerificationCode(String code, int index);
}
