import { Component } from '@angular/core';
import { NavbarLandingComponent } from '../../components/navbar-landing/navbar-landing.component';
import { FooterLandingComponent } from '../../components/footer-landing/footer-landing.component';

@Component({
  selector: 'app-landing',
  imports: [NavbarLandingComponent, FooterLandingComponent],
  templateUrl: './landing.component.html',
})
export class LandingComponent {}
