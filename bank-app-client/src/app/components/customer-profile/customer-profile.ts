import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Profile, ProfileInfo } from '../../services/profile/profile';
import { Auth } from '../../services/auth/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customer-profile',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-profile.html',
  styleUrl: './customer-profile.css'
})
export class CustomerProfile implements OnInit{

  profileForm: FormGroup
  profile: ProfileInfo | null= null
  isEditMode = false
  isLoading = true
  role : 'CUSTOMER' | 'ADMIN' | null = null

  constructor(
    private fb: FormBuilder,
    private profileService: Profile,
    private authService: Auth
  ){
    this.profileForm = this.fb.group({
      phoneNo: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      age: ['', [Validators.required, Validators.min(18), Validators.max(120)]],
      gender: ['', [Validators.required]]
    })
  }


  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user=>{
      this.role=user?.role || null
    })

    this.profileService.getProfile().subscribe({
      next: (profile)=>{
        this.profile = profile
        this.isLoading = false;
        if(profile){
          this.profileForm.patchValue(profile)
        }
      },
      error: (e) => {
        console.log('profile not found', e.message)
        this.isLoading = false
      }
    })
  }

  onSubmit(){

    console.log(this.profileForm.invalid)
    if(this.profileForm.invalid) return
    
    const data = this.profileForm.value as ProfileInfo
    console.log(data)
    const action = this.profile? this.profileService.updateProfile(data) : this.profileService.createProfile(data);

    action.subscribe({
      next: (updatedProfile) => {
        console.log(updatedProfile)        
        this.profile = data
        this.isEditMode = false
      },
      error: (e) => {
        console.log("can create profile"+ e.message)
      }
    })

  }

  startEdit(){
    this.isEditMode = true
  }
  cancleEdit(){
    this.isEditMode = false
  }

  onDelete(){
    if(confirm('Want to delete your profile??')){
      this.profileService.deleteProfile().subscribe({
        next: ()=>{
          // console.log("before null", this.profile)
          this.profile=null
          this.profileForm.reset();
          // console.log("after null", this.profile)
        },
        error: (e)=>{
          console.log("Delete Failed"+ e.message)
          alert("Delete Failed" +e.message)
        }
      })
    }
  }



}
