package com.vetopia.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Mascotas de un dueño (Dueno 1 -- 0..* Mascota). El guion bajo
     * (findByDueno_Id) separa explícitamente la ruta mascota.dueno.id: la
     * entidad Mascota también expone un getter getDuenoId() para la API
     * (JSON), así que sin el "_" Spring Data confundiría "duenoId" con una
     * propiedad inexistente en vez de recorrer la relación.
     */
    List<Mascota> findByDueno_Id(Integer duenoId);

    /**
     * Cuenta las mascotas de un dueño sin traerlas todas (countByDueno_Id
     * recorre la ruta mascota.dueno.id); lo usa el conteo que muestra el
     * listado de clientes.
     */
    long countByDueno_Id(Integer duenoId);

    /**
     * Mascotas cuyo nombre contiene el término buscado, sin distinguir
     * mayúsculas/minúsculas (AC21: poder buscar una mascota por su nombre).
     * Es la consulta derivada del ejemplo del curso
     * (StudentRepository.findByNombre) más las palabras clave Containing e
     * IgnoreCase, de modo que la barra de búsqueda encuentre coincidencias
     * parciales sin exigir el nombre completo.
     */
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Mascotas que se encuentran en un estado determinado. Se usa para
     * ofrecer únicamente pacientes activos al asignar un tratamiento,
     * pues solo las mascotas hospitalizadas (activas) pueden recibirlo.
     */
    List<Mascota> findByEstado(String estado);

    /**
     * Cuenta las mascotas en un estado determinado sin traerlas todas.
     * Alimenta el KPI de mascotas activas (en tratamiento) del dashboard
     * del administrador.
     */
    long countByEstado(String estado);

    /**
     * Consulta personalizada 1: mascotas de un dueño filtradas por estado.
     * Permite al cliente ver separadamente sus pacientes activos
     * (hospitalizados) de los históricos que ya egresaron.
     */
    @Query("SELECT m FROM Mascota m WHERE m.dueno.id = :duenoId AND m.estado = :estado")
    List<Mascota> buscarPorDuenoYEstado(@Param("duenoId") Integer duenoId, @Param("estado") String estado);

    /**
     * Consulta personalizada 2: mascotas cuyo nombre contiene el término
     * buscado (sin distinguir mayúsculas/minúsculas) y además están en el
     * estado indicado. Combina el AC21 de búsqueda parcial con el filtro
     * de pacientes activos que exige la asignación de tratamientos.
     */
    @Query("SELECT m FROM Mascota m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND m.estado = :estado")
    List<Mascota> buscarPorNombreYEstado(@Param("nombre") String nombre, @Param("estado") String estado);

    /**
     * Consulta personalizada 3: mascotas con peso mayor o igual al mínimo
     * indicado. Es el ejemplo del profesor transformado en la veterinaria:
     * reglas del tipo "la dosis del medicamento exige un animal de por
     * lo menos cierta cantidad de kilos".
     */
    @Query("SELECT m FROM Mascota m WHERE m.pesoKg >= :peso")
    List<Mascota> buscarConPesoMayorOIgual(@Param("peso") Double peso);

    /**
     * Consulta personalizada 4: mascotas hospitalizadas desde la fecha
     * indicada en adelante. Permite informes del período reciente
     * (por ejemplo, ingresos de las últimas dos semanas).
     */
    @Query("SELECT m FROM Mascota m WHERE m.fechaIngreso >= :fecha")
    List<Mascota> buscarIngresadasDesde(@Param("fecha") LocalDate fecha);

    /**
     * Consulta personalizada 5: cuenta las mascotas de una especie en un
     * estado determinado sin traerlas. Puede alimentar métricas del
     * dashboard como "perros activos" o "gatos activos".
     */
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.estado = :estado AND m.especie = :especie")
    long contarPorEstadoYEspecie(@Param("estado") String estado, @Param("especie") String especie);
}
