package com.codegym.case_study_module4.service;

import java.util.List;
import java.util.Optional;

public interface IGenerateService<T> {
    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T t);

    void remove(Long id);
}
