import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { Account } from '../../models/account';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Routes } from '@angular/router';
import { InterestLogComponent } from '../interest-log/interest-log';
import { RouterLink } from '@angular/router';

const routes: Routes = [
  {
    path: 'admin',
    children: [
      { path: 'interest-log', component: InterestLogComponent }
    ]
  }
];


@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})




export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  accounts = signal<Account[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  // --- State for Term Account Approval Modal ---
  showApprovalModal = signal(false);
  accountToApprove = signal<Account | null>(null);
  approvalForm: FormGroup;

  // --- State for Set Interest Rate Modal ---
  showInterestModal = signal(false);
  currentAccountForEdit = signal<Account | null>(null);
  interestRateForm: FormGroup;

  constructor() {
    // Form for approving a Term Account
    this.approvalForm = this.fb.group({
      interestRate: ['', [Validators.required, Validators.min(0), Validators.max(20)]],
      penaltyAmount: ['', [Validators.required, Validators.min(0)]]
    });

    // Form for setting the interest rate of an already ACTIVE account
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


  creditInterest(accountId: number) {
    if (confirm(`Are you sure you want to calculate and credit interest for Account #${accountId}?`)) {
      this.adminService.creditInterest(accountId).subscribe({
        next: () => {
          alert('Interest credited successfully!');
          this.loadAccounts(); // Refresh the list to show the new balance
        },
        error: (err) => alert(`Error: ${err.error?.message || 'Could not credit interest.'}`)
      });
    }
  }

  approveSavings(accountId: number) {
    this.adminService.approveSavingsAccount(accountId).subscribe({ next: () => this.loadAccounts() });
  }

  decline(accountId: number) {
    this.adminService.declineAccount(accountId).subscribe({ next: () => this.loadAccounts() });
  }

  // --- Methods for Term Account Approval Modal ---
  openApprovalModal(account: Account) {
    this.accountToApprove.set(account);
    this.approvalForm.patchValue({
      interestRate: account.interestRate || 6.0,
      penaltyAmount: account.penaltyAmount || 0
    });
    this.showApprovalModal.set(true);
  }

  closeApprovalModal() {
    this.showApprovalModal.set(false);
    this.accountToApprove.set(null);
    this.approvalForm.reset();
  }

  submitApproval() {
    if (this.approvalForm.invalid || !this.accountToApprove()) return;
    const accountId = this.accountToApprove()!.accountId;
    const formData = this.approvalForm.value;
    this.adminService.approveTermAccount(accountId, formData).subscribe({
      next: () => {
        this.closeApprovalModal();
        this.loadAccounts();
      },
      error: (err) => alert(`Failed to approve: ${err.message}`)
    });
  }
  
  // --- Methods for Set Interest Rate Modal ---
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
        this.loadAccounts();
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

