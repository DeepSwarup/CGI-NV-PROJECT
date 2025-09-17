import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiBaseUrl}/ai`;

  getWeeklySummary(): Observable<{ summary: string }> {
    return this.http.get<{ summary: string }>(`${this.baseUrl}/summary/weekly`);
  }
}
