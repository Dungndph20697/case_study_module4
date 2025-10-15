package com.codegym.case_study_module4.service;

import com.codegym.case_study_module4.model.Users;

import java.util.Optional;

public interface IUserService extends IGenerateService<Users>{
    boolean existsByEmailIgnoreCase(String email);

    Optional<Users> findByEmailIgnoreCase(String email);

    boolean existsByCitizenIdNumber(String citizenIdNumber);

    Optional<Users> findByCitizenIdNumber(String citizenIdNumber);

    Users findUsersByEmail(String email);
}
