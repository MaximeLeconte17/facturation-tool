import { Component, effect, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ClientService } from '../../client.service';
import { Client } from '../../client.model';

@Component({
  selector: 'app-client-detail-page',
  imports: [RouterLink],
  templateUrl: './client-detail-page.component.html',
  styleUrl: './client-detail-page.component.scss'
})
export class ClientDetailPageComponent {
  private clientService = inject(ClientService);

  id = input.required<string>();
  client = signal<Client | null>(null);
  error = signal<string | null>(null);

  constructor() {
    effect(() => {
      this.clientService.getClientById(Number(this.id())).subscribe({
        next: client => {
          this.client.set(client);
          this.error.set(null);
        },
        error: () => this.error.set('Client introuvable')
      });
    });
  }
}