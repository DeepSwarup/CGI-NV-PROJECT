import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-account-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './account-creation.html',
  styleUrls: ['./account-creation.css']
})
export class AccountCreationComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);

  accountForm: FormGroup;
  accountTypes = ['SAVINGS', 'TERM'];
  isLoading = false;
  errorMessage = '';

  constructor() {
    this.accountForm = this.fb.group({
      accountType: ['SAVINGS', Validators.required],
      initialDeposit: [0, [Validators.required, Validators.min(1000)]],
      minBalance: [1000, Validators.min(0)],
      months: [12, [Validators.min(3), Validators.max(60)]]
    });
  }

  get isSavingsAccount() {
    return this.accountForm.get('accountType')?.value === 'SAVINGS';
  }

  get isTermAccount() {
    return this.accountForm.get('accountType')?.value === 'TERM';
  }

  onSubmit() {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formData = this.accountForm.value;
    const accountData = {
      accountType: formData.accountType,
      initialDeposit: formData.initialDeposit,
      ...(this.isSavingsAccount && { minBalance: formData.minBalance }),
      ...(this.isTermAccount && { months: formData.months })
    };

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    const url = 'http://localhost:8080/bank-api/accounts/' + 
                (this.isSavingsAccount ? 'savings' : 'term');

    this.http.post(url, accountData, { headers }).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        alert('Account created successfully! Waiting for admin approval.');
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Failed to create account';
      }
    });
  }
}