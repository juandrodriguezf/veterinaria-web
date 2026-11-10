package com.vetopia.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.VeterinarioRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MascotaDetalleE2E {

    private static final String BASE_URL = "http://localhost:4200";

    private static final String VET_EMAIL = "carlos.e2e@vetopia.com";
    private static final String VET_PASSWORD = "vet123";
    private static final String VET_CEDULA = "1002003001";
    private static final String VET_NOMBRE = "Carlos E2E";

    private static final String OWNER_CEDULA = "1000000000";
    private static final String OWNER_NOMBRE = "Ana E2E";
    private static final String OWNER_EMAIL = "ana.e2e@vetopia.com";
    private static final String OWNER_PASSWORD = "ana123";

    private static final String PET_NOMBRE = "Max";
    private static final String PET_ESPECIE = "Perro";
    private static final String PET_RAZA = "Doberman";
    private static final double PET_EDAD = 5.0;
    private static final String PET_SEXO = "Macho";
    private static final double PET_PESO = 32.0;
    private static final String PET_COLOR = "Negro";

    private WebDriver driver;

    private WebDriverWait wait;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @BeforeEach
    public void setUp() {
        sembrarDatos();
        ChromeOptions opciones = new ChromeOptions();
        opciones.addArguments("--disable-extensions");
        opciones.addArguments("--window-size=1440,900");
        opciones.addArguments("--lang=es-CO");
        if (Boolean.getBoolean("headless")) {
            opciones.addArguments("--headless=new");
        }
        driver = new ChromeDriver(opciones);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    void detalleMascota_verificaNombre() {
        abrirDetalleMascota();

        assertThat(textoDe("fichaMascotaNombre")).contains(PET_NOMBRE);
    }

    @Test
    void detalleMascota_verificaDatosClinicos() {
        abrirDetalleMascota();

        assertThat(textoDe("fichaMascotaEspecieRaza")).isEqualTo(PET_ESPECIE + " / " + PET_RAZA);
        assertThat(textoDe("fichaMascotaEdad")).isEqualTo("5 años");
        assertThat(textoDe("fichaMascotaPeso")).isEqualTo("32.0 kg");
        assertThat(esperarTexto("Sin tratamientos registrados para esta mascota.").isDisplayed()).isTrue();
    }

    private void abrirDetalleMascota() {
        driver.get(BASE_URL + "/login");
        esperarId("username");
        escribir("username", VET_EMAIL);
        escribir("password", VET_PASSWORD);
        esperarId("btnIngresar").click();
        wait.until(ExpectedConditions.urlContains("/mascotas"));

        escribir("buscarMascota", PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        esperarTexto(PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-ficha-']"))).click();
        wait.until(ExpectedConditions.urlContains("/mascotas/"));
        esperarId("fichaMascotaNombre");
    }

    private void sembrarDatos() {
        mascotaRepository.deleteAll();
        duenoRepository.deleteAll();
        veterinarioRepository.deleteAll();

        veterinarioRepository.save(Veterinario.builder()
                .cedula(VET_CEDULA)
                .contrasena(VET_PASSWORD)
                .correo(VET_EMAIL)
                .especialidad("Medicina General")
                .numeroAtenciones(0)
                .nombre(VET_NOMBRE)
                .estado("Activo")
                .build());

        Dueno dueno = duenoRepository.save(Dueno.builder()
                .cedula(OWNER_CEDULA)
                .nombre(OWNER_NOMBRE)
                .correo(OWNER_EMAIL)
                .contrasena(OWNER_PASSWORD)
                .celular("3000000000")
                .estado("Activo")
                .build());

        mascotaRepository.save(Mascota.builder()
                .nombre(PET_NOMBRE)
                .especie(PET_ESPECIE)
                .raza(PET_RAZA)
                .edad(PET_EDAD)
                .sexo(PET_SEXO)
                .pesoKg(PET_PESO)
                .color(PET_COLOR)
                .fechaIngreso(LocalDate.now().minusDays(10))
                .enfermedad("Control de rutina")
                .dueno(dueno)
                .estado("Activo")
                .build());
    }

    private WebElement esperarId(String id) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

    private WebElement esperarTexto(String texto) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(.), '" + texto + "')]")));
    }

    private void escribir(String id, String texto) {
        WebElement campo = esperarId(id);
        campo.clear();
        campo.sendKeys(texto);
    }

    private String textoDe(String id) {
        return esperarId(id).getText().trim();
    }
}
