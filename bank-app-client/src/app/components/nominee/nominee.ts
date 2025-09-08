import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Account } from '../../models/account';
import { Nominee } from '../../models/nominee';
import { NomineeService } from '../../services/nominee/nominee';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-nominee',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './nominee.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NomineeComponent {
  private fb = inject(FormBuilder);
  private nomineeService = inject(NomineeService);

  // UI state signals
  accounts = signal<Account[]>([]);
  selectedAccount = signal<Account | null>(null);
  showNomineeModal = signal(false);
  showFindNomineeModal = signal(false);
  loadingAccounts = signal(false);
  loadingFindNominee = signal(false);
  isEditMode = signal(false);
  nomineeError = signal<string | null>(null);
  searched = signal(false);
  foundNominee = signal<Nominee | null>(null);
  linkedAccount = signal<Account | null>(null);
  showDropdown = signal<number | null>(null);

  nomineeForm!: FormGroup;
  findNomineeForm!: FormGroup;

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
      phoneNo: ['', [Validators.required]],
      govtIdType: ['', [Validators.required]],
      govtId: ['', [Validators.required]]
    });

    this.findNomineeForm = this.fb.group({
      nomineeId: ['', [Validators.required]]
    });
  }


  fetchAccounts(): void {
    this.loadingAccounts.set(true);

    this.nomineeService.fetchAccountsWithNominees().subscribe({
      next: (accountsWithNominees: Account[]) => {
        this.accounts.set(accountsWithNominees);
        this.loadingAccounts.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch customer, accounts, or nominees:', err);
        this.loadingAccounts.set(false);
      }
    });
  }


  openAddNomineeModal(accountId: number = 0): void {
    this.isEditMode.set(false);
    this.nomineeForm.patchValue({
      nomineeId: 0,
      name: '',
      relation: '',
      phoneNo: '',
      govtIdType: '',
      govtId: '',
      accountId: accountId > 0 ? accountId : 0
    });
    this.nomineeForm.markAsUntouched();
    this.nomineeForm.markAsPristine();
    this.nomineeError.set(null);
    this.showNomineeModal.set(true);
    console.log('Add modal opened with account ID:', accountId);
  }


  onEditNomineeClick(nominee: Nominee): void {
    this.isEditMode.set(true);
    this.nomineeForm.patchValue({
      nomineeId: nominee.nomineeId,
      name: nominee.name,
      govtId: nominee.govtId,
      govtIdType: nominee.govtIdType,
      phoneNo: nominee.phoneNo,
      relation: nominee.relation,
      accountId: nominee.accountId
    });
    this.nomineeForm.markAsUntouched();
    this.nomineeForm.markAsPristine();
    this.nomineeError.set(null);
    this.showNomineeModal.set(true);
  }


  closeModal(): void {
    this.showNomineeModal.set(false);
    this.showFindNomineeModal.set(false);
    this.findNomineeForm.reset();
    this.foundNominee.set(null);
    this.linkedAccount.set(null);
    this.searched.set(false);
    this.nomineeError.set(null);
  }


  saveNominee(): void {
    const formValue = this.nomineeForm.value;
    console.log('Save nominee clicked, form value:', formValue);

    const validationError = this.nomineeService.validateNomineeData(formValue);
    if (validationError) {
      this.nomineeError.set(validationError);
      return;
    }

    const selectedAccount = this.nomineeService.findAccountById(formValue.accountId, this.accounts());

    if (!selectedAccount) {
      this.nomineeError.set('Selected account not found.');
      return;
    }

    selectedAccount.nominees = selectedAccount.nominees || [];

    if (!this.isEditMode() && !this.nomineeService.canAddNomineeToAccount(selectedAccount)) {
      this.nomineeError.set(`Maximum ${environment.validation.maxNomineesPerAccount} nominees allowed per account`);
      return;
    }

    this.nomineeError.set(null);
    console.log('All validations passed, proceeding with save');

    if (this.isEditMode()) {
      // Update existing nominee
      this.nomineeService.updateNominee(formValue.nomineeId, formValue).subscribe({
        next: (response) => {
          console.log('Nominee updated successfully', response);
          this.fetchAccounts(); // refresh accounts and nominees
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to update nominee', err);
          this.nomineeError.set('Failed to update nominee. Please try again.');
        }
      });
    } else {
      // Create new nominee
      this.nomineeService.createNominee(formValue).subscribe({
        next: (response) => {
          console.log('Nominee created successfully', response);
          this.fetchAccounts(); // refresh accounts and nominees
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create nominee', err);
          this.nomineeError.set('Failed to create nominee. Please try again.');
        }
      });
    }
  }


  onDeleteNomineeClick(nomineeId: number): void {
    if (confirm('Are you sure you want to delete this nominee?')) {
      this.nomineeService.deleteNominee(nomineeId).subscribe({
        next: (response) => {
          console.log('Nominee deleted successfully', response);
          this.fetchAccounts();
        },
        error: (err) => {
          if (err.status === 200) {
            console.log('Nominee deleted successfully (status 200)');
            this.fetchAccounts();
          } else {
            console.error('Failed to delete nominee', err);
          }
        }
      });
    }
  }


  openFindNomineeModal(): void {
    this.showFindNomineeModal.set(true);
    this.searched.set(false);
    this.findNomineeForm.reset();
  }


  findNomineeById(): void {
    if (this.findNomineeForm.invalid) {
      this.findNomineeForm.markAllAsTouched();
      return;
    }

    const nomineeId = this.findNomineeForm.get('nomineeId')?.value;
    console.log('Searching for nominee ID:', nomineeId);
    if (!nomineeId) return;

    this.loadingFindNominee.set(true);
    this.searched.set(false);
    this.foundNominee.set(null);
    this.linkedAccount.set(null);

    const searchResult = this.nomineeService.findNomineeInAccounts(nomineeId, this.accounts());

    setTimeout(() => {
      if (searchResult.nominee) {
        console.log('Nominee found in customer accounts:', searchResult.nominee);
        this.foundNominee.set(searchResult.nominee);
        this.linkedAccount.set(searchResult.account);
      } else {
        console.log('Nominee not found in customer accounts');
        this.foundNominee.set(null);
        this.linkedAccount.set(null);
      }

      this.searched.set(true);
      this.loadingFindNominee.set(false);
    }, environment.ui.searchDelay);
  }

  onShowNomineesClick(account: Account): void {
    this.selectedAccount.set(this.selectedAccount()?.accountId === account.accountId ? null : account);
  }


  onHideNomineesClick(): void {
    this.selectedAccount.set(null);
    this.showDropdown.set(null);
  }


  toggleDropdown(accountId: number): void {
    this.showDropdown.set(this.showDropdown() === accountId ? null : accountId);
  }

  isFieldInvalid(fieldName: string, form: FormGroup = this.nomineeForm): boolean {
    const field = form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string, form: FormGroup = this.nomineeForm): string {
    const field = form.get(fieldName);
    if (!field || !field.errors) return '';

    const errors = field.errors;

    if (errors['required']) return `${fieldName} is required`;
    if (errors['min']) return `${fieldName} must be greater than 0`;

    return 'Invalid input';
  }


  getIdPlaceholder(): string {
    const govtIdType = this.nomineeForm.get('govtIdType')?.value;
    return this.nomineeService.getIdPlaceholder(govtIdType);
  }
}
