import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Account } from '../../models/account';
import { Nominee } from '../../models/nominee';
import { NomineeService } from '../../services/nominee/nominee';

@Component({
  selector: 'app-nominee',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './nominee.html',
  styleUrls: ['./nominee.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NomineeComponent {
  private fb = inject(FormBuilder);
  private nomineeService = inject(NomineeService);

  accounts = signal<Account[]>([]);
  showNomineeModal = signal(false);
  loadingAccounts = signal(true);
  isEditMode = signal(false);
  nomineeError = signal<string | null>(null);

  nomineeForm!: FormGroup;

  constructor() {
    this.initializeForms();
    this.fetchAccounts();
  }

  private initializeForms(): void {
    this.nomineeForm = this.fb.group({
      nomineeId: [0],
      accountId: [0, [Validators.required, Validators.min(1)]],
      name: ['', [Validators.required]],
      relation: ['', [Validators.required]],
      phoneNo: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      govtIdType: ['', [Validators.required]],
      govtId: ['', [Validators.required]]
    });
  }

  fetchAccounts(): void {
    this.loadingAccounts.set(true);
    this.nomineeService.fetchAccountsWithNominees().subscribe({
      next: (accountsWithNominees) => {
        this.accounts.set(accountsWithNominees);
        this.loadingAccounts.set(false);
      },
      error: () => this.loadingAccounts.set(false)
    });
  }

  openAddNomineeModal(): void {
    this.isEditMode.set(false);
    this.nomineeForm.reset({
      nomineeId: 0,
      accountId: 0,
      relation: '',
      govtIdType: '',
    });
    this.nomineeError.set(null);
    this.showNomineeModal.set(true);
  }

  onEditNomineeClick(nominee: Nominee): void {
    this.isEditMode.set(true);
    this.nomineeForm.patchValue(nominee);
    this.nomineeError.set(null);
    this.showNomineeModal.set(true);
  }

  closeModal(): void {
    this.showNomineeModal.set(false);
  }

  saveNominee(): void {
    if (this.nomineeForm.invalid) {
      this.nomineeForm.markAllAsTouched();
      return;
    }
    const formValue = this.nomineeForm.value;

    const action = this.isEditMode()
      ? this.nomineeService.updateNominee(formValue.nomineeId, formValue)
      : this.nomineeService.createNominee(formValue);

    action.subscribe({
      next: () => {
        this.fetchAccounts();
        this.closeModal();
      },
      error: (err) => this.nomineeError.set(err.error?.message || 'An error occurred.')
    });
  }


  hasAnyNominees(): boolean {
    const list = this.accounts();   // accounts is a signal
    return Array.isArray(list) && list.some(acc => acc.nominees?.length > 0);
  }


  onDeleteNomineeClick(nomineeId: number): void {
    if (confirm('Are you sure you want to delete this nominee?')) {
      this.nomineeService.deleteNominee(nomineeId).subscribe({
        next: () => this.fetchAccounts(),
        error: (err) => alert(err.error?.message || 'Failed to delete nominee.')
      });
    }
  }

  isFieldInvalid = (field: string) => this.nomineeForm.get(field)?.invalid && this.nomineeForm.get(field)?.touched;
}

