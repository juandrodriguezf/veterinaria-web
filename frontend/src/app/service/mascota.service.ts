import { Injectable, inject } from '@angular/core';
import { DuenoService } from './dueno.service';
import { Mascota } from '../models/mascota.model';

@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  private duenoService = inject(DuenoService);

  private mascotas: Mascota[] = this.sembrar();

  listarMascotas() {
    return [...this.mascotas];
  }

  obtenerMascotaPorId(id: number) {
    return this.mascotas.find((mascota) => mascota.id === id);
  }

  buscarPorNombre(nombre: string) {
    if (!nombre || !nombre.trim()) {
      return this.listarMascotas();
    }
    const termino = nombre.trim().toLowerCase();
    return this.mascotas.filter((mascota) => mascota.nombre.toLowerCase() === termino);
  }

  guardarValidada(mascota: Mascota) {
    const existente = this.obtenerMascotaPorId(mascota.id);
    if (!existente) {
      mascota.estado = 'Activo';
      mascota.id = this.siguienteId();
      this.mascotas.push(mascota);
      mascota.dueno.mascotas.push(mascota);
      return;
    }
    Object.assign(existente, mascota);
  }

  alternarEstado(id: number) {
    const mascota = this.obtenerMascotaPorId(id);
    if (!mascota) {
      return undefined;
    }
    mascota.estado = mascota.estado === 'Inactivo' ? 'Activo' : 'Inactivo';
    return mascota.estado;
  }

  private siguienteId() {
    return this.mascotas.reduce((mayor, mascota) => Math.max(mayor, mascota.id), 0) + 1;
  }

  private sembrar() {
    const duenos = this.duenoService.listarDuenos();
    const mascotas: Mascota[] = [
      {
        id: 1,
        nombre: 'Max',
        especie: 'Perro',
        raza: 'Doberman',
        edad: 5,
        sexo: 'Macho',
        imagen: this.imagenPerro(0),
        pesoKg: 32,
        color: 'Negro',
        fechaIngreso: '2026-01-12',
        enfermedad: 'Control de rutina y vacunación',
        dueno: duenos[0],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 2,
        nombre: 'Luna',
        especie: 'Gato',
        raza: 'Siamés',
        edad: 3,
        sexo: 'Hembra',
        imagen: this.imagenGato(),
        pesoKg: 4.2,
        color: 'Blanco y café',
        fechaIngreso: '2026-02-03',
        enfermedad: 'Infección respiratoria leve',
        dueno: duenos[1],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 3,
        nombre: 'Rocky',
        especie: 'Perro',
        raza: 'Bulldog Francés',
        edad: 0.6,
        sexo: 'Macho',
        imagen: this.imagenPerro(1),
        pesoKg: 7.5,
        color: 'Gris atigrado',
        fechaIngreso: '2025-11-20',
        enfermedad: 'Desparasitación',
        dueno: duenos[2],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 4,
        nombre: 'Canela',
        especie: 'Perro',
        raza: 'Labrador',
        edad: 3.5,
        sexo: 'Hembra',
        imagen: this.imagenPerro(0),
        pesoKg: 18.4,
        color: 'Canela',
        fechaIngreso: '2025-06-14',
        enfermedad: 'Desparasitación',
        dueno: duenos[3],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 5,
        nombre: 'Toby',
        especie: 'Perro',
        raza: 'Beagle',
        edad: 7.2,
        sexo: 'Macho',
        imagen: this.imagenPerro(1),
        pesoKg: 12.1,
        color: 'Tricolor',
        fechaIngreso: '2025-03-02',
        enfermedad: 'Gastritis aguda',
        dueno: duenos[4],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 6,
        nombre: 'Bruno',
        especie: 'Perro',
        raza: 'Pastor Alemán',
        edad: 5.8,
        sexo: 'Macho',
        imagen: this.imagenPerro(0),
        pesoKg: 34.7,
        color: 'Negro',
        fechaIngreso: '2025-09-21',
        enfermedad: 'Chequeo post-operatorio',
        dueno: duenos[5],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 7,
        nombre: 'Milú',
        especie: 'Perro',
        raza: 'Corgi',
        edad: 1.4,
        sexo: 'Hembra',
        imagen: this.imagenPerro(1),
        pesoKg: 9.3,
        color: 'Dorado',
        fechaIngreso: '2026-01-30',
        enfermedad: 'Control de rutina y vacunación',
        dueno: duenos[6],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 8,
        nombre: 'Copito',
        especie: 'Perro',
        raza: 'Poodle',
        edad: 2.9,
        sexo: 'Macho',
        imagen: this.imagenPerro(0),
        pesoKg: 6.8,
        color: 'Blanco',
        fechaIngreso: '2025-12-11',
        enfermedad: 'Dermatitis alérgica leve',
        dueno: duenos[7],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 9,
        nombre: 'Balto',
        especie: 'Perro',
        raza: 'Husky',
        edad: 6.3,
        sexo: 'Macho',
        imagen: this.imagenPerro(1),
        pesoKg: 27.5,
        color: 'Gris',
        fechaIngreso: '2025-05-08',
        enfermedad: 'Rehabilitación tras artroscopia',
        dueno: duenos[0],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 10,
        nombre: 'Dante',
        especie: 'Perro',
        raza: 'Mestizo',
        edad: 0.5,
        sexo: 'Macho',
        imagen: this.imagenPerro(0),
        pesoKg: 5.2,
        color: 'Café con blanco',
        fechaIngreso: '2026-02-17',
        enfermedad: 'Seguimiento por infección urinaria',
        dueno: duenos[1],
        estado: 'Inactivo',
        tratamientos: [],
      },
      {
        id: 11,
        nombre: 'León',
        especie: 'Perro',
        raza: 'Golden Retriever',
        edad: 8.1,
        sexo: 'Macho',
        imagen: this.imagenPerro(1),
        pesoKg: 31.9,
        color: 'Dorado',
        fechaIngreso: '2025-02-25',
        enfermedad: 'Anemia por malnutrición',
        dueno: duenos[2],
        estado: 'Activo',
        tratamientos: [],
      },
      {
        id: 12,
        nombre: 'Kiara',
        especie: 'Perro',
        raza: 'Chihuahua',
        edad: 4.6,
        sexo: 'Hembra',
        imagen: this.imagenPerro(0),
        pesoKg: 3.4,
        color: 'Marrón',
        fechaIngreso: '2025-10-03',
        enfermedad: 'Control de rutina y vacunación',
        dueno: duenos[3],
        estado: 'Inactivo',
        tratamientos: [],
      },
    ];
    mascotas.forEach((mascota) => mascota.dueno.mascotas.push(mascota));
    return mascotas;
  }

  private imagenPerro(indice: number) {
    return indice % 2 === 0
      ? 'https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8wMy8yNy8xOC8xMi9sdW5hLTEyODMzNTZfNjQwLmpwZw'
      : 'https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVtYnMuZHJlYW1zdGltZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn';
  }

  private imagenGato() {
    return 'https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0';
  }
}
