import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Profile } from '../../services/profile/profile';
import { CommonModule } from '@angular/common';

export interface customeralldetails{
customerId: number;
phoneNo: string;
age: number;
gender: 'MALE' | 'FEMALE' | 'OTHER';
userName: string;
userEmail: string;
}

@Component({
  selector: 'app-customer-details',
  imports: [CommonModule],
  templateUrl: './customer-details.html',
  styleUrl: './customer-details.css'
})
export class CustomerDetails implements OnInit{

  customerId: number | null = null
  customer :customeralldetails | null = null

  private route = inject(ActivatedRoute);
  private profileService = inject(Profile);

  ngOnInit(): void {
      this.customerId = +this.route.snapshot.paramMap.get('id')!

      this.profileService.getCustomerById(this.customerId).subscribe({
        next: (c)=> this.customer = c,
        error: (e)=> console.log('Failed to load customers', e.message)
      })
  }


}
