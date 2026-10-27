package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Mascota;

/**
 * SERVICIO - MascotaService
 * Define la lógica de negocio relacionada con mascotas. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface MascotaService {

    /**
     * Lista todas las mascotas registradas. El repositorio las entrega en
     * el orden de la base; la busqueda por nombre se resuelve aparte.
     */
    List<Mascota> listarMascotas();

    /**
     * Obtiene una mascota específica validando previamente el
     * identificador recibido.
     */
    Mascota obtenerMascotaPorId(Integer id);

    /**
     * Guarda (registra) una nueva mascota. El identificador lo asigna
     * la capa repository automáticamente.
     */
    void guardar(Mascota mascota);

    /**
     * Guarda la mascota aplicando las reglas de negocio: asigna el estado
     * inicial "Activo" cuando es un registro nuevo y exige un dueño
     * asignado para no dejar registros huérfanos. Lanza
     * IllegalArgumentException si no llegan datos y
     * IllegalStateException si la mascota queda sin dueño.
     */
    void guardarValidada(Mascota mascota);

    /**
     * Obtiene la mascota con el id indicado y valida que pertenezca al
     * dueño dado (aislamiento de datos del portal del cliente). Lanza
     * IllegalArgumentException si el id es inválido o la mascota no
     * existe, y IllegalStateException si la mascota es de otro dueño.
     */
    Mascota obtenerPropia(Integer id, Integer duenoId);

    /**
     * Alterna el estado de la mascota (Activo <-> Inactivo, el borrado
     * lógico del diagrama de clases) y devuelve el nuevo estado; null si
     * la mascota no existe.
     */
    String alternarEstado(Integer id);

    /**
     * Cambia el estado (Activo/Inactivo) de una mascota por su id.
     * Usado por el veterinario para dar de alta o de baja una mascota.
     */
    void cambiarEstado(Integer id, String estado);

    /** Elimina definitivamente una mascota por su id (borrado físico). */
    void eliminar(Integer id);

    /**
     * Lista solo las mascotas que pertenecen al dueño indicado
     * (relación Dueno 1 -- 0..* Mascota del diagrama de clases). Valida que
     * el identificador sea positivo y que el dueño exista; un cliente
     * inexistente se informa como recurso no encontrado, no como lista vacía.
     */
    List<Mascota> listarMascotasPorDueno(Integer duenoId);

    /**
     * Busca mascotas cuyo nombre contenga el término dado, sin distinguir
     * mayúsculas/minúsculas (AC21). Si el término es nulo o vacío, devuelve
     * el listado completo para que el mismo endpoint sirva búsqueda y
     * listado, igual que el findByNombre del ejemplo del curso más el
     * IgnoreCase que el proyecto ya usa en credenciales.
     */
    List<Mascota> buscarPorNombre(String nombre);

    /**
     * Busca mascotas de un dueño cuyo nombre contenga el término dado, sin
     * distinguir mayúsculas/minúsculas (AC21, portal del cliente). La
     * relación se resuelve en el service (findByDuenoId) y el nombre se
     * compara en memoria con la misma coincidencia parcial que el listado
     * general. Si el término es nulo o vacío, cae al listado de mascotas
     * del dueño.
     */
    List<Mascota> buscarPorDuenoYNombre(Integer duenoId, String nombre);

    /**
     * Lista únicamente las mascotas activas, es decir, las que están
     * hospitalizadas. Regla de negocio: solo se puede dar tratamiento a
     * mascotas activas.
     */
    List<Mascota> listarMascotasActivas();

    /**
     * Cuenta las mascotas del dueño indicado sin traerlas todas (consulta
     * derivada countBy...). Lo usa el listado de clientes para mostrar el
     * número de mascotas sin cruzar el listado completo.
     */
    long contarMascotasPorDueno(Integer duenoId);
}
