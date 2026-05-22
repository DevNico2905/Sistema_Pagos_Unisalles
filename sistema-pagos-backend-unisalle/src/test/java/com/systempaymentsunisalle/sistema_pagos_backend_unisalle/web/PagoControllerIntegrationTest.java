package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Pago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.PagoStatus;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.TypePago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.EstudianteRepository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.PagoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de integración - PagoController")
class PagoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PagoRepository pagoRepository;

    private Estudiante estudianteTest;

    @BeforeEach
    void setUp() {
        pagoRepository.deleteAll();
        estudianteRepository.deleteAll();

        estudianteTest = estudianteRepository.save(Estudiante.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Carlos")
                .apellido("Martinez")
                .codigo("TEST001")
                .programId("IGSV23")
                .build());
    }

    // ========== Pruebas de Estudiantes ==========

    @Test
    @DisplayName("GET /estudiantes - Debe retornar lista de estudiantes con status 200")
    void listarEstudiantes_DebeRetornar200() throws Exception {
        mockMvc.perform(get("/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nombre").value("Carlos"))
                .andExpect(jsonPath("$[0].codigo").value("TEST001"));
    }

    @Test
    @DisplayName("GET /estudiantes/{codigo} - Debe retornar estudiante por código")
    void listarEstudiantesByCodigo_DebeRetornarEstudiante() throws Exception {
        mockMvc.perform(get("/estudiantes/{codigo}", "TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellido").value("Martinez"))
                .andExpect(jsonPath("$.programId").value("IGSV23"));
    }

    @Test
    @DisplayName("POST /agregarEstudiante - Debe crear estudiante nuevo con status 200")
    void agregarEstudiante_DebeCrearEstudiante() throws Exception {
        Estudiante nuevo = Estudiante.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Laura")
                .apellido("Gomez")
                .codigo("TEST002")
                .programId("IGSV24")
                .build();

        mockMvc.perform(post("/agregarEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laura"))
                .andExpect(jsonPath("$.codigo").value("TEST002"));
    }

    // ========== Pruebas de Pagos ==========

    @Test
    @DisplayName("GET /pagos - Debe retornar lista vacía inicialmente con status 200")
    void listarPagos_DebeRetornar200() throws Exception {
        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("POST /pagos - Debe registrar un pago con archivo adjunto")
    void guardarPago_DebeRegistrarPago() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "file",
                "comprobante.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "PDF de prueba - comprobante de pago".getBytes()
        );

        mockMvc.perform(multipart("/pagos")
                        .file(archivo)
                        .param("cantidad", "15000")
                        .param("type", "TRANSFERENCIA")
                        .param("fecha", LocalDate.now().toString())
                        .param("codigoEstudiante", "TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(15000))
                .andExpect(jsonPath("$.type").value("TRANSFERENCIA"))
                .andExpect(jsonPath("$.status").value("CREADO"))
                .andExpect(jsonPath("$.estudiante.codigo").value("TEST001"));
    }

    @Test
    @DisplayName("GET /estudiantes/{codigo}/pagos - Debe retornar pagos del estudiante")
    void listarPagosByEstudiante_DebeRetornarPagos() throws Exception {
        // Arrange: crear un pago directamente en BD
        Pago pago = pagoRepository.save(Pago.builder()
                .cantidad(8000)
                .type(TypePago.EFECTIVO)
                .status(PagoStatus.CREADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build());

        // Act & Assert
        mockMvc.perform(get("/estudiantes/{codigo}/pagos", "TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cantidad").value(8000))
                .andExpect(jsonPath("$[0].type").value("EFECTIVO"));
    }

    @Test
    @DisplayName("PUT /pagos/{id}/updatePayment - Debe actualizar estado a VALIDADO")
    void actualizarStatusPago_DebeActualizarAValidado() throws Exception {
        // Arrange
        Pago pago = pagoRepository.save(Pago.builder()
                .cantidad(12000)
                .type(TypePago.CHEQUE)
                .status(PagoStatus.CREADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build());

        // Act & Assert
        mockMvc.perform(put("/pagos/{pagoId}/updatePayment", pago.getId())
                        .param("status", "VALIDADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDADO"))
                .andExpect(jsonPath("$.cantidad").value(12000));
    }

    @Test
    @DisplayName("PUT /pagos/{id}/updatePayment - Debe actualizar estado a RECHAZADO")
    void actualizarStatusPago_DebeActualizarARechazado() throws Exception {
        // Arrange
        Pago pago = pagoRepository.save(Pago.builder()
                .cantidad(9500)
                .type(TypePago.TRANSFERENCIA)
                .status(PagoStatus.CREADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build());

        // Act & Assert
        mockMvc.perform(put("/pagos/{pagoId}/updatePayment", pago.getId())
                        .param("status", "RECHAZADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECHAZADO"));
    }

    @Test
    @DisplayName("GET /pagosByStatus - Debe filtrar pagos por estado CREADO")
    void listarPagosByStatus_DebeRetornarFiltrados() throws Exception {
        // Arrange
        pagoRepository.save(Pago.builder()
                .cantidad(7000)
                .type(TypePago.EFECTIVO)
                .status(PagoStatus.CREADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build());

        pagoRepository.save(Pago.builder()
                .cantidad(3000)
                .type(TypePago.CHEQUE)
                .status(PagoStatus.VALIDADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build());

        // Act & Assert
        mockMvc.perform(get("/pagosByStatus")
                        .param("status", "CREADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("CREADO"));
    }

    @Test
    @DisplayName("GET /pagos/ByType - Debe filtrar pagos por tipo TRANSFERENCIA")
    void listarPagosByType_DebeRetornarFiltrados() throws Exception {
        // Arrange
        pagoRepository.save(Pago.builder()
                .cantidad(11000)
                .type(TypePago.TRANSFERENCIA)
                .status(PagoStatus.CREADO)
                .fecha(LocalDate.now())
                .estudiante(estudianteTest)
                .build());

        // Act & Assert
        mockMvc.perform(get("/pagos/ByType")
                        .param("type", "TRANSFERENCIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("TRANSFERENCIA"));
    }

    // ========== Prueba de flujo completo (end-to-end) ==========

    @Test
    @DisplayName("Flujo completo: crear estudiante → registrar pago → validar pago")
    void flujoCompleto_RegistroYValidacionDePago() throws Exception {
        // Paso 1: Verificar que el estudiante existe
        mockMvc.perform(get("/estudiantes/{codigo}", "TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("TEST001"));

        // Paso 2: Registrar un pago con comprobante
        MockMultipartFile archivo = new MockMultipartFile(
                "file", "recibo.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Comprobante de pago academico".getBytes()
        );

        String pagoResponse = mockMvc.perform(multipart("/pagos")
                        .file(archivo)
                        .param("cantidad", "20000")
                        .param("type", "TRANSFERENCIA")
                        .param("fecha", LocalDate.now().toString())
                        .param("codigoEstudiante", "TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREADO"))
                .andReturn().getResponse().getContentAsString();

        // Extraer el ID del pago creado
        Long pagoId = objectMapper.readTree(pagoResponse).get("id").asLong();

        // Paso 3: Validar el pago (cambiar estado a VALIDADO)
        mockMvc.perform(put("/pagos/{pagoId}/updatePayment", pagoId)
                        .param("status", "VALIDADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDADO"))
                .andExpect(jsonPath("$.cantidad").value(20000));

        // Paso 4: Verificar que el pago aparece en el historial del estudiante
        mockMvc.perform(get("/estudiantes/{codigo}/pagos", "TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + pagoId + ")].status").value("VALIDADO"));
    }
}
