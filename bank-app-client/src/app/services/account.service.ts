import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Account } from '../models/account';
import { Customer } from '../models/customer';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiBaseUrl}/accounts`;
  private customerUrl = `${environment.apiBaseUrl}/customers`;

  // Fetch accounts for the currently logged-in customer
  getAccountsForCurrentUser(): Observable<Account[]> {
    // First, get the current customer's ID
    return this.http.get<Customer>(this.customerUrl).pipe(
      // Then, use the ID to get their accounts
      switchMap(customer => {
        if (!customer || !customer.customerId) {
          return of([]); // Return empty array if no customer found
        }
        return this.http.get<Account[]>(`${this.baseUrl}/customer/${customer.customerId}`);
      })
    );
  }

   transferMoney(
  transferData: { senderAccountId: number; receiverAccountId: number; amount: number },
  token: string
): Observable<any> {
  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
  });

  const params = new HttpParams()
    .set('senderAccountId', transferData.senderAccountId)
    .set('receiverAccountId', transferData.receiverAccountId)
    .set('amount', transferData.amount);

  return this.http.post(`${this.baseUrl}/transfer`, null, {
    headers,
    params
  });
}


  // Create a new savings account
  createSavingsAccount(data: { customerId: number; initialDeposit: number; minBalance: number }): Observable<Account> {
    return this.http.post<Account>(`${this.baseUrl}/savings`, data);
  }

  // Create a new term account
  createTermAccount(data: { customerId: number; initialDeposit: number; amount: number; months: number }): Observable<Account> {
    return this.http.post<Account>(`${this.baseUrl}/term`, data);
  }
}