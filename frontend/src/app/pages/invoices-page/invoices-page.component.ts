import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { SearchComponent } from '../../components/search/search.component';
import { Invoice, statusView } from '../../invoice.model';
import { InvoiceService } from '../../invoice.service';
import { apiErrorMessage } from '../../utils/api-error';
import { normalize } from '../../utils/text';

type InvoiceFilter = 'ALL' | 'DRAFT' | 'SENT' | 'OVERDUE' | 'PAID';

@Component({
  selector: 'app-invoices-page',
  imports: [
    FormsModule, CurrencyPipe, DatePipe, TableModule, TagModule, ButtonModule,
    SelectButtonModule, SearchComponent
  ],
  templateUrl: './invoices-page.component.html',
  styleUrl: './invoices-page.component.scss'
})
export class InvoicesPageComponent implements OnInit {
  private invoiceService = inject(InvoiceService);
  private router = inject(Router);
  private messages = inject(MessageService);

  invoices = signal<Invoice[]>([]);
  term = signal('');
  filter = signal<InvoiceFilter>('ALL');

  filterOptions: { label: string; value: InvoiceFilter }[] = [
    { label: 'Toutes', value: 'ALL' },
    { label: 'Brouillons', value: 'DRAFT' },
    { label: 'Envoyées', value: 'SENT' },
    { label: 'En retard', value: 'OVERDUE' },
    { label: 'Payées', value: 'PAID' }
  ];

  statusView = statusView;

  filteredInvoices = computed(() => {
    const term = normalize(this.term().trim());
    const filter = this.filter();

    return this.invoices().filter(invoice =>
      this.matchesFilter(invoice, filter) &&
      (!term || normalize(`${invoice.number ?? ''} ${invoice.clientName}`).includes(term))
    );
  });

  ngOnInit(): void {
    this.invoiceService.list().subscribe({
      next: invoices => this.invoices.set(invoices),
      error: err => this.messages.add({
        severity: 'error',
        summary: 'Chargement impossible',
        detail: apiErrorMessage(err)
      })
    });
  }

  open(id: number): void {
    this.router.navigate(['/factures', id]);
  }

  create(): void {
    this.router.navigate(['/factures', 'nouvelle']);
  }

  private matchesFilter(invoice: Invoice, filter: InvoiceFilter): boolean {
    switch (filter) {
      case 'ALL': return true;
      case 'DRAFT': return invoice.status === 'DRAFT';
      case 'SENT': return invoice.status === 'SENT' && !invoice.overdue;
      case 'OVERDUE': return invoice.overdue;
      case 'PAID': return invoice.status === 'PAID';
    }
  }
}