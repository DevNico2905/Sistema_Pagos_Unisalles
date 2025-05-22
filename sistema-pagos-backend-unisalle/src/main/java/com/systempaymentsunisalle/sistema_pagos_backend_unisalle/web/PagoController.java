package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Pago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.PagoStatus;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.TypePago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.EstudianteRepository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.PagoRepository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.services.PagoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;





@RestController
@CrossOrigin("*")
public class PagoController {
    
    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private PagoService pagoService;

    //Métodos para manejos de estudiantes

    //Método que devuelve una lista de todos los estudiantes
    @GetMapping("/estudiantes")
    public List<Estudiante> listarEstudiantes() {
        return estudianteRepository.findAll();
    }
    
    //Método que devuelva un estudiante en especifico según su código
    @GetMapping("/estudiantes/{codigo}")
    public Estudiante listarEstudiantesByCodigo(@PathVariable String codigo) {
        return estudianteRepository.findByCodigo(codigo);
    }

    //Método que lista estudiantes según el programa acádemico
    @GetMapping("/estudiantesByProgramId/{programId}")
    public List<Estudiante> listarEstudiantesByProgramId(@RequestParam String programId) {
        return estudianteRepository.findByProgramId(programId);
    }

    //Métodos que devuelve una lista con todos los pagos registrados
    @GetMapping("/pagos")
    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    //Método que devuelve un pago men especifico por su id
    @GetMapping("/pagos/{id}")
    public Pago listarPagosById(@PathVariable Long id) {
        return pagoRepository.findById(id).get();
    }

    //Método que lista los pagos hechos por un estudiante según su código
    @GetMapping("/estudiantes/{codigo}/pagos")
    public List<Pago> listarPagosByCodigoEstudiante(@PathVariable String codigo) {
        return pagoRepository.findByEstudianteCodigo(codigo);
    }

    @GetMapping("/pagosByStatus")
    public List<Pago> listarPagosByStatus(@RequestParam PagoStatus status) {
        return pagoRepository.findByStatus(status);
    }

    //Método que lista los pagos segun su tipo (EFECTIVO, CHEQUE, TRANSFERENCIA)
    @GetMapping("/pagos/ByType")
    public List<Pago> listarPagosByType(@RequestParam TypePago type) {
        return pagoRepository.findByType(type);
    }

    //Métodos para actualizar los estados de un pago
    @PutMapping("pagos/{pagoId}/updatePayment")
    public Pago actualizarStatusPago(@RequestParam PagoStatus status, @PathVariable Long pagoId) {
        return pagoService.actualizarPagoPorStatus(status, pagoId);
    }

    //Método para registrar un pago con un archivo adjunto(comprobante de pago)
    @PostMapping(path = "/pagos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Pago guardarPago(
        @RequestParam("file") MultipartFile file,
        double cantidad,
        TypePago type,
        LocalDate date,
        String codigoEstudiante) throws IOException {
            return pagoService.savePago(file, cantidad, type, date, codigoEstudiante);
    }

    @PostMapping("/agregarEstudiante")
    public ResponseEntity<Estudiante> agregarEstudiante(@RequestBody Estudiante estudiante) {
        Estudiante response = estudianteRepository.save(estudiante);
        return ResponseEntity.ok(response);
    }
    

    //Metodo para descargar un archivo de pago
    //Método que lista los pagos segun su tipo (EFECTIVO, CHEQUE, TRANSFERENCIA)
    @GetMapping(value = "pagoFile{pagoId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] listarArchivosPorId(@PathVariable Long pagoId) throws IOException{
        return pagoService.getArchivoPorId(pagoId); //Obtener
    }

    /*
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
     */
}
