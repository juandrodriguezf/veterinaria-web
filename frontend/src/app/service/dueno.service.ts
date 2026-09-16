import { Injectable } from '@angular/core';
import { Dueno } from '../models/dueno.model';

@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  private duenos: Dueno[] = [
    {
      id: 1,
      cedula: '1020304050',
      nombre: 'Ana Rodríguez',
      correo: 'ana@correo.com',
      contrasena: 'dueño123',
      celular: '3001234567',
      estado: 'Activo',
      mascotas: [],
    },
    {
      id: 2,
      cedula: '1020304051',
      nombre: 'Pedro Gómez',
      correo: 'pedro@correo.com',
      contrasena: 'dueño123',
      celular: '3007654321',
      estado: 'Activo',
      mascotas: [],
    },
    {
      id: 3,
      cedula: '1020304052',
      nombre: 'María López',
      correo: 'maria@correo.com',
      contrasena: 'dueño123',
      celular: '3010001111',
      estado: 'Inactivo',
      mascotas: [],
    },
    {
      id: 4,
      cedula: '10203004105',
      nombre: 'Sofía Sánchez',
      correo: 'cliente1@correo.com',
      contrasena: 'dueño123',
      celular: '3001000000',
      estado: 'Activo',
      mascotas: [],
    },
    {
      id: 5,
      cedula: '10203004106',
      nombre: 'Mateo Ramírez',
      correo: 'cliente2@correo.com',
      contrasena: 'dueño123',
      celular: '3001000137',
      estado: 'Activo',
      mascotas: [],
    },
    {
      id: 6,
      cedula: '10203004107',
      nombre: 'Valentina Torres',
      correo: 'cliente3@correo.com',
      contrasena: 'dueño123',
      celular: '3001000274',
      estado: 'Activo',
      mascotas: [],
    },
    {
      id: 7,
      cedula: '10203004108',
      nombre: 'Sebastián Fernández',
      correo: 'cliente4@correo.com',
      contrasena: 'dueño123',
      celular: '3001000411',
      estado: 'Activo',
      mascotas: [],
    },
    {
      id: 8,
      cedula: '10203004109',
      nombre: 'Camila Silva',
      correo: 'cliente5@correo.com',
      contrasena: 'dueño123',
      celular: '3001000548',
      estado: 'Activo',
      mascotas: [],
    },
  ];

  listarDuenos() {
    return [...this.duenos];
  }

  obtenerPorId(id: number) {
    return this.duenos.find((dueno) => dueno.id === id);
  }
}
