import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Auth } from '../../services/auth/auth';
import { CustomerInfo, Profile } from '../../services/profile/profile';
import { RouterLink } from '@angular/router';

interface User {
  name: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
}

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit{

  users: User[] = []
  customers: CustomerInfo[]=[]

  private authService = inject(Auth)
  private profileService = inject(Profile)

  ngOnInit(){
    this.authService.getUsers().subscribe({
      next: (users)=> this.users=users,
      error: (e)=> console.log('Failed to load users: '+ e.message)
    })

    this.profileService.getAllCustomers().subscribe({
      next: (customers)=>{
        this.customers=customers
        console.log(customers);
        
      },
      error: (e)=> console.log('Failed to load users: '+ e.message)
    })
  }

}
