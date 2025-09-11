import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Profile, ProfileInfo } from '../../services/profile/profile';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-profile.html',
  styleUrls: ['./customer-profile.css']
})
export class CustomerProfile implements OnInit {
  private fb = inject(FormBuilder);
  private profileService = inject(Profile);
  private router = inject(Router);

  profileForm: FormGroup;
  profile = signal<ProfileInfo | null>(null);
  isEditMode = signal(false);
  isLoading = signal(true);
  showDeleteModal = signal(false);

  constructor() {
    this.profileForm = this.fb.group({
      phoneNo: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      age: ['', [Validators.required, Validators.min(18), Validators.max(120)]],
      gender: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile() {
    this.isLoading.set(true);
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.profileForm.patchValue(profile);
        this.isEditMode.set(false); // Start in view mode
        this.isLoading.set(false);
      },
      error: () => {
        this.profile.set(null);
        this.isEditMode.set(true); // No profile exists, start in edit/create mode
        this.isLoading.set(false);
      }
    });
  }

  onSubmit() {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    
    const data = this.profileForm.value as ProfileInfo;
    const action = this.profile()
      ? this.profileService.updateProfile(data)
      : this.profileService.createProfile(data);

    action.subscribe({
      next: () => {
        const wasCreating = !this.profile();
        this.profile.set(data);
        this.isEditMode.set(false);
        if (wasCreating) {
          // If they just created their profile, send them to the dashboard
          this.router.navigate(['/dashboard']);
        }
      },
      error: (e) => alert(`Failed to save profile: ${e.message}`)
    });
  }

  onDelete() {
    this.profileService.deleteProfile().subscribe({
      next: () => {
        this.profile.set(null);
        this.profileForm.reset();
        this.isEditMode.set(true); // Go back to create mode
        this.showDeleteModal.set(false);
      },
      error: (e) => alert(`Delete failed: ${e.message}`)
    });
  }

  startEdit() {
    this.isEditMode.set(true);
  }

  cancelEdit() {
    // If a profile exists, reset the form to its values and exit edit mode
    if (this.profile()) {
      this.profileForm.patchValue(this.profile()!);
      this.isEditMode.set(false);
    }
  }
}

