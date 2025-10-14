package com.codegym.case_study_module4.repository;

import com.codegym.case_study_module4.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findUsersByEmail(String email);
}
