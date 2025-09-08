import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Account } from '../../models/account';
import { Beneficiary } from '../../models/beneficiary';
import { BeneficiaryService } from '../../services/beneficiary/beneficiary';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-beneficiary',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './beneficiary.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BeneficiaryComponent {
  // Injected services
  private fb = inject(FormBuilder);
  private beneficiaryService = inject(BeneficiaryService);

  // UI state signals
  accounts = signal<Account[]>([]);
  selectedAccount = signal<Account | null>(null);
  showBeneficiaryModal = signal(false);
  showFindBeneficiaryModal = signal(false);
  loadingAccounts = signal(false);
  loadingFindBeneficiary = signal(false);
  isEditMode = signal(false);
  beneficiaryError = signal<string | null>(null);
  searched = signal(false);
  foundBeneficiary = signal<Beneficiary | null>(null);
  linkedAccount = signal<Account | null>(null);
  showDropdown = signal<number | null>(null);


  beneficiaryForm!: FormGroup;
  findBeneficiaryForm!: FormGroup;

  constructor() {
    this.initializeForms();
    this.fetchAccounts();
  }


  private initializeForms(): void {
    this.beneficiaryForm = this.fb.group({
      beneficiaryId: [0],
      accountId: [0, [Validators.required, Validators.min(1)]],
      beneficiaryName: ['', [Validators.required]],
      beneficiaryAccNo: [0, [Validators.required, Validators.min(10000000000), Validators.max(99999999999)]],
      ifsc: ['', [Validators.required]],
      accountType: ['', [Validators.required]]
    });


    this.findBeneficiaryForm = this.fb.group({
      beneficiaryId: ['', [Validators.required]]
    });
  }


  fetchAccounts(): void {
    this.loadingAccounts.set(true);

    this.beneficiaryService.fetchAccountsWithBeneficiaries().subscribe({
      next: (accountsWithBeneficiaries: Account[]) => {
        this.accounts.set(accountsWithBeneficiaries);
        this.loadingAccounts.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch customer, accounts, or beneficiaries:', err);
        this.loadingAccounts.set(false);
      }
    });
  }


  openAddBeneficiaryModal(accountId: number = 0): void {
    this.isEditMode.set(false);
    this.beneficiaryForm.patchValue({
      beneficiaryId: 0,
      beneficiaryName: '',
      beneficiaryAccNo: 0,
      ifsc: '',
      accountType: '',
      accountId: accountId > 0 ? accountId : 0
    });
    this.beneficiaryForm.markAsUntouched();
    this.beneficiaryForm.markAsPristine();
    this.beneficiaryError.set(null);
    this.showBeneficiaryModal.set(true);
    console.log('Add modal opened with account ID:', accountId);
  }


  onEditBeneficiaryClick(beneficiary: Beneficiary): void {
    this.isEditMode.set(true);
    this.beneficiaryForm.patchValue({
      beneficiaryId: beneficiary.beneficiaryId,
      beneficiaryName: beneficiary.beneficiaryName,
      beneficiaryAccNo: beneficiary.beneficiaryAccNo,
      ifsc: beneficiary.ifsc,
      accountType: beneficiary.accountType,
      accountId: beneficiary.accountId
    });
    this.beneficiaryForm.markAsUntouched();
    this.beneficiaryForm.markAsPristine();
    this.beneficiaryError.set(null);
    this.showBeneficiaryModal.set(true);
  }


  closeModal(): void {
    this.showBeneficiaryModal.set(false);
    this.showFindBeneficiaryModal.set(false);
    this.findBeneficiaryForm.reset();
    this.foundBeneficiary.set(null);
    this.linkedAccount.set(null);
    this.searched.set(false);
    this.beneficiaryError.set(null);
  }


  saveBeneficiary(): void {
    const formValue = this.beneficiaryForm.value;
    console.log('Save beneficiary clicked, form value:', formValue);

    // Validate form using service
    const validationError = this.beneficiaryService.validateBeneficiaryData(formValue);
    if (validationError) {
      this.beneficiaryError.set(validationError);
      return;
    }

    // Business logic validation for new beneficiaries
    if (!this.isEditMode()) {
      const selectedAccount = this.beneficiaryService.findAccountById(formValue.accountId, this.accounts());
      if (selectedAccount && !this.beneficiaryService.canAddBeneficiaryToAccount(selectedAccount)) {
        this.beneficiaryError.set(`Maximum ${environment.validation.maxBeneficiariesPerAccount} beneficiaries allowed per account`);
        return;
      }
    }

    this.beneficiaryError.set(null);
    console.log('All validations passed, proceeding with save');

    if (this.isEditMode()) {
      // Update beneficiary
      this.beneficiaryService.updateBeneficiary(formValue.beneficiaryId, formValue).subscribe({
        next: (response) => {
          console.log('Beneficiary updated successfully', response);
          this.fetchAccounts();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to update beneficiary', err);
          this.beneficiaryError.set('Failed to update beneficiary. Please try again.');
        }
      });
    } else {
      // Create beneficiary
      this.beneficiaryService.createBeneficiary(formValue).subscribe({
        next: (response) => {
          console.log('Beneficiary created successfully', response);
          this.fetchAccounts();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create beneficiary', err);
          const backendMessage = err.error || 'Failed to create beneficiary. Please try again.';
          this.beneficiaryError.set(backendMessage);
        }
      });
    }
  }


  onDeleteBeneficiaryClick(beneficiaryId: number): void {
    if (confirm('Are you sure you want to delete this beneficiary?')) {
      this.beneficiaryService.deleteBeneficiary(beneficiaryId).subscribe({
        next: (response) => {
          console.log('Beneficiary deleted successfully', response);
          this.fetchAccounts();
        },
        error: (err) => {
          if (err.status === 200) {
            console.log('Beneficiary deleted successfully (status 200)');
            this.fetchAccounts();
          } else {
            console.error('Failed to delete beneficiary', err);
          }
        }
      });
    }
  }


  openFindBeneficiaryModal(): void {
    this.showFindBeneficiaryModal.set(true);
    this.searched.set(false);
    this.findBeneficiaryForm.reset();
  }

  findBeneficiaryById(): void {
    if (this.findBeneficiaryForm.invalid) {
      this.findBeneficiaryForm.markAllAsTouched();
      return;
    }

    const beneficiaryId = this.findBeneficiaryForm.get('beneficiaryId')?.value;
    console.log('Searching for beneficiary ID:', beneficiaryId);
    if (!beneficiaryId) return;

    this.loadingFindBeneficiary.set(true);
    this.searched.set(false);
    this.foundBeneficiary.set(null);
    this.linkedAccount.set(null);

    const searchResult = this.beneficiaryService.findBeneficiaryInAccounts(beneficiaryId, this.accounts());

    // Simulate search delay
    setTimeout(() => {
      if (searchResult.beneficiary) {
        console.log('Beneficiary found in customer accounts:', searchResult.beneficiary);
        this.foundBeneficiary.set(searchResult.beneficiary);
        this.linkedAccount.set(searchResult.account);
      } else {
        console.log('Beneficiary not found in customer accounts');
        this.foundBeneficiary.set(null);
        this.linkedAccount.set(null);
      }

      this.searched.set(true);
      this.loadingFindBeneficiary.set(false);
    }, environment.ui.searchDelay);
  }


  onShowBeneficiariesClick(account: Account): void {
    this.selectedAccount.set(this.selectedAccount()?.accountId === account.accountId ? null : account);
  }

  onHideBeneficiariesClick(): void {
    this.selectedAccount.set(null);
    this.showDropdown.set(null);
  }

  toggleDropdown(accountId: number): void {
    this.showDropdown.set(this.showDropdown() === accountId ? null : accountId);
  }

  isFieldInvalid(fieldName: string, form: FormGroup = this.beneficiaryForm): boolean {
    const field = form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string, form: FormGroup = this.beneficiaryForm): string {
    const field = form.get(fieldName);
    if (!field || !field.errors) return '';

    const errors = field.errors;

    if (errors['required']) return `${fieldName} is required`;
    if (errors['min'] && fieldName === 'beneficiaryAccNo') return 'Account number must be 11 digits';
    if (errors['max'] && fieldName === 'beneficiaryAccNo') return 'Account number must be 11 digits';
    if (errors['min']) return `${fieldName} must be greater than 0`;

    return 'Invalid input';
  }
}
