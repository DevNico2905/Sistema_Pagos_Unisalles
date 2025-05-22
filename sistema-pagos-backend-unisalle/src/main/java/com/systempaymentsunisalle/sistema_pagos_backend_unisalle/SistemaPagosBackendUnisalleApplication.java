package com.systempaymentsunisalle.sistema_pagos_backend_unisalle;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Estudiante;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.entities.Pago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.PagoStatus;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.enums.TypePago;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.EstudianteRepository;
import com.systempaymentsunisalle.sistema_pagos_backend_unisalle.repositories.PagoRepository;

@SpringBootApplication
public class SistemaPagosBackendUnisalleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaPagosBackendUnisalleApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(EstudianteRepository estudianteRepository, PagoRepository pagoRepository) {
		return args -> {
			estudianteRepository.save(Estudiante.builder()
					.id(UUID.randomUUID().toString())
					.nombre("Nicolas")
					.apellido("Bernal")
					.codigo("290503")
					.programId("IGSV23")
					.build());

			estudianteRepository.save(Estudiante.builder()
					.id(UUID.randomUUID().toString())
					.nombre("Kevin")
					.apellido("Velasco")
					.codigo("070420")
					.programId("IGSV23")
					.build());

			estudianteRepository.save(Estudiante.builder()
					.id(UUID.randomUUID().toString())
					.nombre("Samuel")
					.apellido("Valenzuela")
					.codigo("140520")
					.programId("IGSV24")
					.build());

			// Obtiene todos los valores posibles
			TypePago tiposPago[] = TypePago.values();
			Random random = new Random();

			estudianteRepository.findAll().forEach(estudiante -> {
				for (int i = 0; i < 10; i++) {
					// Genere indice aleatorio para seleccionar un tipo de gano
					int index = random.nextInt(tiposPago.length);

					// Construir objetos con pagos y con valores aleatorios
					Pago pago = Pago.builder()
							.cantidad(1000 + (int) (Math.random() * 20000))
							.type(tiposPago[index])
							.status(PagoStatus.CREADO)
							.fecha(LocalDate.now())
							.estudiante(estudiante)
							.build();
							
					pagoRepository.save(pago);
				}
			});
		};
	}
}
