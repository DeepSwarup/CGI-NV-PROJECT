import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Account } from '../models/account';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiBaseUrl}/admin`;

  /**
   * Gets a list of all accounts in the system.
   */
  getAllAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.baseUrl}/accounts`);
  }

  /**
   * Sends a request to approve a pending account.
   */
  approveAccount(accountId: number): Observable<Account> {
    return this.http.post<Account>(`${this.baseUrl}/accounts/${accountId}/approve`, {});
  }

  /**
   * Sends a request to decline a pending account.
   */
  declineAccount(accountId: number): Observable<Account> {
    return this.http.post<Account>(`${this.baseUrl}/accounts/${accountId}/decline`, {});
  }

  updateInterestRate(accountId: number, interestRate: number): Observable<Account> {
    const payload = { interestRate }; // Create the body object { "interestRate": 5.5 }
    return this.http.post<Account>(`${this.baseUrl}/accounts/${accountId}/interest-rate`, payload);
  }
}
