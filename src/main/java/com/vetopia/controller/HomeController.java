package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vetopia.service.LoginService;
import com.vetopia.service.LoginService.ResultadoLogin;

/**
 * CONTROLLER (Página principal)
 * Gestiona la ruta raíz del sitio y retorna la vista index.
 * La plantilla vive en: src/main/resources/templates/landing/index.html
 */
@Controller
public class HomeController {

    /** Servicio de autenticación (login). */
    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public String index() {
        return "landing/index";
    }

    /**
     * Atiende GET /login: pantalla de inicio de sesión del portal.
     *
     * URL para visualizar: http://localhost:8080/login
     * Vista: src/main/resources/templates/login/login.html
     */
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    /**
     * Atiende POST /login: procesa las credenciales enviadas por el
     * formulario (correo y contraseña). El service busca en las tres
     * tablas (Veterinario, Administrador, Dueño) y se redirige al panel
     * correspondiente según el rol. Si las credenciales no coinciden en
     * ninguna tabla se vuelve al login mostrando un mensaje de error.
     */
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                Model model) {
        ResultadoLogin resultado = loginService.autenticar(username, password);
        if (resultado == null) {
            model.addAttribute("mensajeError", "Correo o contraseña incorrectos. Verifica tus credenciales.");
            return "login/login";
        }

        switch (resultado.rol()) {
            case "VETERINARIO":
                return "redirect:/veterinario/mascotas";
            case "ADMINISTRADOR":
                return "redirect:/";
            case "CLIENTE":
            default:
                return "redirect:/cliente/mascotas";
        }
    }
}
