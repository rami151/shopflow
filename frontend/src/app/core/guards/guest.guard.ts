import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }

  const user = authService.getCurrentUser();
  if (user) {
    switch (user.role) {
      case 'ADMIN':
        router.navigate(['/admin']);
        break;
      case 'SELLER':
        router.navigate(['/seller']);
        break;
      default:
        router.navigate(['/dashboard']);
    }
  }

  return false;
};