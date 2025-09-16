import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OtpService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiBaseUrl}/otp`;

  generateOtp(type: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/generate`, { type });
  }
}
