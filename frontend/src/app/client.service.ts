import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Client } from './client.model';

const API_URL = 'http://127.0.0.1:8080/api/clients';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  constructor(private http: HttpClient) {}

  lister(): Observable<Client[]> {
    return this.http.get<Client[]>(API_URL);
  }

  create(client: Client): Observable<Client> {
    return this.http.post<Client>(API_URL, client);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
