import { Component, inject } from '@angular/core';
import { Auth } from '../../services/auth/auth';
import {FormsModule} from '@angular/forms'
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-signup',
  imports: [FormsModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup {

  name = ''
  email = ''
  password = ''
  role : 'CUSTOMER' = 'CUSTOMER'

  // constructor(private authService: Auth){}
  private authService = inject(Auth)

  onSignup(){
    this.authService.signup({name: this.name, email: this.email, password: this.password, role: this.role})
                    .subscribe({
                      next: (res) => console.log("Signup success:", res),
                      error: (err)=> console.log("Signup failed: "+ err.message)
                    })
  }

}
