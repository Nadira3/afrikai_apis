package com.precious.UserApi.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.precious.UserApi.model.enums.UserRole;
import com.precious.UserApi.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    // Custom query methods
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Find users by specific roles
    List<User> findByRole(UserRole role);

    // set user enabled status
    @Transactional
    @Modifying
    @Query("UPDATE User a SET a.enabled = TRUE WHERE a.email = ?1")
    int enableUser(String email);
}
