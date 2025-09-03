import { Component, inject } from '@angular/core';
import {FormsModule} from '@angular/forms'
import { Auth } from '../../services/auth/auth';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email = ''
  password = ''

  private authService = inject(Auth)

  onLogin(){
    const data = {email: this.email, password: this.password}
    console.log(data)
    this.authService.login(data)
      .subscribe({
        next: (res)=> console.log("Login Successful"),
        error: (e)=> console.log("failed login "+ e.message)
      })
  }
}
