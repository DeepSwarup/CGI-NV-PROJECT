import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerProfile } from '../customer-profile/customer-profile';
import { Auth } from '../../services/auth/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, CustomerProfile],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
})
export class Profile implements OnInit {
  private authService = inject(Auth);
  
  userName = signal('');
  userEmail = signal('');

  ngOnInit() {
    this.authService.currentUser$.subscribe((user) => {
      this.userName.set(user?.name || 'User');
      this.userEmail.set(user?.email || '');
    });
  }
}
