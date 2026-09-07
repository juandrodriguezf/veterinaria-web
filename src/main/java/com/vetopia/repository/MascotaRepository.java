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
}
