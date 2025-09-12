import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth/auth';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email = '';
  password = '';
  loginError = signal<string | null>(null); // Signal to hold the error message

  private authService = inject(Auth);
  private router = inject(Router);

  onLogin() {
    this.loginError.set(null); // Clear previous errors
    const data = { email: this.email, password: this.password };
    
    this.authService.login(data).subscribe({
      next: () => {
        console.log("Login Successful");
        // Navigation is handled by the auth service
      },
      error: (err) => {
        // Set the error signal with the message from the backend
        this.loginError.set(err.error?.message || 'Invalid email or password. Please try again.');
        console.error("Login failed: ", err);
      }
    });
  }
}
