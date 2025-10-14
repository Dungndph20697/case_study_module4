package com.codegym.case_study_module4.model;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numberRoom;

    private String typeRoom;

    private Double price;

    private Integer statusRoom;

    private String description;
}
