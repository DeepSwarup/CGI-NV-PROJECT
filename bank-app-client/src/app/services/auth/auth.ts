import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import {jwtDecode} from 'jwt-decode';

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

  private apiUrl = 'http://localhost:8080/bank-api';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$: Observable<User| null> = this.currentUserSubject.asObservable();

  constructor(private router: Router, private http: HttpClient){
    const token = localStorage.getItem('token');
    if(token){
      const user = this.decodeToken(token)
      if (user && user.exp*1000> Date.now()){
        this.currentUserSubject.next(user)
      }else{
        this.logout()
      }
    }
  }

  signup(data: SignupData): Observable<any>{

    return this.http.post(`${this.apiUrl}/auth/signup`, data).pipe(
      tap(()=>this.router.navigate(['/login']))
    )

  }

  login(data: LoginData): Observable<{token: string}>{
    console.log("service login data", data)
    return this.http.post<{ token: string }>(
      `${this.apiUrl}/auth/login`,
      data,
      {headers: {"Content-Type": "application/json"}}
    )
    .pipe(
      tap(response=>{
        localStorage.setItem('token', response.token)
        const user = this.decodeToken(response.token)
        this.currentUserSubject.next(user)
        this.router.navigate(['/profile'])
      })
    )
  }

  logout():void{
    localStorage.removeItem('token')
    this.currentUserSubject.next(null)
    this.router.navigate(['/'])
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    if (!token) return false;
    const user = this.decodeToken(token);
    return user!=null ? (user.exp*1000> Date.now() ? true: false) : false
  }

  getRole(): 'CUSTOMER' | 'ADMIN' | null {
    return this.currentUserSubject.value?.role || null
  }

  getUsers():Observable<User[]>{
    return this.http.get<User[]>(`${this.apiUrl}/admin`);
  }

  private decodeToken(token: string): JwtPayload | null {
    try{
      const payload = jwtDecode<JwtPayload>(token)
      console.log(payload)
      return {
        name: payload.name,
        email: payload.email,
        role: payload.role,
        sub: payload.sub,
        exp: payload.exp
      }
    }catch (e){
      console.log("error occcured uppon decoding",e)
      return null
    }
  }
  
}
