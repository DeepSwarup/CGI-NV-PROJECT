import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Signup } from './components/signup/signup';
import { Profile } from './components/profile/profile';
import { Dashboard } from './components/dashboard/dashboard';
import { authGuard } from './guard/auth-guard';
import { CustomerProfile } from './components/customer-profile/customer-profile';
import { NomineeComponent } from './components/nominee/nominee';
import { BeneficiaryComponent } from './components/beneficiary/beneficiary';
import { Transactions } from './components/transactions/transactions';
import { adminGuard } from './guard/admin-guard'; 
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard';

import { AccountListComponent } from './components/account-list/account-list'; // New
import { TransactionListComponent } from './components/transaction-list/transaction-list'; // New


export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'signup', component: Signup },
  
  // Protected Routes
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'create-profile', component: CustomerProfile, canActivate: [authGuard] },
    { path: 'accounts', component: AccountListComponent, canActivate: [authGuard] },
  { path: 'transactions', component: TransactionListComponent, canActivate: [authGuard] },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'nominees', component: NomineeComponent, canActivate: [authGuard] },
  { path: 'beneficiaries', component: BeneficiaryComponent, canActivate: [authGuard] },
  { path: 'transactions/account/:accountId', component: Transactions, canActivate: [authGuard] },
  
  { 
    path: 'admin/dashboard', 
    component: AdminDashboardComponent, 
    canActivate: [authGuard, adminGuard] // Protect with both guards
  },
  
  // Redirect any other path to home
  { path: '**', redirectTo: '' }
];