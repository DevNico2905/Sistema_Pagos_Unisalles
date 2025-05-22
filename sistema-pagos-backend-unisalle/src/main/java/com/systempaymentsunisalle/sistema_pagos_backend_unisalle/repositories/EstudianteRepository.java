package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;



@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {

    //Método personalizado para buscar un estudiante en especifico
    Estudiante findByCodigo(String codigo);

    List<Estudiante> findByProgramId(String programId);
    
}