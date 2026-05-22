package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Pago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.PagoStatus;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.TypePago;

@DataJpaTest
@DisplayName("Pruebas de repositorio - Capa de datos")
class RepositoryTest {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PagoRepository pagoRepository;

    private Estudiante estudiante1;
    private Estudiante estudiante2;

    @BeforeEach
    void setUp() {
        pagoRepository.deleteAll();
        estudianteRepository.deleteAll();

        estudiante1 = estudianteRepository.save(Estudiante.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Ana")
                .apellido("Lopez")
                .codigo("EST001")
                .programId("IGSV23")
                .build());

        estudiante2 = estudianteRepository.save(Estudiante.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Pedro")
                .apellido("Ruiz")
                .codigo("EST002")
                .programId("IGSV24")
                .build());
    }

    // ========== EstudianteRepository ==========

    @Test
    @DisplayName("findByCodigo - Debe encontrar estudiante por código único")
    void findByCodigo_DebeEncontrarEstudiante() {
        Estudiante resultado = estudianteRepository.findByCodigo("EST001");

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        assertEquals("Lopez", resultado.getApellido());
    }

    @Test
    @DisplayName("findByCodigo - Debe retornar null si el código no existe")
    void findByCodigo_DebeRetornarNullSiNoExiste() {
        Estudiante resultado = estudianteRepository.findByCodigo("NOEXISTE");
        assertNull(resultado);
    }

    @Test
    @DisplayName("findByProgramId - Debe filtrar estudiantes por programa académico")
    void findByProgramId_DebeRetornarPorPrograma() {
        List<Estudiante> resultado = estudianteRepository.findByProgramId("IGSV23");

        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
    }

    // ========== PagoRepository ==========

    @Test
    @DisplayName("findByEstudianteCodigo - Debe retornar pagos de un estudiante")
    void findByEstudianteCodigo_DebeRetornarPagos() {
        pagoRepository.save(Pago.builder()
                .cantidad(10000).type(TypePago.EFECTIVO)
                .status(PagoStatus.CREADO).fecha(LocalDate.now())
                .estudiante(estudiante1).build());

        pagoRepository.save(Pago.builder()
                .cantidad(5000).type(TypePago.CHEQUE)
                .status(PagoStatus.VALIDADO).fecha(LocalDate.now())
                .estudiante(estudiante1).build());

        // Pago de otro estudiante
        pagoRepository.save(Pago.builder()
                .cantidad(8000).type(TypePago.TRANSFERENCIA)
                .status(PagoStatus.CREADO).fecha(LocalDate.now())
                .estudiante(estudiante2).build());

        List<Pago> pagosEst1 = pagoRepository.findByEstudianteCodigo("EST001");

        assertEquals(2, pagosEst1.size());
    }

    @Test
    @DisplayName("findByStatus - Debe filtrar pagos por estado")
    void findByStatus_DebeRetornarPorEstado() {
        pagoRepository.save(Pago.builder()
                .cantidad(10000).type(TypePago.EFECTIVO)
                .status(PagoStatus.CREADO).fecha(LocalDate.now())
                .estudiante(estudiante1).build());

        pagoRepository.save(Pago.builder()
                .cantidad(7000).type(TypePago.CHEQUE)
                .status(PagoStatus.VALIDADO).fecha(LocalDate.now())
                .estudiante(estudiante1).build());

        List<Pago> creados = pagoRepository.findByStatus(PagoStatus.CREADO);
        List<Pago> validados = pagoRepository.findByStatus(PagoStatus.VALIDADO);

        assertEquals(1, creados.size());
        assertEquals(1, validados.size());
        assertEquals(PagoStatus.VALIDADO, validados.get(0).getStatus());
    }

    @Test
    @DisplayName("findByType - Debe filtrar pagos por tipo de pago")
    void findByType_DebeRetornarPorTipo() {
        pagoRepository.save(Pago.builder()
                .cantidad(10000).type(TypePago.TRANSFERENCIA)
                .status(PagoStatus.CREADO).fecha(LocalDate.now())
                .estudiante(estudiante1).build());

        pagoRepository.save(Pago.builder()
                .cantidad(5000).type(TypePago.TRANSFERENCIA)
                .status(PagoStatus.CREADO).fecha(LocalDate.now())
                .estudiante(estudiante2).build());

        pagoRepository.save(Pago.builder()
                .cantidad(3000).type(TypePago.EFECTIVO)
                .status(PagoStatus.CREADO).fecha(LocalDate.now())
                .estudiante(estudiante1).build());

        List<Pago> transferencias = pagoRepository.findByType(TypePago.TRANSFERENCIA);
        List<Pago> efectivos = pagoRepository.findByType(TypePago.EFECTIVO);

        assertEquals(2, transferencias.size());
        assertEquals(1, efectivos.size());
    }
}
