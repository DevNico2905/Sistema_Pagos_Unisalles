import { Component, OnInit } from '@angular/core';
import { Estudiante } from '../models/estudiantes.model';
import { EstudiantesService } from '../services/estudiantes.service';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';

@Component({
  selector: 'app-estudiantes',
  standalone: false,
  templateUrl: './estudiantes.component.html',
  styleUrl: './estudiantes.component.css'
})
export class EstudiantesComponent implements OnInit{
  
  estudiantes!: Array<Estudiante>;
  estudiantesDataSource!: MatTableDataSource<Estudiante>;
  displayedColumns: string[] = ['id','nombre','apellido','codigo','programId','pagos']

  constructor(private estudianteService: EstudiantesService,private router:Router) {

  }

  ngOnInit(): void {
      this.estudianteService.getAllEstudiantes().subscribe({
      next: value => {
        this.estudiantes = value;
        this.estudiantesDataSource = new MatTableDataSource<Estudiante>(this.estudiantes);
      },
      error: err => {
        console.log(err);
      }
    })
  }

  listarPagosDeEstudiante(estudiante:Estudiante){
    this.router.navigateByUrl(`/admin/estudiante-detalles/${estudiante.codigo}`)
  }
}
