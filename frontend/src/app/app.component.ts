import { Component, inject, OnInit, signal } from '@angular/core';
import { ClientService } from './client.service';
import { Client } from './client.model';
import { ListComponent } from './components/list/list.component';
import { ClientFormComponent } from './components/client-form/client-form.component';

@Component({
  selector: 'app-root',
  imports: [ListComponent, ClientFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  private clientService = inject(ClientService);

  public clients = signal<Client[]>([]);

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.clientService.lister().subscribe(clients => this.clients.set(clients));
  }

  remove(id:number): void {
    this.clientService.delete(id).subscribe(() => {this.reload()})
  }
}
  