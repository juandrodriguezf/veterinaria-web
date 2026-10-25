import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, FooterPortalComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private loginService = inject(LoginService);

  private router = inject(Router);

  correo = '';

  contrasena = '';

  mostrarContrasena = false;

  mensajeError = '';

  ingresar() {
    this.mensajeError = '';
    // Validación local: si falta un campo no se consulta la API y el aviso
    // aparece de inmediato, igual que el mensaje del backend.
    const identificador = this.correo.trim();
    if (!identificador || !this.contrasena) {
      this.mensajeError = 'Ingresa tu cédula o correo y tu contraseña para continuar.';
      return;
    }
    this.correo = identificador;
    // Consulta: el componente reacciona a la respuesta y navega según el rol.
    this.loginService.autenticar(identificador, this.contrasena).subscribe({
      next: (resultado) => this.navegarPorRol(resultado.rol, resultado.id),
      error: (error) => {
        // El backend responde 401 con el mensaje exacto del service.
        this.mensajeError = error?.error?.mensaje ?? 'Cédula, correo o contraseña incorrectos. Verifica tus credenciales.';
      },
    });
  }

  private navegarPorRol(rol: string, id: number) {
    switch (rol) {
      case 'VETERINARIO':
        this.router.navigate(['/mascotas']);
        break;
      case 'CLIENTE':
        this.router.navigate(['/cliente/mascotas', id]);
        break;
      case 'ADMINISTRADOR':
        this.router.navigate(['/admin/dashboard']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}
