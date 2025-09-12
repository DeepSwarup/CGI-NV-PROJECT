import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth/auth';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);

  if (authService.isAuthenticated() && authService.getRole() === 'ADMIN') {
    return true;
  }

  // Redirect to home if not an admin
  router.navigate(['/']);
  return false;
};

