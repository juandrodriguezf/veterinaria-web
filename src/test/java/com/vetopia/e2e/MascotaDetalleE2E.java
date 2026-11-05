package com.vetopia.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vetopia.e2e.support.E2eBase;
import com.vetopia.e2e.support.E2eData;

public class MascotaDetalleE2E extends E2eBase {

    @Test
    void detalleMascota_verificaNombre() {
        abrirDetalleMascota();

        assertThat(textoDe("fichaMascotaNombre")).contains(E2eData.PET_NOMBRE);
    }

    @Test
    void detalleMascota_verificaDatosClinicos() {
        abrirDetalleMascota();

        assertThat(textoDe("fichaMascotaEspecieRaza"))
                .isEqualTo(E2eData.PET_ESPECIE + " / " + E2eData.PET_RAZA);
        assertThat(textoDe("fichaMascotaEdad")).isEqualTo("5 años");
        assertThat(textoDe("fichaMascotaPeso")).isEqualTo("32.0 kg");
        assertThat(esperarTexto("Sin tratamientos registrados para esta mascota.").isDisplayed()).isTrue();
    }

    private void abrirDetalleMascota() {
        abrirLogin();
        iniciarSesion(E2eData.VET_EMAIL, E2eData.VET_PASSWORD);
        esperarUrlContiene("/mascotas");

        escribir("buscarMascota", E2eData.PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        esperarTexto(E2eData.PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-ficha-']"))).click();
        esperarUrlContiene("/mascotas/");
        esperarId("fichaMascotaNombre");
    }
}
