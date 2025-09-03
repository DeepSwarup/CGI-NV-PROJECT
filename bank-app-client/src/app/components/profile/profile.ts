import { Component, inject } from '@angular/core';
import { Auth } from '../../services/auth/auth';
import { CommonModule } from '@angular/common';
import { CustomerProfile } from '../customer-profile/customer-profile';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, CustomerProfile],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  // user: { username: string; role: 'user' | 'admin' } | null = null
  user: {
    name: string;
    email: string;
    role: 'CUSTOMER' | 'ADMIN';
  } | null = null;
  
  private authService = inject(Auth);

  ngOnInit() {
    this.authService.currentUser$.subscribe((user) => {
      // console.log(user)
      this.user = user
    });
  }
}
