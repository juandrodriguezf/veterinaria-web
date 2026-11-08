package com.vetopia.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vetopia.e2e.support.E2eBase;
import com.vetopia.e2e.support.E2eData;

public class CasoUsoCompletoE2E extends E2eBase {

    @Test
    void caso1_registroClienteMascotaYPortalCliente() {
        abrirLogin();
        iniciarSesion(E2eData.VET_EMAIL, "clave-incorrecta");
        assertThat(esperarId("mensajeLogin").isDisplayed()).isTrue();

        iniciarSesion(E2eData.VET_EMAIL, E2eData.VET_PASSWORD);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));

        esperarId("navClientes").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/clientes"));
        esperarId("btnAgregarCliente").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/clientes/new"));
        esperarId("cedula");

        llenarCliente(E2eData.OWNER_CEDULA);
        esperarId("btnRegistrarCliente").click();
        assertThat(esperarId("mensajeErrorCliente").getText())
                .contains("Ya existe una cuenta con la cédula");

        escribir("cedula", E2eData.NEW_CLIENT_CEDULA);
        esperarId("btnRegistrarCliente").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/clientes"));
        esperarTestId("cliente-" + E2eData.NEW_CLIENT_CEDULA);

        esperarId("navPacientes").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarId("btnAgregarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas/new"));
        esperarId("nombre");

        llenarMascota();
        esperarId("btnRegistrarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarTexto(E2eData.NEW_PET_NOMBRE);

        cerrarSesionVeterinario();

        abrirLogin();
        iniciarSesion(E2eData.NEW_CLIENT_CEDULA, E2eData.NEW_CLIENT_PASSWORD);
        wait.until(ExpectedConditions.urlContains("/cliente/mascotas/"));
        esperarTexto(E2eData.NEW_PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-detalle-']"))).click();
        wait.until(ExpectedConditions.urlContains("/detalle/"));

        assertThat(textoDe("detalleMascotaNombre")).isEqualTo(E2eData.NEW_PET_NOMBRE);
        assertThat(textoDe("detalleMascotaEspecieRaza"))
                .contains(E2eData.NEW_PET_RAZA)
                .contains(E2eData.NEW_PET_ESPECIE);
    }

    @Test
    void caso2_tratamientoYValidacionDashboard() {
        abrirListadoMascotas();

        escribir("buscarMascota", E2eData.PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        esperarTexto(E2eData.PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-ficha-']"))).click();
        wait.until(ExpectedConditions.urlContains("/mascotas/"));
        esperarId("fichaMascotaNombre");

        esperarId("btnAsignarTratamiento").click();
        wait.until(ExpectedConditions.urlContains("/tratamiento/asignar"));
        esperarId("mascotaId");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@id='mascotaId']/option[contains(normalize-space(.), '" + E2eData.PET_NOMBRE + "')]")));
        seleccionarPorTextoVisible("mascotaId", E2eData.PET_NOMBRE);

        escribir("filtroMedicamento", E2eData.DRUG_NOMBRE);
        esperarTestId("medicamento-" + E2eData.DRUG_NOMBRE).click();
        esperarId("btnAplicarTratamiento").click();

        esperarId("modalTratamientoAplicado");
        assertThat(textoDe("stockRestante")).contains(String.valueOf(E2eData.DRUG_STOCK - 1));

        esperarId("btnVolverFicha").click();
        wait.until(ExpectedConditions.urlContains("/mascotas/"));
        esperarId("fichaMascotaNombre");
        assertThat(esperarTexto("Tratamiento con " + E2eData.DRUG_NOMBRE).isDisplayed()).isTrue();

        cerrarSesionVeterinario();

        abrirLogin();
        iniciarSesion(E2eData.ADMIN_EMAIL, E2eData.ADMIN_PASSWORD);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/admin/dashboard"));
        esperarId("kpiTratamientos");

        assertThat(numeroDesdeTexto(textoDe("kpiTratamientos"))).isEqualTo(1);
        assertThat(numeroDesdeTexto(textoDe("kpiVentas")))
                .isEqualTo((int) E2eData.DRUG_PRECIO_VENTA);
        assertThat(numeroDesdeTexto(textoDe("kpiGanancias")))
                .isEqualTo((int) (E2eData.DRUG_PRECIO_VENTA - E2eData.DRUG_PRECIO_COMPRA));
        assertThat(numeroDesdeTexto(textoDeTestId("cantidad-" + E2eData.DRUG_NOMBRE))).isEqualTo(1);

        var droga = drogaRepository.findAll().stream()
                .filter(registro -> E2eData.DRUG_NOMBRE.equals(registro.getNombre()))
                .findFirst()
                .orElseThrow();
        assertThat(droga.getUnidadesDisponibles()).isEqualTo(E2eData.DRUG_STOCK - 1);
        assertThat(droga.getUnidadesVendidas()).isEqualTo(1);
    }

    private void abrirListadoMascotas() {
        abrirLogin();
        iniciarSesion(E2eData.VET_EMAIL, E2eData.VET_PASSWORD);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarTexto(E2eData.PET_NOMBRE);
    }

    private void llenarCliente(String cedula) {
        escribir("cedula", cedula);
        escribir("nombre", E2eData.NEW_CLIENT_NOMBRE);
        escribir("correo", E2eData.NEW_CLIENT_EMAIL);
        escribir("contrasena", E2eData.NEW_CLIENT_PASSWORD);
        escribir("celular", E2eData.NEW_CLIENT_CELULAR);
    }

    private void llenarMascota() {
        escribir("nombre", E2eData.NEW_PET_NOMBRE);
        seleccionarPorTextoVisible("especie", E2eData.NEW_PET_ESPECIE);
        escribir("raza", E2eData.NEW_PET_RAZA);
        seleccionarPorTextoVisible("sexo", E2eData.NEW_PET_SEXO);
        escribir("edad", String.valueOf((int) E2eData.NEW_PET_EDAD));
        escribir("pesoKg", String.valueOf((int) E2eData.NEW_PET_PESO));
        escribir("color", E2eData.NEW_PET_COLOR);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@id='dueno']/option[contains(normalize-space(.), '" + E2eData.NEW_CLIENT_NOMBRE + "')]")));
        seleccionarPorTextoVisible("dueno",
                E2eData.NEW_CLIENT_NOMBRE + " (" + E2eData.NEW_CLIENT_CEDULA + ")");
    }
}
