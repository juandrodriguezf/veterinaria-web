package com.vetopia.e2e.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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

import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;
import com.vetopia.service.DashboardService;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class E2eBase {

    protected static final String BASE_URL = "http://localhost:4200";

    protected WebDriver driver;

    protected WebDriverWait wait;

    @Autowired
    protected VeterinarioRepository veterinarioRepository;

    @Autowired
    protected AdministradorRepository administradorRepository;

    @Autowired
    protected DuenoRepository duenoRepository;

    @Autowired
    protected MascotaRepository mascotaRepository;

    @Autowired
    protected DrogaRepository drogaRepository;

    @Autowired
    protected TratamientoRepository tratamientoRepository;

    @Autowired
    protected DashboardService dashboardService;

    @BeforeEach
    protected void prepararEntorno() {
        E2eData.sembrar(veterinarioRepository, administradorRepository, duenoRepository,
                mascotaRepository, drogaRepository, tratamientoRepository);
        configurarDriver();
    }

    @AfterEach
    protected void cerrarDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    protected void configurarDriver() {
        WebDriverManager.chromedriver().setup();
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

    protected void abrirLogin() {
        driver.get(BASE_URL + "/login");
        esperarId("username");
    }

    protected void iniciarSesion(String usuario, String contrasena) {
        escribir("username", usuario);
        escribir("password", contrasena);
        esperarId("btnIngresar").click();
    }

    protected void cerrarSesionVeterinario() {
        esperarId("btnLogoutVeterinario").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
    }

    protected void cerrarSesionAdministrador() {
        esperarId("btnLogoutAdmin").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
    }

    protected void cerrarSesionCliente() {
        esperarId("btnLogoutCliente").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/login"));
    }

    protected void esperarUrlContiene(String fragmento) {
        wait.until(ExpectedConditions.urlContains(fragmento));
    }

    protected WebElement esperarId(String id) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

    protected WebElement esperarTestId(String testId) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='" + testId + "']")));
    }

    protected WebElement esperarTexto(String texto) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(.), '" + texto + "')]")));
    }

    protected void escribir(String id, String texto) {
        WebElement campo = esperarId(id);
        campo.clear();
        campo.sendKeys(texto);
    }

    protected void seleccionarPorTextoVisible(String id, String texto) {
        new Select(esperarId(id)).selectByVisibleText(texto);
    }

    protected String textoDe(String id) {
        return esperarId(id).getText().trim();
    }

    protected String textoDeTestId(String testId) {
        return esperarTestId(testId).getText().trim();
    }

    protected int numeroDesdeTexto(String texto) {
        String soloDigitos = texto.replaceAll("[^0-9]", "");
        return soloDigitos.isEmpty() ? 0 : Integer.parseInt(soloDigitos);
    }

    protected void capturarPantalla(String nombre) {
        if (driver == null) {
            return;
        }
        try {
            Path carpeta = Path.of("target", "e2e-screenshots");
            Files.createDirectories(carpeta);
            File origen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(origen.toPath(), carpeta.resolve(nombre + ".png"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException excepcion) {
            System.err.println("No se pudo guardar la captura: " + excepcion.getMessage());
        }
    }
}
