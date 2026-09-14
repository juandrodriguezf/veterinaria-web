package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Mascota;

/**
 * REPOSITORIO - Mascota
 * Spring Data JPA genera automáticamente todas las operaciones estándar
 * (findAll, findById, save, deleteById, count...) extendiendo
 * JpaRepository. Las consultas por atributos se declaran por nombre de
 * método (findBy...) o con consultas JPQL, y Spring las implementa en
 * tiempo de ejecución.
 *
 * La clave de la entidad es Integer (asegura = tipo del id autogenerado).
 */
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    /**
     * Mascotas de un dueño (Dueno 1 -- 0..* Mascota). Spring Data recorre
     * la ruta mascota.dueno.id desde el nombre del método.
     */
    List<Mascota> findByDuenoId(Integer duenoId);

    /**
     * Mascotas cuyo nombre coincide exactamente con el término buscado,
     * sin distinguir mayúsculas/minúsculas (AC21: poder buscar una mascota
     * por su nombre). Es la consulta derivada del ejemplo del curso
     * (StudentRepository.findByNombre) más la palabra clave IgnoreCase,
     * que el proyecto ya usa en las consultas de credenciales.
     */
    List<Mascota> findByNombreIgnoreCase(String nombre);

    /**
     * Mascotas que se encuentran en un estado determinado. Se usa para
     * ofrecer únicamente pacientes activos al asignar un tratamiento,
     * pues solo las mascotas hospitalizadas (activas) pueden recibirlo.
     */
    List<Mascota> findByEstado(String estado);
}
