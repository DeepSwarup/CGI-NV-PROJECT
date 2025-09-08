import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Signup } from './components/signup/signup';
import { Login } from './components/login/login';
import { Profile } from './components/profile/profile';
import { authGuard } from './guard/auth-guard';
import { Dashboard } from './components/dashboard/dashboard';
import { CustomerDetails } from './components/customer-details/customer-details';
import { NomineeComponent } from './components/nominee/nominee';
import { BeneficiaryComponent } from './components/beneficiary/beneficiary';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'signup', component: Signup },
  { path: 'login', component: Login },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'customer/:id', component: CustomerDetails, canActivate: [authGuard] },
  { path: 'nominee', component: NomineeComponent },
  { path: 'beneficiary', component: BeneficiaryComponent },

  { path: '**', redirectTo: '' },
];
