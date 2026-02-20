package com.mysawit.mysawit_panen.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table
@Setter
@Getter
public class dummyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



}
