import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, retry } from 'rxjs';
import { observableToBeFn } from 'rxjs/internal/testing/TestScheduler';
import { customeralldetails } from '../../components/customer-details/customer-details';

export interface ProfileInfo {
  phoneNo: string;
  age: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
}

// this is to test git



export interface CustomerInfo extends ProfileInfo{
    customerId: number;
    email: string;
}

@Injectable({
  providedIn: 'root',
})
export class Profile {
  private baseUrl = 'http://localhost:8080/bank-api/customers';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<ProfileInfo> {
    return this.http.get<ProfileInfo>(`${this.baseUrl}`);
  }

  createProfile(data: ProfileInfo): Observable<{success: boolean}> {
    return this.http.post<{success: boolean}>(`${this.baseUrl}`,data);
  }

  updateProfile(data: ProfileInfo): Observable<{success: boolean}> {
    return this.http.put<{success: boolean}>(`${this.baseUrl}`, data);
  }

  deleteProfile(): Observable<{success: boolean}> {
    return this.http.delete<{success: boolean}>(`${this.baseUrl}`);
  }

  getAllCustomers(): Observable<CustomerInfo[]>{
    return this.http.get<CustomerInfo[]>(`${this.baseUrl}/all`)
  }

  getCustomerById(id: number): Observable<customeralldetails>{
    return this.http.get<customeralldetails>(`${this.baseUrl}/${id}`)
  }
  
}
