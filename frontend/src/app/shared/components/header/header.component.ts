import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../features/cart/services/cart.service';
import { User, UserRole } from '../../../core/models/user.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <header class="fixed top-0 left-0 right-0 z-50 bg-white border-b border-dark-100 shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center gap-2">
            <a routerLink="/" class="flex items-center gap-2 hover:opacity-80 transition">
              <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold text-dark-900">ShopFlow</span>
            </a>
          </div>

          <nav class="hidden md:flex items-center gap-8">
            <a routerLink="/products" class="text-sm font-medium text-dark-600 hover:text-primary-600 transition">Products</a>
            <a routerLink="/categories" class="text-sm font-medium text-dark-600 hover:text-primary-600 transition">Categories</a>
          </nav>

          <div class="flex items-center gap-4">
            <a routerLink="/products" class="hidden sm:flex items-center gap-2 px-3 py-2 text-sm text-dark-600 bg-dark-50 rounded-lg hover:bg-dark-100 transition">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <span class="hidden md:block">Search</span>
            </a>

            @if (currentUser) {
              <div class="relative">
                <button (click)="toggleMenu()" class="p-2 text-dark-600 hover:text-primary-600 transition">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </button>

                @if (userMenuOpen()) {
                  <div class="absolute right-0 top-12 w-48 bg-white rounded-lg shadow-lg border border-dark-100 overflow-hidden animate-fade-in">
                    <div class="px-4 py-3 border-b border-dark-100">
                      <p class="text-sm font-medium text-dark-900">{{ currentUser.prenom }} {{ currentUser.nom }}</p>
                      <p class="text-xs text-dark-500">{{ currentUser.email }}</p>
                    </div>
                    @if (currentUser.role === 'CUSTOMER') {
                      <a routerLink="/dashboard" class="block px-4 py-3 text-sm font-medium text-dark-700 hover:bg-dark-50 transition">Dashboard</a>
                      <a routerLink="/dashboard/orders" class="block px-4 py-3 text-sm font-medium text-dark-700 hover:bg-dark-50 transition">My Orders</a>
                    }
                    @if (currentUser.role === 'SELLER') {
                      <a routerLink="/seller" class="block px-4 py-3 text-sm font-medium text-dark-700 hover:bg-dark-50 transition">Seller Dashboard</a>
                    }
                    @if (currentUser.role === 'ADMIN') {
                      <a routerLink="/admin" class="block px-4 py-3 text-sm font-medium text-dark-700 hover:bg-dark-50 transition">Admin Dashboard</a>
                    }
                    <button (click)="logout()" class="w-full text-left px-4 py-3 text-sm font-medium text-dark-700 hover:bg-dark-50 transition">Sign Out</button>
                  </div>
                }
              </div>
            } @else {
              <a routerLink="/auth/login" class="hidden md:block text-sm font-medium text-dark-600 hover:text-primary-600 transition">Sign In</a>
              <a routerLink="/auth/register" class="hidden md:block text-sm font-medium text-primary-600 hover:text-primary-700 transition">Create Account</a>
            }

            <a routerLink="/cart" class="relative p-2 text-dark-600 hover:text-primary-600 transition">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              @if (cartCount() > 0) {
                <span class="absolute top-1 right-1 w-5 h-5 bg-accent-500 text-white text-xs font-bold rounded-full flex items-center justify-center">{{ cartCount() }}</span>
              }
            </a>

            <button (click)="toggleMobileMenu()" class="md:hidden p-2 text-dark-600 hover:text-primary-600 transition">
              @if (!mobileMenuOpen()) {
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              } @else {
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              }
            </button>
          </div>
        </div>
      </div>

      @if (mobileMenuOpen()) {
        <div class="md:hidden border-t border-dark-100 bg-white">
          <div class="px-4 py-4 space-y-4">
            <a routerLink="/products" class="block text-sm font-medium text-dark-600 hover:text-primary-600 transition">Products</a>
            <a routerLink="/categories" class="block text-sm font-medium text-dark-600 hover:text-primary-600 transition">Categories</a>
            @if (!currentUser) {
              <div class="border-t border-dark-100 pt-4">
                <a routerLink="/auth/login" class="block text-sm font-medium text-primary-600 hover:text-primary-700 transition">Sign In</a>
                <a routerLink="/auth/register" class="block text-sm font-medium text-dark-600 hover:text-primary-600 transition mt-2">Create Account</a>
              </div>
            }
          </div>
        </div>
      }
    </header>
  `,
  styles: []
})
export class HeaderComponent implements OnInit {
  mobileMenuOpen = signal(false);
  userMenuOpen = signal(false);
  currentUser: User | null = null;
  cartCount = signal(0);

  constructor(
    private authService: AuthService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.cartService.cartItemCount$.subscribe(count => {
      this.cartCount.set(count);
    });

    if (this.authService.isAuthenticated()) {
      this.cartService.loadCart();
    }
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen.update(v => !v);
  }

  toggleMenu(): void {
    this.userMenuOpen.update(v => !v);
  }

  logout(): void {
    this.authService.logout();
  }
}