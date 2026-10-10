import { Routes } from '@angular/router';
import { ClientsPageComponent } from './pages/clients-page/clients-page.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { ClientDetailPageComponent } from './pages/client-detail-page/client-detail-page.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomePageComponent },
  { path: 'clients', component: ClientsPageComponent },
  { path: 'clients/:id', component: ClientDetailPageComponent },
  { path: '**', redirectTo: 'home' }
];