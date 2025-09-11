import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, switchMap, catchError, of, map } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';
import { Profile } from '../profile/profile';

// Interfaces remain the same...
interface User {
  name: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
}

interface SignupData {
  name: string;
  email: string;
  password: string;
  role: 'CUSTOMER';
}

interface LoginData {
  email: string;
  password: string;
}

interface JwtPayload {
  name: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  sub: string
  exp: number;
}


@Injectable({
  providedIn: 'root'
})
export class Auth {

  private apiUrl = `${environment.apiBaseUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$: Observable<User | null> = this.currentUserSubject.asObservable();

  private http = inject(HttpClient);
  private router = inject(Router);
  private profileService = inject(Profile);

  constructor() {
    const token = localStorage.getItem('token');
    if (token) {
      const user = this.decodeToken(token);
      if (user && user.exp * 1000 > Date.now()) {
        this.currentUserSubject.next(user);
      } else {
        this.logout();
      }
    }
  }

  signup(data: SignupData): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, data).pipe(
      tap(() => this.router.navigate(['/login']))
    );
  }

  // V V V V V THIS IS THE MODIFIED LOGIN LOGIC V V V V V
  login(data: LoginData): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, data).pipe(
      tap(response => {
        // Step 1: Save token and decode user info
        localStorage.setItem('token', response.token);
        const user = this.decodeToken(response.token);
        this.currentUserSubject.next(user);

        // Step 2: Check the user's role and redirect
        if (user?.role === 'ADMIN') {
          // If user is an ADMIN, go directly to the admin dashboard
          this.router.navigate(['/admin/dashboard']);
        } 
        else if (user?.role === 'CUSTOMER') {
          // If user is a CUSTOMER, perform the profile check
          this.profileService.getProfile().subscribe({
            next: () => {
              // Profile exists, go to customer dashboard
              this.router.navigate(['/dashboard']);
            },
            error: (err) => {
              // Profile does not exist, go to create profile page
              if (err.status === 404 || err.status === 409) {
                this.router.navigate(['/create-profile']);
              }
            }
          });
        }
        else {
          // Fallback for safety
          this.router.navigate(['/']);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.router.navigate(['/']);
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    if (!token) return false;
    const user = this.decodeToken(token);
    return user != null ? (user.exp * 1000 > Date.now() ? true : false) : false
  }

  getRole(): 'CUSTOMER' | 'ADMIN' | null {
    return this.currentUserSubject.value?.role || null
  }
  
  private decodeToken(token: string): JwtPayload | null {
    try {
      return jwtDecode<JwtPayload>(token);
    } catch (e) {
      console.error("Failed to decode token", e);
      return null;
    }
  }
}
