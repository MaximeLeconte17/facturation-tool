export type InvoiceStatus = 'DRAFT' | 'SENT' | 'PAID';

export interface InvoiceLine {
  position: number;
  description: string;
  quantity: number;
  unitPriceExclTax: number;
  vatRate: number;
  totalExclTax: number;
}

export interface Invoice {
  id: number;
  number: string | null;
  clientId: number;
  clientName: string;
  status: InvoiceStatus;
  overdue: boolean;
  issueDate: string | null;
  dueDate: string;
  lines: InvoiceLine[];
  totalExclTax: number;
  totalVat: number;
  totalInclTax: number;
}

export interface InvoiceLineRequest {
  description: string;
  quantity: number;
  unitPriceExclTax: number;
  vatRate: number;
}

export interface InvoiceRequest {
  clientId: number;
  dueDate: string;
  lines: InvoiceLineRequest[];
}

export type TagSeverity = 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast';

export function statusView(invoice: Pick<Invoice, 'status' | 'overdue'>): { label: string; severity: TagSeverity } {
  if (invoice.overdue) {
    return { label: 'En retard', severity: 'danger' };
  }
  switch (invoice.status) {
    case 'DRAFT': return { label: 'Brouillon', severity: 'secondary' };
    case 'SENT': return { label: 'Envoyée', severity: 'info' };
    case 'PAID': return { label: 'Payée', severity: 'success' };
  }
}