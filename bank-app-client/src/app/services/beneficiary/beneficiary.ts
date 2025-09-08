import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, forkJoin, of, switchMap, map, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account } from '../../models/account';
import { Beneficiary, CreateBeneficiaryRequest, UpdateBeneficiaryRequest } from '../../models/beneficiary';
import { Customer } from '../../models/customer';

@Injectable({
  providedIn: 'root'
})
export class BeneficiaryService {
  private http = inject(HttpClient);

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });
  }

  fetchAccountsWithBeneficiaries(): Observable<Account[]> {
    console.log('Fetching customer and accounts...');

    const headers = this.getHeaders();

    return this.http.get<Customer>(environment.customersUrl, { headers })
      .pipe(
        switchMap(customer => {
          console.log("Resolved customer:", customer);
          return this.http.get<Account[]>(`${environment.accountsUrl}/${customer.customerId}`, { headers });
        }),
        switchMap(accounts => {
          console.log('Accounts fetched successfully:', accounts);
          console.log('Total accounts found:', accounts.length);

          if (!accounts || accounts.length === 0) {
            console.log('No accounts found for customer');
            return of([]);
          }

          const beneficiaryRequests = accounts.map(account =>
            this.http.get<Beneficiary[]>(`${environment.beneficiariesUrl}/account/${account.accountId}`, { headers })
              .pipe(
                catchError(err => {
                  if (err.status === 404) {
                    console.log(`No beneficiaries found for account ${account.accountId}`);
                    return of([]);
                  }
                  console.error(`Error fetching beneficiaries for account ${account.accountId}:`, err);
                  return of([]);
                })
              )
          );

          // Combine accounts with their beneficiaries
          return forkJoin<Beneficiary[][]>(beneficiaryRequests).pipe(
            map((beneficiariesArrays: Beneficiary[][]) => {
              console.log("beneficiaries:", beneficiariesArrays);
              const result = accounts.map((account, index) => ({
                ...account,
                beneficiaries: beneficiariesArrays[index] || []
              }));
              console.log('Accounts with beneficiaries:', result);
              return result;
            })
          );
        })
      );
  }

  createBeneficiary(beneficiaryData: any): Observable<any> {
    console.log('Creating new beneficiary:', beneficiaryData);

    const headers = this.getHeaders();
    const createRequest: CreateBeneficiaryRequest = this.processBeneficiaryData(beneficiaryData, false);

    return this.http.post(environment.beneficiariesUrl, createRequest, { headers });
  }


  updateBeneficiary(beneficiaryId: number, beneficiaryData: any): Observable<any> {
    console.log('Updating beneficiary with ID:', beneficiaryId, beneficiaryData);

    const headers = this.getHeaders();
    const updateRequest: UpdateBeneficiaryRequest = this.processBeneficiaryData(beneficiaryData, true);

    return this.http.put(`${environment.beneficiariesUrl}/${beneficiaryId}`, updateRequest, { headers });
  }


  deleteBeneficiary(beneficiaryId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.delete(`${environment.beneficiariesUrl}/${beneficiaryId}`, {
      headers,
      responseType: 'text'
    });
  }


  private processBeneficiaryData(formData: any, isUpdate: false): CreateBeneficiaryRequest;
  private processBeneficiaryData(formData: any, isUpdate: true): UpdateBeneficiaryRequest;
  private processBeneficiaryData(formData: any, isUpdate: boolean): CreateBeneficiaryRequest | UpdateBeneficiaryRequest {
    const baseData: CreateBeneficiaryRequest = {
      beneficiaryName: formData.beneficiaryName?.trim() || '',
      beneficiaryAccNo: Number(formData.beneficiaryAccNo) || 0,
      ifsc: formData.ifsc?.trim()?.toUpperCase() || '',
      accountType: formData.accountType?.toUpperCase() || '',
      accountId: formData.accountId
    };

    if (isUpdate) {
      return {
        ...baseData,
        beneficiaryId: formData.beneficiaryId
      };
    }

    return baseData;
  }


  validateBeneficiaryData(formValue: any): string | null {
    if (!formValue.accountId || formValue.accountId === 0) {
      return 'Please select an account';
    }
    if (!formValue.beneficiaryName?.trim()) {
      return 'Please enter beneficiary name';
    }
    if (!formValue.beneficiaryAccNo || formValue.beneficiaryAccNo === 0) {
      return 'Please enter beneficiary account number';
    }
    // Check if account number is 11 digits
    const accNoStr = formValue.beneficiaryAccNo.toString();
    if (accNoStr.length !== 11) {
      return 'Account number must be exactly 11 digits';
    }
    if (!formValue.ifsc?.trim()) {
      return 'Please enter IFSC code';
    }
    if (!formValue.accountType) {
      return 'Please select account type';
    }
    return null;
  }


  canAddBeneficiaryToAccount(account: Account): boolean {
    const currentCount = account.beneficiaries?.length || 0;
    return currentCount < environment.validation.maxBeneficiariesPerAccount;
  }

  findBeneficiaryInAccounts(beneficiaryId: string, accounts: Account[]): { beneficiary: Beneficiary | null, account: Account | null } {
    for (const account of accounts) {
      if (account.beneficiaries) {
        const beneficiary = account.beneficiaries.find(b => b.beneficiaryId.toString() === beneficiaryId);
        if (beneficiary) {
          return { beneficiary, account };
        }
      }
    }
    return { beneficiary: null, account: null };
  }


  findAccountById(accountId: number, accounts: Account[]): Account | null {
    return accounts.find(acc => acc.accountId == accountId) || null;
  }

}
