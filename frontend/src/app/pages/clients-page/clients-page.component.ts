import { Component, computed, inject, OnInit, signal, viewChild } from '@angular/core';
import { ClientService } from '../../client.service';
import { Client, ImportResult } from '../../client.model';
import { ListComponent } from '../../components/list/list.component';
import { FormComponent } from '../../components/form/form.component';
import { Router } from '@angular/router';
import { SearchComponent } from '../../components/search/search.component';
import { FileImportComponent } from '../../components/file-import/file-import.component';

@Component({
  selector: 'app-clients-page',
  imports: [ListComponent, FormComponent, SearchComponent, FileImportComponent],
  templateUrl: './clients-page.component.html',
  styleUrl: './clients-page.component.scss'
})
export class ClientsPageComponent implements OnInit {
  private clientService = inject(ClientService);
  private router = inject(Router);

  clients = signal<Client[]>([]);
  erreur = signal<string | null>(null);
  form = viewChild.required(FormComponent);
  terme = signal('');
  report = signal<ImportResult | null>(null);



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

  importFile(file: File): void {
  this.clientService.importFile(file).subscribe({
    next: result => {
      this.report.set(result);
      this.reload();
    },
    error: () => this.report.set({
      imported: 0,
      ignored: 0,
      errors: ['Import impossible : fichier .xlsx invalide ou trop volumineux']
    })
  });
}

  open(id: number): void {
  this.router.navigate(['/clients', id]);
}

  remove(id: number): void {
    this.clientService.delete(id).subscribe(() => this.reload());
  }

  clientsFiltres = computed(() => {
  const terme = normaliser(this.terme().trim());
  if (!terme) return this.clients();

  return this.clients().filter(client =>
    [client.nom, client.email, client.adresse].some(champ =>
      normaliser(champ ?? '').includes(terme)
    )
  );
});
}

function normaliser(texte: string): string {
  return texte.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
}