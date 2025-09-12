import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { Auth } from '../../services/auth/auth';
import { Profile, CustomerInfo } from '../../services/profile/profile';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction';
import { BeneficiaryService } from '../../services/beneficiary/beneficiary';

import { Account } from '../../models/account';
import { Transaction } from '../../models/transaction.model';
import { Beneficiary } from '../../models/beneficiary';

// const token = localStorage.getItem('authToken') || '';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(Auth);
  private profileService = inject(Profile);
  private accountService = inject(AccountService);
  private transactionService = inject(TransactionService);
  private beneficiaryService = inject(BeneficiaryService);
  

  // Main data signals
  customer = signal<CustomerInfo | null>(null);
  accounts = signal<Account[]>([]);
  recentTransactions = signal<Transaction[]>([]);
  beneficiaries = signal<Beneficiary[]>([]);
  isLoading = signal(true);
  userName = signal('');

  // Setup checklist signals
  isProfileComplete = signal(false);
  hasAddedNominee = signal(false);
  setupProgress = signal(0);
  
  // Modals and forms
  showAccountModal = signal(false);
  showTransferModal = signal(false);
  transferError = signal<string | null>(null);
  accountForm: FormGroup;
  transferForm: FormGroup;

  // Computed signal for transfer targets
  transferTargets = computed(() => {
    const userAccounts = this.accounts()
      .filter(acc => acc.status === 'ACTIVE')
      .map(acc => ({ id: acc.accountId, name: `${acc.accountType} Account - #${acc.accountId}` }));
    const beneficiaryAccounts = this.beneficiaries()
      .map(b => ({ id: b.beneficiaryAccNo, name: `${b.beneficiaryName} (Beneficiary)` }));
    return [...userAccounts, ...beneficiaryAccounts];
  });

  constructor() {
    this.accountForm = this.fb.group({
      accountType: ['SAVINGS', Validators.required],
      initialDeposit: [500, [Validators.required, Validators.min(100)]],
      minBalance: [500],
      // amount: [10000],
      months: [12],
    });

    this.transferForm = this.fb.group({
      senderAccountId: ['', Validators.required],
      receiverAccountId: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      remarks: [''], 
    });
  }

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => this.userName.set(user?.name.split(' ')[0] || ''));
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading.set(true);
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.customer.set(profile);
        this.isProfileComplete.set(true);
        this.accountService.getAccountsForCurrentUser().subscribe(accs => {
          this.accounts.set(accs);
          this.hasAddedNominee.set(accs.length > 0 && accs.every(a => a.nominees && a.nominees.length > 0));
          this.updateSetupProgress();

          if (accs.length > 0) {
            const firstActiveAccount = accs.find(a => a.status === 'ACTIVE');
            if (firstActiveAccount) {
              this.transactionService.getRecentTransactions(firstActiveAccount.accountId, 5).subscribe(txns => this.recentTransactions.set(txns));
              this.beneficiaryService.listAllBeneficiaries(firstActiveAccount.accountId).subscribe(bens => this.beneficiaries.set(bens));
            }
          }
          this.isLoading.set(false);
        });
      },
      error: () => this.isLoading.set(false)
    });
  }

  updateSetupProgress() {
    let completed = (this.isProfileComplete() ? 1 : 0) + (this.hasAddedNominee() ? 1 : 0);
    this.setupProgress.set((completed / 2) * 100);
  }

  // --- Modal & Form Logic ---
  openModal(type: 'account' | 'transfer') {
    if (type === 'account') this.showAccountModal.set(true);
    if (type === 'transfer') this.showTransferModal.set(true);
  }

  closeModal() {
    this.showAccountModal.set(false);
    this.showTransferModal.set(false);
    this.transferError.set(null);
    this.accountForm.reset({ accountType: 'SAVINGS', initialDeposit: 500 });
    this.transferForm.reset();
  }

  createAccount() {
    if (this.accountForm.invalid || !this.customer()) return;
    const { accountType, ...rest } = this.accountForm.value;
    // The payload is now simpler and correct for both account types
    const payload = { ...rest, customerId: this.customer()!.customerId };

    const apiCall = accountType === 'SAVINGS'
      ? this.accountService.createSavingsAccount(payload)
      : this.accountService.createTermAccount(payload);
    apiCall.subscribe({
      next: () => {
        alert('Account creation request sent!');
        this.closeModal();
        this.loadDashboardData();
      },
      error: (err) => alert(`Error: ${err.error?.message || 'Could not create account.'}`)
    });
  }

  executeTransfer() {
    if (this.transferForm.invalid) return;
    this.transferError.set(null);

    this.accountService.transferMoney(this.transferForm.value).subscribe({
      next: () => {
        alert('Transfer successful!');
        this.closeModal();
        this.loadDashboardData();
      },
      // Simplified error handling
      error: (err: any) => this.transferError.set(err.error?.message || 'Transfer failed.')
    });
  }

  // --- Navigation & UI Helpers ---
  getAccountStatusClass = (s?: string) => ({ 'status-dot-green': s === 'ACTIVE', 'status-dot-orange': s === 'PENDING', 'status-dot-red': s === 'DECLINED' || s === 'CLOSED' });
  navigate = (path: string) => this.router.navigate([path]);
}

