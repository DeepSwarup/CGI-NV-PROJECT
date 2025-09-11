import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Account } from '../../models/account';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe],
  templateUrl: './account-list.html',
})
export class AccountListComponent implements OnInit {
  private accountService = inject(AccountService);
  accounts = signal<Account[]>([]);
  isLoading = signal(true);

  ngOnInit() {
    this.accountService.getAccountsForCurrentUser().subscribe(accs => {
      this.accounts.set(accs);
      this.isLoading.set(false);
    });
  }

  getAccountStatusClass(status?: string) {
    switch (status) {
      case 'ACTIVE': return 'text-bg-success';
      case 'PENDING': return 'text-bg-warning';
      case 'DECLINED': case 'CLOSED': return 'text-bg-danger';
      default: return 'text-bg-secondary';
    }
  }
}

