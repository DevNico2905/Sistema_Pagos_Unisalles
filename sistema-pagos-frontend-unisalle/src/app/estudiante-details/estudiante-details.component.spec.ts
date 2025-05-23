import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstudianteDetailsComponent } from './estudiante-details.component';

describe('EstudianteDetailsComponent', () => {
  let component: EstudianteDetailsComponent;
  let fixture: ComponentFixture<EstudianteDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EstudianteDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EstudianteDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
