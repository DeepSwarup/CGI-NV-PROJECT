import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { Transaction } from '../../models/transaction.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-interest-log',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './interest-log.html',
})




export class InterestLogComponent implements OnInit {
  private adminService = inject(AdminService);

  transactions = signal<Transaction[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.adminService.getInterestLog().subscribe({
      next: (data) => {
        // Filter for "Interest Credit" remarks and sort by most recent
        const interestTxns = data
          .filter(tx => tx.transactionRemarks?.includes('Interest Credit'))
          .sort((a, b) => new Date(b.transactionDateandTime).getTime() - new Date(a.transactionDateandTime).getTime());
        this.transactions.set(interestTxns);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load interest transaction log.');
        this.isLoading.set(false);
      }
    });
  }
}
