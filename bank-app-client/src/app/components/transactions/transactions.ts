import { Component, OnDestroy, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { TransactionService } from '../../services/transaction';
import { Transaction } from '../../models/transaction.model';
import { HostListener } from '@angular/core';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ReactiveFormsModule],
  templateUrl: './transactions.html',
  styleUrls: ['./transactions.css']
})
export class Transactions implements OnInit, OnDestroy {
  accountId = signal<number | null>(null);
  allTransactions = signal<Transaction[]>([]);
  transactions = signal<Transaction[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  searchTerm = signal<string>('');
  toastCounter = 0;
  toasts = signal<{ id: number; message: string; type: 'error' | 'info' }[]>([]);

// modal state for date range
// showDateRangeModalSignal = signal(false);
// modalFrom = signal<string>('');
// modalTo = signal<string>('');

   dropdownOpen = {
  type: false,
  status: false
};

filteredCount = computed(() => this.filteredTransactions().length);

firstItemIndex = computed(() => {
  const total = this.filteredCount();
  if (total === 0) return 0;
  return (this.currentPage() - 1) * this.pageSize() + 1;
});

lastItemIndex = computed(() => {
  return Math.min(this.currentPage() * this.pageSize(), this.filteredCount());
});

  // Pagination
  pageSize = signal(10);
  pageSizeOptions = [5, 10, 20, 50];
  currentPage = signal(1);

  totalPages = computed(() =>
    Math.ceil(this.filteredTransactions().length / this.pageSize()) || 1
  );
  totalPagesArray = computed(() =>
    Array.from({ length: this.totalPages() }, (_, i) => i + 1)
  );

  filterForm: FormGroup;
  private subs: Subscription[] = [];

  constructor(
    private transactionService: TransactionService,
    private route: ActivatedRoute,
    fb: FormBuilder
  ) {
    this.filterForm = fb.group({
      from: [''],
      to: [''],
      type: ['ALL'],
      status: ['ALL']
    });
  }

  ngOnInit(): void {
    const sub = this.route.paramMap.subscribe(pm => {
      const idParam = pm.get('accountId');
      const id = idParam ? Number(idParam) : null;
      this.accountId.set(id);
      if (id) this.loadAllTransactions();
    });
    this.subs.push(sub);
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  private setLoading(val: boolean) {
    this.loading.set(val);
  }

  loadAllTransactions() {
  const id = this.accountId();
  if (!id) {
    this.error.set('Missing account id');
    return;
  }

  this.setLoading(true);
  this.error.set(null);

  const sub = this.transactionService.getRecentTransactions(id, 1000).subscribe({
    next: list => {
      list.sort(
        (a, b) =>
          new Date(b.transactionDateandTime).getTime() -
          new Date(a.transactionDateandTime).getTime()
      );
      this.allTransactions.set(list);
      this.transactions.set(list);
      this.currentPage.set(1);
      this.setLoading(false);
    },
    error: () => {
      this.setLoading(false);
      this.error.set('Failed to load transactions');
    }
  });

  this.subs.push(sub);
}

// Apply filters (type, status, date)
applyFilter() {
  const { from, to, type, status } = this.filterForm.value;
  let filtered = [...this.allTransactions()];

  if (from && to) {
    const fromDate = new Date(from);
    const toDate = new Date(to);
    filtered = filtered.filter(
      tx =>
        new Date(tx.transactionDateandTime) >= fromDate &&
        new Date(tx.transactionDateandTime) <= toDate
    );
  }

  if (type && type !== 'ALL') filtered = filtered.filter(tx => tx.transactiontype === type);
  if (status && status !== 'ALL') filtered = filtered.filter(tx => tx.transactionstatus === status);

  this.transactions.set(filtered);
  this.currentPage.set(1); // reset to first page
}

// Filtered by search term
filteredTransactions = computed(() => {
  const term = this.searchTerm().trim().toLowerCase();
  if (!term) return this.transactions();
  return this.transactions().filter(tx => {
    const idMatch = tx.transactionId.toString() === term;
    const remarkMatch = (tx.transactionRemarks || '').toLowerCase().includes(term);
    return idMatch || remarkMatch;
  });
});

// Pagination
pagedTransactions = computed(() => {
  const size = Number(this.pageSize());
  const start = (this.currentPage() - 1) * size;
  return this.filteredTransactions().slice(start, start + size);
});

// Page navigation
goToPage(page: number) {
  if (page >= 1 && page <= this.totalPages()) this.currentPage.set(page);
}
nextPage() { this.goToPage(this.currentPage() + 1); }
prevPage() { this.goToPage(this.currentPage() - 1); }

// Change page size globally
changePageSize(size: number) {
  this.pageSize.set(Number(size));
  this.currentPage.set(1); // always start from first page
}

// Clear filters
clearFilters() {
  this.filterForm.reset({ from: '', to: '', type: 'ALL', status: 'ALL' });
  this.searchTerm.set('');
  this.transactions.set(this.allTransactions());
  this.currentPage.set(1);
}

showToast(message: string, type: 'error' | 'info' = 'error', duration = 5000) {
    const id = ++this.toastCounter;
    this.toasts.update(t => [...t, { id, message, type }]);
    setTimeout(() => this.removeToast(id), duration);
  }

  removeToast(id: number) {
    this.toasts.update(t => t.filter(x => x.id !== id));
  }
}