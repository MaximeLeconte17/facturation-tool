import { Component, inject, OnInit, signal, viewChild } from '@angular/core';
import { ClientService } from '../../client.service';
import { Client } from '../../client.model';
import { ListComponent } from '../../components/list/list.component';
import { FormComponent } from '../../components/form/form.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-clients-page',
  imports: [ListComponent, FormComponent],
  templateUrl: './clients-page.component.html',
  styleUrl: './clients-page.component.scss'
})
export class ClientsPageComponent implements OnInit {
  private clientService = inject(ClientService);
  private router = inject(Router);

  clients = signal<Client[]>([]);
  erreur = signal<string | null>(null);
  form = viewChild.required(FormComponent);

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.clientService.lister().subscribe(clients => this.clients.set(clients));
  }

  create(values: Record<string, string>): void {
    const client: Client = {
      nom: values['nom'],
      email: values['email'],
      adresse: values['adresse']
    };

    this.clientService.create(client).subscribe({
      next: () => {
        this.erreur.set(null);
        this.form().reset();
        this.reload();
      },
      error: () => this.erreur.set('Nom et email valides requis')
    });
  }

  open(id: number): void {
  this.router.navigate(['/clients', id]);
}

  remove(id: number): void {
    this.clientService.delete(id).subscribe(() => this.reload());
  }
}