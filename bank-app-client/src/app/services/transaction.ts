import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, shareReplay, catchError } from 'rxjs/operators';
import { Transaction } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private static readonly API_ROOT = 'http://localhost:8080/bank-api';
  private readonly base = `${TransactionService.API_ROOT}/transactions`;

  constructor(private http: HttpClient) {}

  private handleError(err: HttpErrorResponse) {
    if (err.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return throwError(() => err);
  }

  getRecentTransactions(accountId: number, limit = 12): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/account/${accountId}/all`).pipe(
      catchError(this.handleError),
      map(list =>
        [...list]
          .sort((a, b) => new Date(b.transactionDateandTime).getTime() - new Date(a.transactionDateandTime).getTime())
          .slice(0, limit)
      ),
      shareReplay({ bufferSize: 1, refCount: true })
    );
  }

  getTransactionsByDateRange(accountId: number, from: string, to: string): Observable<Transaction[]> {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<Transaction[]>(`${this.base}/account/${accountId}`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getTransactionById(transactionId: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.base}/${transactionId}`).pipe(
      catchError(this.handleError)
    );
  }
}