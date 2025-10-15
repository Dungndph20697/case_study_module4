package com.codegym.case_study_module4.repository;

import com.codegym.case_study_module4.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByRoomId(Long roomId);}
