import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive,RouterModule } from '@angular/router';
import { Auth } from './services/auth/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, RouterModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  private authService = inject(Auth);

  isAuthenticated = false;
  role: 'CUSTOMER' | 'ADMIN' | null = null
  userNameInitial = '';
  isSidebarVisible = signal(false); // Signal to manage sidebar visibility on mobile

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.isAuthenticated = !!user;
      this.role = user?.role || null
      if (user) {
        this.userNameInitial = user.name.charAt(0).toUpperCase();
      }
    });
  }

  logout() {
    this.authService.logout();
  }

  // Method to toggle the sidebar on and off
  toggleSidebar() {
    this.isSidebarVisible.set(!this.isSidebarVisible());
  }
}

