import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Account } from '../../models/account';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe],
  templateUrl: './transaction-list.html',
})
export class TransactionListComponent implements OnInit {
  private accountService = inject(AccountService);
  accounts = signal<Account[]>([]);
  isLoading = signal(true);

  ngOnInit() {
    this.accountService.getAccountsForCurrentUser().subscribe(accs => {
      this.accounts.set(accs.filter(a => a.status === 'ACTIVE')); // Only show active accounts
      this.isLoading.set(false);
    });
  }
}

