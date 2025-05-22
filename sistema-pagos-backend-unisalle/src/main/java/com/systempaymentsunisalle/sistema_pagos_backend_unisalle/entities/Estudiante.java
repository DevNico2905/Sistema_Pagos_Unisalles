package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {

    @Id
    private String id;

    private String nombre;
    private String apellido;

    //Indica que una columna 'codigo' debe ser única en la base de datos.//
    @Column(unique = true)
    private String codigo;

    //Identificador del programa acádemico al que pertenece el estudiante.
    private String programId;

    private String foto;
}
