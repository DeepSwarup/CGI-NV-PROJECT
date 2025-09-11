import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Profile, ProfileInfo } from '../../services/profile/profile';
import { Auth } from '../../services/auth/auth';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; // Import the Router

@Component({
  selector: 'app-customer-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-profile.html',
  styleUrl: './customer-profile.css'
})
export class CustomerProfile implements OnInit{

  profileForm: FormGroup;
  profile: ProfileInfo | null = null;
  isEditMode = false;
  isLoading = true;
  role: 'CUSTOMER' | 'ADMIN' | null = null;
  
  // Inject services
  private fb = inject(FormBuilder);
  private profileService = inject(Profile);
  private authService = inject(Auth);
  private router = inject(Router); // Inject the Router

  constructor() {
    this.profileForm = this.fb.group({
      phoneNo: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      age: ['', [Validators.required, Validators.min(18), Validators.max(120)]],
      gender: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.role = user?.role || null;
    });

    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.isLoading = false;
        if (profile) {
          this.profileForm.patchValue(profile);
        }
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  onSubmit() {
    if (this.profileForm.invalid) return;

    // Determine if we are creating a new profile BEFORE the API call
    const isCreatingNewProfile = !this.profile;

    const data = this.profileForm.value as ProfileInfo;
    const action = isCreatingNewProfile 
      ? this.profileService.createProfile(data) 
      : this.profileService.updateProfile(data);

    action.subscribe({
      next: () => {
        // After success, if we were creating a new profile, redirect.
        if (isCreatingNewProfile) {
          alert('Profile created successfully! Redirecting to your dashboard.');
          this.router.navigate(['/dashboard']); // <-- THIS IS THE NEW LINE
        } else {
          // If just updating, update local state
          this.profile = data;
          this.isEditMode = false;
          alert('Profile updated successfully!');
        }
      },
      error: (e) => {
        console.error("Could not save profile: " + e.message);
        alert("Error: Could not save profile.");
      }
    });
  }

  startEdit() {
    this.isEditMode = true;
  }
  
  cancelEdit() {
    this.isEditMode = false;
    if (this.profile) {
      this.profileForm.patchValue(this.profile);
    }
  }

  onDelete() {
    if (confirm('Are you sure you want to delete your profile?')) {
      this.profileService.deleteProfile().subscribe({
        next: () => {
          this.profile = null;
          this.profileForm.reset();
          alert('Profile deleted.');
        },
        error: (e) => alert("Delete Failed: " + e.message)
      });
    }
  }
}
