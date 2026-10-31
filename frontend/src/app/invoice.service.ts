import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Invoice, InvoiceRequest } from './invoice.model';

const API_URL = 'http://127.0.0.1:8080/api/invoices';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private http = inject(HttpClient);

  list(clientId?: number): Observable<Invoice[]> {
    let params = new HttpParams();
    if (clientId !== undefined) {
      params = params.set('clientId', clientId);
    }
    return this.http.get<Invoice[]>(API_URL, { params });
  }

  get(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${API_URL}/${id}`);
  }

  create(request: InvoiceRequest): Observable<Invoice> {
    return this.http.post<Invoice>(API_URL, request);
  }

  update(id: number, request: InvoiceRequest): Observable<Invoice> {
    return this.http.put<Invoice>(`${API_URL}/${id}`, request);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  send(id: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${API_URL}/${id}/send`, {});
  }

  pay(id: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${API_URL}/${id}/pay`, {});
  }
}