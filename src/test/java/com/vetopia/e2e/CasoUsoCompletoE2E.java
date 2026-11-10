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

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CasoUsoCompletoE2E {

    private static final String BASE_URL = "http://localhost:4200";

    private static final String VET_EMAIL = "carlos.e2e@vetopia.com";
    private static final String VET_PASSWORD = "vet123";
    private static final String VET_CEDULA = "1002003001";
    private static final String VET_NOMBRE = "Carlos E2E";

    private static final String ADMIN_EMAIL = "admin.e2e@vetopia.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_CEDULA = "1000000001";
    private static final String ADMIN_NOMBRE = "Admin E2E";

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

    private static final String DRUG_NOMBRE = "TILOSINA";
    private static final double DRUG_PRECIO_COMPRA = 12000.0;
    private static final double DRUG_PRECIO_VENTA = 20000.0;
    private static final int DRUG_STOCK = 10;

    private static final String NEW_CLIENT_CEDULA = "1099887766";
    private static final String NEW_CLIENT_NOMBRE = "Cliente E2E";
    private static final String NEW_CLIENT_EMAIL = "cliente.e2e@vetopia.com";
    private static final String NEW_CLIENT_PASSWORD = "cliente123";
    private static final String NEW_CLIENT_CELULAR = "3001239876";

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
    private AdministradorRepository administradorRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private DrogaRepository drogaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

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

    /**
     * Caso 1 del enunciado: el veterinario falla el primer login y entra al
     * segundo intento; registra un cliente equivocándose en la cédula
     * (duplicada), corrige y guarda; registra la mascota asociada al nuevo
     * dueño; y por último el cliente entra con su cédula al portal y valida
     * los datos de la mascota.
     */
    @Test
    void caso1_registroClienteMascotaYPortalCliente() {
        driver.get(BASE_URL + "/login");
        esperarId("username");
        escribir("username", VET_EMAIL);
        escribir("password", "clave-incorrecta");
        esperarId("btnIngresar").click();
        assertThat(esperarId("mensajeLogin").isDisplayed()).isTrue();

        escribir("password", VET_PASSWORD);
        esperarId("btnIngresar").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));

        esperarId("navClientes").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/clientes"));
        esperarId("btnAgregarCliente").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/clientes/new"));
        esperarId("cedula");

        llenarCliente(OWNER_CEDULA);
        esperarId("btnRegistrarCliente").click();
        assertThat(esperarId("mensajeErrorCliente").getText())
                .contains("Ya existe una cuenta con la cédula");

        escribir("cedula", NEW_CLIENT_CEDULA);
        esperarId("btnRegistrarCliente").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/clientes"));
        esperarTestId("cliente-" + NEW_CLIENT_CEDULA);

        esperarId("navPacientes").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarId("btnAgregarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas/new"));
        esperarId("nombre");

        llenarMascota();
        esperarId("btnRegistrarMascota").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarTexto(NEW_PET_NOMBRE);

        cerrarSesionVeterinario();

        driver.get(BASE_URL + "/login");
        esperarId("username");
        escribir("username", NEW_CLIENT_CEDULA);
        escribir("password", NEW_CLIENT_PASSWORD);
        esperarId("btnIngresar").click();
        wait.until(ExpectedConditions.urlContains("/cliente/mascotas/"));
        esperarTexto(NEW_PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-detalle-']"))).click();
        wait.until(ExpectedConditions.urlContains("/detalle/"));

        assertThat(textoDe("detalleMascotaNombre")).isEqualTo(NEW_PET_NOMBRE);
        assertThat(textoDe("detalleMascotaEspecieRaza"))
                .contains(NEW_PET_RAZA)
                .contains(NEW_PET_ESPECIE);
        assertThat(esperarTexto("2 años").isDisplayed()).isTrue();
        assertThat(esperarTexto("10.0 kg").isDisplayed()).isTrue();
        assertThat(esperarTexto(NEW_PET_COLOR).isDisplayed()).isTrue();
    }

    /**
     * Caso 2 del enunciado: el veterinario busca la mascota, le asigna un
     * tratamiento (TILOSINA) y confirma el stock restante; vuelve a buscar la
     * mascota y valida el tratamiento en la ficha; finalmente el
     * administrador comprueba en el dashboard la cantidad de medicamentos y
     * las ganancias, además del inventario en base de datos.
     */
    @Test
    void caso2_tratamientoYValidacionDashboard() {
        driver.get(BASE_URL + "/login");
        esperarId("username");
        escribir("username", VET_EMAIL);
        escribir("password", VET_PASSWORD);
        esperarId("btnIngresar").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        esperarTexto(PET_NOMBRE);

        escribir("buscarMascota", PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        esperarTexto(PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-ficha-']"))).click();
        wait.until(ExpectedConditions.urlContains("/mascotas/"));
        esperarId("fichaMascotaNombre");

        esperarId("btnAsignarTratamiento").click();
        wait.until(ExpectedConditions.urlContains("/tratamiento/asignar"));
        esperarId("mascotaId");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@id='mascotaId']/option[contains(normalize-space(.), '" + PET_NOMBRE + "')]")));
        seleccionarPorTextoVisible("mascotaId", PET_NOMBRE);

        escribir("filtroMedicamento", DRUG_NOMBRE);
        esperarTestId("medicamento-" + DRUG_NOMBRE).click();
        esperarId("btnAplicarTratamiento").click();

        esperarId("modalTratamientoAplicado");
        assertThat(textoDe("stockRestante")).contains(String.valueOf(DRUG_STOCK - 1));

        esperarId("btnVolverFicha").click();
        wait.until(ExpectedConditions.urlContains("/mascotas/"));
        esperarId("fichaMascotaNombre");

        esperarId("navPacientes").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/mascotas"));
        escribir("buscarMascota", PET_NOMBRE);
        esperarId("btnBuscarMascota").click();
        esperarTexto(PET_NOMBRE);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[data-testid^='ver-ficha-']"))).click();
        wait.until(ExpectedConditions.urlContains("/mascotas/"));
        esperarId("fichaMascotaNombre");
        assertThat(esperarTexto("Tratamiento con " + DRUG_NOMBRE).isDisplayed()).isTrue();

        cerrarSesionVeterinario();

        driver.get(BASE_URL + "/login");
        esperarId("username");
        escribir("username", ADMIN_EMAIL);
        escribir("password", ADMIN_PASSWORD);
        esperarId("btnIngresar").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/admin/dashboard"));
        esperarId("kpiTratamientos");

        assertThat(numeroDesdeTexto(textoDe("kpiTratamientos"))).isEqualTo(1);
        assertThat(numeroDesdeTexto(textoDe("kpiVentas"))).isEqualTo((int) DRUG_PRECIO_VENTA);
        assertThat(numeroDesdeTexto(textoDe("kpiGanancias")))
                .isEqualTo((int) (DRUG_PRECIO_VENTA - DRUG_PRECIO_COMPRA));
        assertThat(numeroDesdeTexto(textoDeTestId("cantidad-" + DRUG_NOMBRE))).isEqualTo(1);

        var droga = drogaRepository.findAll().stream()
                .filter(registro -> DRUG_NOMBRE.equals(registro.getNombre()))
                .findFirst()
                .orElseThrow();
        assertThat(droga.getUnidadesDisponibles()).isEqualTo(DRUG_STOCK - 1);
        assertThat(droga.getUnidadesVendidas()).isEqualTo(1);
    }

    private void llenarCliente(String cedula) {
        escribir("cedula", cedula);
        escribir("nombre", NEW_CLIENT_NOMBRE);
        escribir("correo", NEW_CLIENT_EMAIL);
        escribir("contrasena", NEW_CLIENT_PASSWORD);
        escribir("celular", NEW_CLIENT_CELULAR);
    }

    private void llenarMascota() {
        escribir("nombre", NEW_PET_NOMBRE);
        seleccionarPorTextoVisible("especie", NEW_PET_ESPECIE);
        escribir("raza", NEW_PET_RAZA);
        seleccionarPorTextoVisible("sexo", NEW_PET_SEXO);
        escribir("edad", String.valueOf((int) NEW_PET_EDAD));
        escribir("pesoKg", String.valueOf((int) NEW_PET_PESO));
        escribir("color", NEW_PET_COLOR);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//*[@id='dueno']/option[contains(normalize-space(.), '" + NEW_CLIENT_NOMBRE + "')]")));
        seleccionarPorTextoVisible("dueno", NEW_CLIENT_NOMBRE + " (" + NEW_CLIENT_CEDULA + ")");
    }

    private void cerrarSesionVeterinario() {
        esperarId("btnLogoutVeterinario").click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
    }

    private void sembrarDatos() {
        tratamientoRepository.deleteAll();
        mascotaRepository.deleteAll();
        duenoRepository.deleteAll();
        drogaRepository.deleteAll();
        veterinarioRepository.deleteAll();
        administradorRepository.deleteAll();

        administradorRepository.save(Administrador.builder()
                .cedula(ADMIN_CEDULA)
                .correo(ADMIN_EMAIL)
                .contrasena(ADMIN_PASSWORD)
                .nombre(ADMIN_NOMBRE)
                .build());

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

        drogaRepository.save(Droga.builder()
                .nombre(DRUG_NOMBRE)
                .precioCompra(DRUG_PRECIO_COMPRA)
                .precioVenta(DRUG_PRECIO_VENTA)
                .unidadesDisponibles(DRUG_STOCK)
                .unidadesVendidas(0)
                .build());
    }

    private WebElement esperarId(String id) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

    private WebElement esperarTestId(String testId) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='" + testId + "']")));
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

    private String textoDe(String id) {
        return esperarId(id).getText().trim();
    }

    private String textoDeTestId(String testId) {
        return esperarTestId(testId).getText().trim();
    }

    private int numeroDesdeTexto(String texto) {
        String soloDigitos = texto.replaceAll("[^0-9]", "");
        return soloDigitos.isEmpty() ? 0 : Integer.parseInt(soloDigitos);
    }
}
