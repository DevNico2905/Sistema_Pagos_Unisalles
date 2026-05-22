package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Pago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.PagoStatus;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.TypePago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.EstudianteRepository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.PagoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - PagoService")
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private PagoService pagoService;

    private Estudiante estudianteTest;
    private Pago pagoTest;

    @BeforeEach
    void setUp() {
        estudianteTest = Estudiante.builder()
                .id("test-uuid-001")
                .nombre("Nicolas")
                .apellido("Bernal")
                .codigo("290503")
                .programId("IGSV23")
                .build();

        pagoTest = Pago.builder()
                .id(1L)
                .cantidad(5000)
                .type(TypePago.TRANSFERENCIA)
                .status(PagoStatus.CREADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build();
    }

    // ========== Pruebas para actualizarPagoPorStatus ==========

    @Test
    @DisplayName("Debe actualizar el estado de un pago de CREADO a VALIDADO")
    void actualizarPagoPorStatus_DebeActualizarAValidado() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoTest));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pago resultado = pagoService.actualizarPagoPorStatus(PagoStatus.VALIDADO, 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(PagoStatus.VALIDADO, resultado.getStatus());
        verify(pagoRepository).findById(1L);
        verify(pagoRepository).save(pagoTest);
    }

    @Test
    @DisplayName("Debe actualizar el estado de un pago de CREADO a RECHAZADO")
    void actualizarPagoPorStatus_DebeActualizarARechazado() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoTest));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pago resultado = pagoService.actualizarPagoPorStatus(PagoStatus.RECHAZADO, 1L);

        // Assert
        assertEquals(PagoStatus.RECHAZADO, resultado.getStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el pago no existe")
    void actualizarPagoPorStatus_DebeLanzarExcepcionSiNoExiste() {
        // Arrange
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            pagoService.actualizarPagoPorStatus(PagoStatus.VALIDADO, 999L);
        });
    }

    @Test
    @DisplayName("Debe conservar los demás campos del pago al actualizar estado")
    void actualizarPagoPorStatus_DebeConservarDatosPago() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoTest));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pago resultado = pagoService.actualizarPagoPorStatus(PagoStatus.VALIDADO, 1L);

        // Assert
        assertEquals(5000, resultado.getCantidad());
        assertEquals(TypePago.TRANSFERENCIA, resultado.getType());
        assertEquals("Nicolas", resultado.getEstudiante().getNombre());
        assertEquals(PagoStatus.VALIDADO, resultado.getStatus());
    }

    @Test
    @DisplayName("Debe invocar save del repositorio exactamente una vez")
    void actualizarPagoPorStatus_DebeInvocarSaveUnaVez() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoTest));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoTest);

        // Act
        pagoService.actualizarPagoPorStatus(PagoStatus.VALIDADO, 1L);

        // Assert
        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(pagoRepository, times(1)).findById(1L);
    }
}
