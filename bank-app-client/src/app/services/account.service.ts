import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Account } from '../models/account';
import { Customer } from '../models/customer';
import { Transaction } from '../models/transaction.model';

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
transferMoney(transferData: { senderAccountId: number; receiverAccountId: number; amount: number; remarks: string ; otp: string }): Observable<Transaction> {
    // The second argument here IS the request body. No need for HttpParams or manual headers.
    return this.http.post<Transaction>(`${this.baseUrl}/transfer`, transferData);
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