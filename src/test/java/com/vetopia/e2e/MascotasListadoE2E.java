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
import org.openqa.selenium.support.ui.Select;
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
public class MascotasListadoE2E {

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

    private static final String NEW_PET_NOMBRE = "Mascota E2E";
    private static final String NEW_PET_ESPECIE = "Perro";
    private static final String NEW_PET_RAZA = "Criollo";
    private static final String NEW_PET_SEXO = "Macho";
    private static final double NEW_PET_EDAD = 2.0;
    private static final double NEW_PET_PESO = 10.0;
    private static final String NEW_PET_COLOR = "Cafe";

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
    void listadoMascotas_buscaPorNombre_filtraResultados() {
        abrirListadoMascotas();

        assertThat(esperarTexto("1 mascota registrada").isDisplayed()).isTrue();

        escribir("buscarMascota", PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        assertThat(esperarTexto("1 de 1 mascota").isDisplayed()).isTrue();
        assertThat(esperarTexto(PET_NOMBRE).isDisplayed()).isTrue();

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

        escribir("nombre", NEW_PET_NOMBRE);
        seleccionarPorTextoVisible("especie", NEW_PET_ESPECIE);
        escribir("raza", NEW_PET_RAZA);
        seleccionarPorTextoVisible("sexo", NEW_PET_SEXO);
        escribir("edad", String.valueOf((int) NEW_PET_EDAD));
        escribir("pesoKg", String.valueOf((int) NEW_PET_PESO));
        escribir("color", NEW_PET_COLOR);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@id='dueno']/option[contains(normalize-space(.), '" + OWNER_NOMBRE + "')]")));
        seleccionarPorTextoVisible("dueno", OWNER_NOMBRE + " (" + OWNER_CEDULA + ")");

        esperarId("btnRegistrarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));

        assertThat(esperarTexto("2 mascotas registradas").isDisplayed()).isTrue();
        assertThat(esperarTexto(NEW_PET_NOMBRE).isDisplayed()).isTrue();
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
                .filter(mascota -> PET_NOMBRE.equals(mascota.getNombre()))
                .findFirst()
                .orElseThrow()
                .getEstado()).isEqualTo("Inactivo");
    }

    private void abrirListadoMascotas() {
        driver.get(BASE_URL + "/login");
        esperarId("username");
        escribir("username", VET_EMAIL);
        escribir("password", VET_PASSWORD);
        esperarId("btnIngresar").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarTexto(PET_NOMBRE);
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

    private void seleccionarPorTextoVisible(String id, String texto) {
        new Select(esperarId(id)).selectByVisibleText(texto);
    }
}
