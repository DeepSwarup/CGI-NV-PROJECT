import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, forkJoin, of, switchMap, map, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account } from '../../models/account';
import { CreateNomineeRequest, Nominee, UpdateNomineeRequest } from '../../models/nominee';
import { Customer } from '../../models/customer';


@Injectable({
  providedIn: 'root'
})
export class NomineeService {
  private http = inject(HttpClient);


  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });
  }


  fetchAccountsWithNominees(): Observable<Account[]> {
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

          // Fetch nominees for each account
          const nomineeRequests = accounts.map(account =>
            this.http.get<Nominee[]>(`${environment.nomineesUrl}/${account.accountId}`, { headers })
              .pipe(
                catchError(err => {
                  if (err.status === 404) {
                    console.log(`No nominees found for account ${account.accountId}`);
                    return of([]);
                  }
                  console.error(`Error fetching nominees for account ${account.accountId}:`, err);
                  return of([]);
                })
              )
          );

          // Combine accounts with their nominees
          return forkJoin<Nominee[][]>(nomineeRequests).pipe(
            map((nomineesArrays: Nominee[][]) => {
              console.log("nominees:", nomineesArrays);
              const result = accounts.map((account, index) => ({
                ...account,
                nominees: nomineesArrays[index] || []
              }));
              console.log('Accounts with nominees:', result);
              return result;
            })
          );
        })
      );
  }


  createNominee(nomineeData: any): Observable<any> {
    console.log('Creating new nominee:', nomineeData);
    const headers = this.getHeaders();
    const createRequest: CreateNomineeRequest = this.processNomineeData(nomineeData, false);
    return this.http.post(environment.nomineesUrl, createRequest, { headers });
  }


  updateNominee(nomineeId: number, nomineeData: any): Observable<any> {
    console.log('Updating nominee with ID:', nomineeId, nomineeData);
    const headers = this.getHeaders();
    const updateRequest: UpdateNomineeRequest = this.processNomineeData(nomineeData, true);
    return this.http.put(`${environment.nomineesUrl}/${nomineeId}`, updateRequest, { headers });
  }


  deleteNominee(nomineeId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.delete(`${environment.nomineesUrl}/${nomineeId}`, {
      headers,
      responseType: 'text'
    });
  }

  /**
   * Process nominee data (clean and format)
   */
  private processNomineeData(formData: any, isUpdate: false): CreateNomineeRequest;
  private processNomineeData(formData: any, isUpdate: true): UpdateNomineeRequest;
  private processNomineeData(formData: any, isUpdate: boolean): CreateNomineeRequest | UpdateNomineeRequest {
    const baseData: CreateNomineeRequest = {
      name: formData.name?.trim() || '',
      govtId: formData.govtId?.trim() || '',
      govtIdType: formData.govtIdType?.toUpperCase() || '',
      phoneNo: formData.phoneNo?.trim() || '',
      relation: formData.relation?.toUpperCase() || '',
      accountId: formData.accountId
    };

    if (isUpdate) {
      return {
        ...baseData,
        nomineeId: formData.nomineeId
      };
    }

    return baseData;
  }


  validateNomineeData(formValue: any): string | null {
    if (!formValue.accountId || formValue.accountId === 0) {
      return 'Please select an account';
    }
    if (!formValue.name?.trim()) {
      return 'Please enter nominee name';
    }
    if (!formValue.relation) {
      return 'Please select relationship';
    }
    if (!formValue.phoneNo?.trim()) {
      return 'Please enter phone number';
    }
    if (!formValue.govtIdType) {
      return 'Please select government ID type';
    }
    if (!formValue.govtId?.trim()) {
      return 'Please enter government ID number';
    }
    return null;
  }


  canAddNomineeToAccount(account: Account): boolean {
    const currentCount = account.nominees?.length || 0;
    return currentCount < environment.validation.maxNomineesPerAccount;
  }

  findNomineeInAccounts(nomineeId: string, accounts: Account[]): { nominee: Nominee | null, account: Account | null } {
    for (const account of accounts) {
      if (account.nominees) {
        const nominee = account.nominees.find(n => n.nomineeId.toString() === nomineeId);
        if (nominee) {
          return { nominee, account };
        }
      }
    }
    return { nominee: null, account: null };
  }


  findAccountById(accountId: number, accounts: Account[]): Account | null {
    return accounts.find(acc => acc.accountId == accountId) || null;
  }


  getIdPlaceholder(govtIdType: string): string {
    switch (govtIdType?.toLowerCase()) {
      case 'aadhar':
        return 'Enter Aadhar number';
      case 'pan':
        return 'Enter PAN number';
      case 'passport':
        return 'Enter passport number';
      case 'driving_license':
        return 'Enter driving license number';
      default:
        return 'Enter ID number';
    }
  }
}


