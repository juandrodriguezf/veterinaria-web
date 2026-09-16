import { Injectable } from '@angular/core';
import { Droga } from '../models/droga.model';
import { Mascota } from '../models/mascota.model';
import { Tratamiento } from '../models/tratamiento.model';
import { Veterinario } from '../models/veterinario.model';

@Injectable({
  providedIn: 'root',
})
export class TratamientoService {
  private drogas: Droga[] = [
    {
      id: 1,
      nombre: 'Amoxicilina',
      precioCompra: 8000,
      precioVenta: 15000,
      unidadesDisponibles: 100,
      unidadesVendidas: 0,
      tratamientos: [],
    },
    {
      id: 2,
      nombre: 'Ivermectina',
      precioCompra: 12000,
      precioVenta: 22000,
      unidadesDisponibles: 50,
      unidadesVendidas: 0,
      tratamientos: [],
    },
    {
      id: 3,
      nombre: 'Metronidazol',
      precioCompra: 6000,
      precioVenta: 11000,
      unidadesDisponibles: 75,
      unidadesVendidas: 0,
      tratamientos: [],
    },
    {
      id: 4,
      nombre: 'Rimadyl',
      precioCompra: 10000,
      precioVenta: 18000,
      unidadesDisponibles: 60,
      unidadesVendidas: 0,
      tratamientos: [],
    },
    {
      id: 5,
      nombre: 'Doxiciclina',
      precioCompra: 9000,
      precioVenta: 16000,
      unidadesDisponibles: 60,
      unidadesVendidas: 0,
      tratamientos: [],
    },
  ];

  private veterinarios: Veterinario[] = [
    {
      id: 1,
      cedula: '1002003001',
      contrasena: 'vet123',
      correo: 'carlos.gutierrez@vetopia.com',
      especialidad: 'Medicina General',
      numeroAtenciones: 0,
      nombre: 'Carlos Gutiérrez',
      estado: 'Activo',
      urlFoto: 'https://example.com/foto-carlos.jpg',
      tratamientos: [],
    },
    {
      id: 2,
      cedula: '1002003002',
      contrasena: 'vet123',
      correo: 'laura.mendez@vetopia.com',
      especialidad: 'Cirugía',
      numeroAtenciones: 0,
      nombre: 'Laura Méndez',
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 3,
      cedula: '1002003003',
      contrasena: 'vet123',
      correo: 'jorge.santana@vetopia.com',
      especialidad: 'Dermatología',
      numeroAtenciones: 0,
      nombre: 'Jorge Santana',
      estado: 'Activo',
      tratamientos: [],
    },
    {
      id: 4,
      cedula: '1002003004',
      contrasena: 'vet123',
      correo: 'sofia.rincon@vetopia.com',
      especialidad: 'Odontología',
      numeroAtenciones: 0,
      nombre: 'Sofía Rincón',
      estado: 'Activo',
      urlFoto: 'https://example.com/foto-sofia.jpg',
      tratamientos: [],
    },
    {
      id: 5,
      cedula: '1002003005',
      contrasena: 'vet123',
      correo: 'diego.paredes@vetopia.com',
      especialidad: 'Nutrición',
      numeroAtenciones: 0,
      nombre: 'Diego Paredes',
      estado: 'Activo',
      tratamientos: [],
    },
  ];

  private tratamientos: Tratamiento[] = [];

  listarTratamientos() {
    return [...this.tratamientos];
  }

  listarTratamientosPorMascota(id: number) {
    return this.tratamientos.filter((tratamiento) => tratamiento.mascota.id === id);
  }

  sembrarTratamientos(mascotas: Mascota[]) {
    const [max, luna, rocky] = mascotas;
    const [amoxicilina, ivermectina, metronidazol, rimadyl, doxiciclina] = this.drogas;
    const [carlos, laura, jorge, sofia, diego] = this.veterinarios;

    this.registrar('2026-08-25', luna, rimadyl, sofia);
    this.registrar('2026-08-27', max, doxiciclina, diego);
    this.registrar('2026-08-10', luna, amoxicilina, carlos);
    this.registrar('2026-08-15', max, ivermectina, laura);
    this.registrar('2026-08-20', rocky, metronidazol, jorge);

    mascotas.forEach((mascota) =>
      mascota.tratamientos.sort((uno, otro) => uno.fecha.localeCompare(otro.fecha)),
    );
  }

  private registrar(fecha: string, mascota: Mascota, droga: Droga, veterinario: Veterinario) {
    const tratamiento: Tratamiento = {
      id: this.tratamientos.length + 1,
      fecha,
      mascota,
      droga,
      veterinario,
    };
    this.tratamientos.push(tratamiento);
    mascota.tratamientos.push(tratamiento);
    droga.tratamientos.push(tratamiento);
    veterinario.tratamientos.push(tratamiento);
  }
}
