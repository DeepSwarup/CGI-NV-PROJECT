import { CommonModule } from '@angular/common';
import { Component, inject , OnInit} from '@angular/core';
import { RouterLink } from '@angular/router';
import { Auth } from '../../services/auth/auth';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

  isAuthenticated = false;
  role: 'CUSTOMER' | 'ADMIN' | null = null

  private authService = inject(Auth)

  ngOnInit(){
    this.authService.currentUser$.subscribe(user=>{
      this.isAuthenticated = !!user
      this.role = user?.role || null
    })
  }

  logout(){
    this.authService.logout()
  }

}
