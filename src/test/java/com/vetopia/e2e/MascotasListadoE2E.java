package com.vetopia.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vetopia.e2e.support.E2eBase;
import com.vetopia.e2e.support.E2eData;

public class MascotasListadoE2E extends E2eBase {

    @Test
    void listadoMascotas_buscaPorNombre_filtraResultados() {
        abrirListadoMascotas();

        assertThat(esperarTexto("1 mascota registrada").isDisplayed()).isTrue();

        escribir("buscarMascota", E2eData.PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        assertThat(esperarTexto("1 de 1 mascota").isDisplayed()).isTrue();
        assertThat(esperarTexto(E2eData.PET_NOMBRE).isDisplayed()).isTrue();

        escribir("buscarMascota", "NoExiste");
        esperarId("btnBuscarMascota").click();
        esperarTexto("No se encontraron mascotas con el nombre \"NoExiste\".");
        wait.until(driver -> driver.findElements(By.cssSelector("tr[data-testid^='mascota-']")).isEmpty());
    }

    @Test
    void listadoMascotas_agregaMascota_incrementaListado() {
        abrirListadoMascotas();

        assertThat(esperarTexto("1 mascota registrada").isDisplayed()).isTrue();

        esperarId("btnAgregarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas/new"));
        esperarId("nombre");

        escribir("nombre", E2eData.NEW_PET_NOMBRE);
        seleccionarPorTextoVisible("especie", E2eData.NEW_PET_ESPECIE);
        escribir("raza", E2eData.NEW_PET_RAZA);
        seleccionarPorTextoVisible("sexo", E2eData.NEW_PET_SEXO);
        escribir("edad", String.valueOf((int) E2eData.NEW_PET_EDAD));
        escribir("pesoKg", String.valueOf((int) E2eData.NEW_PET_PESO));
        escribir("color", E2eData.NEW_PET_COLOR);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@id='dueno']/option[contains(normalize-space(.), '" + E2eData.OWNER_NOMBRE + "')]")));
        seleccionarPorTextoVisible("dueno",
                E2eData.OWNER_NOMBRE + " (" + E2eData.OWNER_CEDULA + ")");

        esperarId("btnRegistrarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));

        assertThat(esperarTexto("2 mascotas registradas").isDisplayed()).isTrue();
        assertThat(esperarTexto(E2eData.NEW_PET_NOMBRE).isDisplayed()).isTrue();
    }

    @Test
    void listadoMascotas_desactivaMascota_cambiaEstado() {
        abrirListadoMascotas();

        By botonAlternar = By.cssSelector("button[data-testid^='toggle-mascota-']");
        wait.until(ExpectedConditions.elementToBeClickable(botonAlternar)).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//button[contains(@data-testid,'toggle-mascota-')]//span[normalize-space(.)='Activar']")));

        assertThat(mascotaRepository.findAll().stream()
                .filter(mascota -> E2eData.PET_NOMBRE.equals(mascota.getNombre()))
                .findFirst()
                .orElseThrow()
                .getEstado()).isEqualTo("Inactivo");
    }

    private void abrirListadoMascotas() {
        abrirLogin();
        iniciarSesion(E2eData.VET_EMAIL, E2eData.VET_PASSWORD);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarTexto(E2eData.PET_NOMBRE);
    }
}
