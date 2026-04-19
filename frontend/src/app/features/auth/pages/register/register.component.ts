import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { UserRole } from '../../../../core/models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-dark-50 to-dark-100 py-12 px-4">
      <div class="max-w-md w-full">
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <h1 class="text-2xl font-bold text-dark-900">Create Account</h1>
          <p class="text-dark-500 mt-2">Join ShopFlow today</p>
        </div>

        <div class="bg-white rounded-2xl shadow-lg p-8">
          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <div class="space-y-5">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label for="prenom" class="block text-sm font-medium text-dark-700 mb-1">First Name</label>
                  <input
                    type="text"
                    id="prenom"
                    formControlName="prenom"
                    class="input-field"
                    placeholder="John"
                  />
                </div>
                <div>
                  <label for="nom" class="block text-sm font-medium text-dark-700 mb-1">Last Name</label>
                  <input
                    type="text"
                    id="nom"
                    formControlName="nom"
                    class="input-field"
                    placeholder="Doe"
                  />
                </div>
              </div>

              <div>
                <label for="email" class="block text-sm font-medium text-dark-700 mb-1">Email</label>
                <input
                  type="email"
                  id="email"
                  formControlName="email"
                  class="input-field"
                  placeholder="you@example.com"
                />
              </div>

              <div>
                <label for="password" class="block text-sm font-medium text-dark-700 mb-1">Password</label>
                <input
                  type="password"
                  id="password"
                  formControlName="password"
                  class="input-field"
                  placeholder="••••••••"
                />
              </div>

              <div>
                <label for="confirmPassword" class="block text-sm font-medium text-dark-700 mb-1">Confirm Password</label>
                <input
                  type="password"
                  id="confirmPassword"
                  formControlName="confirmPassword"
                  class="input-field"
                  placeholder="••••••••"
                />
              </div>

              <div>
                <label for="role" class="block text-sm font-medium text-dark-700 mb-1">Account Type</label>
                <select id="role" formControlName="role" class="input-field">
                  <option value="CUSTOMER">Customer</option>
                  <option value="SELLER">Seller</option>
                </select>
              </div>

              <button
                type="submit"
                [disabled]="loading()"
                class="w-full btn-primary py-3 flex items-center justify-center"
              >
                @if (loading()) {
                  <svg class="w-5 h-5 animate-spin mr-2" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                }
                Create Account
              </button>
            </div>
          </form>

          <div class="mt-6 text-center">
            <p class="text-dark-600">
              Already have an account?
              <a routerLink="/auth/login" class="text-primary-600 hover:text-primary-700 font-medium">Sign in</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = signal(false);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      prenom: ['', Validators.required],
      nom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      role: ['CUSTOMER']
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { confirmPassword, ...data } = this.registerForm.value;

    if (data.password !== confirmPassword) {
      this.toastService.error('Passwords do not match');
      return;
    }

    this.loading.set(true);
    this.authService.register(data).subscribe({
      next: () => {
        this.toastService.success('Account created successfully!');
        const user = this.authService.getCurrentUser();
        this.redirectByRole(user?.role);
      },
      error: (err) => {
        this.loading.set(false);
        this.toastService.error(err.error?.message || 'Registration failed. Please try again.');
      }
    });
  }

  private redirectByRole(role?: string): void {
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin']);
        break;
      case 'SELLER':
        this.router.navigate(['/seller']);
        break;
      default:
        this.router.navigate(['/dashboard']);
    }
  }
}