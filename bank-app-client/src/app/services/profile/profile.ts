import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { customeralldetails } from '../../components/customer-details/customer-details';

// This interface is for CREATING or UPDATING a profile.
// It matches the backend DTO.
export interface ProfileInfo {
  phoneNo: string;
  age: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
}

// This interface is for VIEWING a profile.
// It includes the extra data the backend sends back.
export interface CustomerInfo extends ProfileInfo{
    customerId: number;
    email: string;
}

@Injectable({
  providedIn: 'root',
})
export class Profile {
  private baseUrl = `${environment.apiBaseUrl}/customers`;

  constructor(private http: HttpClient) {}

  // This is correct: it GETS the full customer info.
  getProfile(): Observable<CustomerInfo> {
    return this.http.get<CustomerInfo>(`${this.baseUrl}`);
  }

  // FIX HERE: This should only SEND the basic profile info.
  createProfile(data: ProfileInfo): Observable<{success: boolean}> {
    return this.http.post<{success: boolean}>(`${this.baseUrl}`, data);
  }

  // FIX HERE: This should also only SEND the basic profile info.
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
