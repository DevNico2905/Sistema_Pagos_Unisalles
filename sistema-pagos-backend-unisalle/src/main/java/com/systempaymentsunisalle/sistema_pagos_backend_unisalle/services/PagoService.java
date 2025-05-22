package com.systempaymentsunisalle.sistema_pagos_backend_unisalle.services;


import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Pago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.PagoStatus;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.TypePago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.EstudianteRepository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.PagoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    /*
     * @param file - archivo PDF que se subirá al servidor
     * @param cantidad - monto del pago realizado
     * @param type - tipo de pago(EFECTIVO, CHEQUE, TRANSFERENCIA)
     * @param date - fecha en que sea realiza el pago
     * @param codigoEstudiante - codigo del estudiante que realiza el pago
     * @return Objeto del pago guardado en la base de datos
     * @IOException lanzar si ocurre un error al manejar el archivo
     */
    
    public Pago savePago(MultipartFile file, double cantidad, TypePago type, LocalDate date, String codigoEstudiante) throws IOException{
        
        // Construir ruta donde se guardará el archivo dentro del sistema 
        Path folderPath = Paths.get(System.getProperty("user.home"),"enset-data", "pagos");
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        //Genera nombre unico para el archivo usando UUID(Identificador Unico Universal)
        String fileName = UUID.randomUUID().toString();

        // Construir la ruta completa del archivo añadiendo la extension .pdf
        Path filePath = Paths.get(System.getProperty("user.home"),"enset-data", "pagos", fileName + ".pdf");

        //guardar el archivo recibido en la ubicación especificada dentro del sistema de archivos
        Files.copy(file.getInputStream(), filePath);

        //buscar en la base de datos de estudiantes que realizó el pago usando el código unico de sistema
        Estudiante estudiante = estudianteRepository.findByCodigo(codigoEstudiante);

        //Creamos un nuevo objeto Pago utilizando patron de diseño builder
        Pago pago = Pago.builder()

            .type(type) //Tipo de pago
            .status(PagoStatus.CREADO) // Estado inicial del pago
            .fecha(date) // fecha que se realiza el pago
            .estudiante(estudiante)
            .cantidad(cantidad)
            .file(filePath.toUri().toString()) // Ruta del archivo PDF almacenado
            .build(); // Construcción final del objeto pago

        return pagoRepository.save(pago);
    }
    

    public byte[] getArchivoPorId(Long pagoId) throws IOException{
        
        // Buscar un objeto pago en la base de datos
        Pago pago = pagoRepository.findById(pagoId).get();
        return Files.readAllBytes(Path.of(URI.create(pago.getFile())));
    }

    public Pago actualizarPagoPorStatus(PagoStatus status, Long Id){
        // Buscar objeto pago en la base de datos por su ID
        Pago pago = pagoRepository.findById(Id).get();

        //Actualice el estado de su pa
        pago.setStatus(status);

        return pagoRepository.save(pago);
    }

}
