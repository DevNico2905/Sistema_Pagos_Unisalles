import { Component, OnInit } from '@angular/core';
import { Pago } from '../models/estudiantes.model';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { EstudiantesService } from '../services/estudiantes.service';

@Component({
  selector: 'app-estudiante-details',
  standalone: false,
  templateUrl: './estudiante-details.component.html',
  styleUrl: './estudiante-details.component.css'
})
export class EstudianteDetailsComponent implements OnInit {

  estudianteCodigo!: string;
  pagosEstudiane!: Array<Pago>;
  pagosDataSource!: MatTableDataSource<Pago>;

  public displayedColumns = ['id', 'fecha', 'cantidad', 'type', 'status', 'nombre'];

  constructor(private activatedRoute: ActivatedRoute, private estudiantesService: EstudiantesService, private router: Router) {

  }

  ngOnInit(): void {
    this.estudianteCodigo = this.activatedRoute.snapshot.params['codigo'];
    this.estudiantesService.getPagosDeEstudiante(this.estudianteCodigo).subscribe({
      next: value => {
        this.pagosEstudiane = value;
        this.pagosDataSource = new MatTableDataSource<Pago>(this.pagosEstudiane);
      },
      error: err => {
        console.log(err);
      }
    })
  }

  agregarPago() {
    this.router.navigateByUrl(`/admin/new-pago/${this.estudianteCodigo}`);
  }

}
