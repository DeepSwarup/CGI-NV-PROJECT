import { Component, inject, signal } from '@angular/core';
import { Auth } from '../../services/auth/auth';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup {
  name = '';
  email = '';
  password = '';
  role: 'CUSTOMER' = 'CUSTOMER';
  signupError = signal<string | null>(null); // Signal for signup errors

  private authService = inject(Auth);

  onSignup() {
    this.signupError.set(null); // Clear previous error
    this.authService.signup({ name: this.name, email: this.email, password: this.password, role: this.role })
      .subscribe({
        next: (res) => console.log("Signup success:", res),
        error: (err) => {
          this.signupError.set(err.error?.message || 'Could not create account. The email might already be in use.');
          console.error("Signup failed: " + err.message);
        }
      });
  }
}
