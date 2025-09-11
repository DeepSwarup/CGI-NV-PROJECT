import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { Account } from '../../models/account';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  accounts = signal<Account[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  // State for the interest rate modal
  showInterestModal = signal(false);
  currentAccountForEdit = signal<Account | null>(null);
  interestRateForm: FormGroup;

  constructor() {
    this.interestRateForm = this.fb.group({
      interestRate: ['', [Validators.required, Validators.min(0), Validators.max(20)]]
    });
  }

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.isLoading.set(true);
    this.adminService.getAllAccounts().subscribe({
      next: (data) => {
        // Sort to show PENDING accounts on top
        data.sort((a, b) => (a.status === 'PENDING' ? -1 : b.status === 'PENDING' ? 1 : 0));
        this.accounts.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load accounts. You may not have permission.');
        this.isLoading.set(false);
        console.error(err);
      }
    });
  }

  approve(accountId: number) {
    this.adminService.approveAccount(accountId).subscribe({ next: () => this.loadAccounts() });
  }

  decline(accountId: number) {
    this.adminService.declineAccount(accountId).subscribe({ next: () => this.loadAccounts() });
  }

  // --- Methods for Interest Rate Modal ---
  openInterestModal(account: Account) {
    this.currentAccountForEdit.set(account);
    this.interestRateForm.patchValue({ interestRate: account.interestRate });
    this.showInterestModal.set(true);
  }

  closeInterestModal() {
    this.showInterestModal.set(false);
    this.currentAccountForEdit.set(null);
    this.interestRateForm.reset();
  }

  saveInterestRate() {
    if (this.interestRateForm.invalid || !this.currentAccountForEdit()) return;

    const accountId = this.currentAccountForEdit()!.accountId;
    const newRate = this.interestRateForm.value.interestRate;

    this.adminService.updateInterestRate(accountId, newRate).subscribe({
      next: () => {
        this.closeInterestModal();
        this.loadAccounts(); // Refresh the list to show the new rate
      },
      error: (err) => alert(`Failed to update rate: ${err.message}`)
    });
  }

  getAccountStatusClass(status?: string) {
    switch (status) {
      case 'ACTIVE': return 'text-bg-success';
      case 'PENDING': return 'text-bg-warning';
      case 'DECLINED':
      case 'CLOSED': return 'text-bg-danger';
      default: return 'text-bg-secondary';
    }
  }
}

