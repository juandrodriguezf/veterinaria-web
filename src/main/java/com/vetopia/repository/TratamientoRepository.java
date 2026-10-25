package com.vetopia.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Tratamiento;

/**
 * REPOSITORIO - Tratamiento
 * Operaciones estándar heredadas de JpaRepository (findAll, findById,
 * save...) más la consulta derivada por mascota, que soporta la
 * desvinculación del historial clínico desde el service.
 */
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {

    /**
     * Tratamientos aplicados a una mascota: soporta que el service los
     * desvincule (FK a nulo) antes de eliminar la mascota, conservando
     * el historial clínico. El guion bajo separa la relación de su id
     * (igual que en MascotaRepository) porque la entidad expone un
     * getter calculado "mascotaId" para la API.
     */
    List<Tratamiento> findByMascota_Id(Integer mascotaId);

    /**
     * Tratamientos aplicados por un veterinario: alimenta la pestaña
     * "Tratadas" del portal y permite desvincular el historial (FK a
     * nulo) antes de eliminar al veterinario. El guion bajo separa la
     * relación de su id por el getter calculado "veterinarioId".
     */
    List<Tratamiento> findByVeterinario_Id(Integer veterinarioId);

    /**
     * Tratamientos aplicados desde una fecha (incluida). Alimenta los KPIs
     * del dashboard del administrador: cantidad del último mes y tabla por
     * medicamento, que el service agrupa en memoria.
     */
    List<Tratamiento> findByFechaGreaterThanEqual(LocalDate fecha);
}
