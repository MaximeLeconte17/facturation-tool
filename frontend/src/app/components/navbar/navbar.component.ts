import { Component } from '@angular/core';
import { MegaMenu } from 'primeng/megamenu';
import { MegaMenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MegaMenu, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  items: MegaMenuItem[] = [
    { label: 'Accueil', icon: 'pi pi-home', routerLink: '/home'  },
    { label: 'Clients', icon: 'pi pi-users', routerLink: '/clients'  },
    { label: 'Factures', icon: 'pi pi-file'}
  ];
}