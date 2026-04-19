import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { HeaderComponent } from "./header";

@Component({
  selector: "app-layout",
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  template: `
    <app-header></app-header>
    <main class="pt-16">
      <router-outlet></router-outlet>
    </main>
    <footer class="bg-dark-900 text-white mt-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          <!-- About -->
          <div>
            <h3 class="font-bold text-lg mb-4 flex items-center gap-2">
              <div class="w-6 h-6 bg-gradient-to-br from-primary-400 to-primary-600 rounded flex items-center justify-center">
                <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              ShopFlow
            </h3>
            <p class="text-dark-300 text-sm">Your trusted online marketplace for quality products and seamless shopping experience.</p>
          </div>

          <!-- Quick Links -->
          <div>
            <h4 class="font-semibold mb-4">Quick Links</h4>
            <ul class="space-y-2 text-sm text-dark-300">
              <li><a href="#" class="hover:text-primary-400 transition">About Us</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Categories</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Best Sellers</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">New Arrivals</a></li>
            </ul>
          </div>

          <!-- Customer Service -->
          <div>
            <h4 class="font-semibold mb-4">Support</h4>
            <ul class="space-y-2 text-sm text-dark-300">
              <li><a href="#" class="hover:text-primary-400 transition">Contact Us</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Shipping Info</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Returns</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">FAQ</a></li>
            </ul>
          </div>

          <!-- Legal -->
          <div>
            <h4 class="font-semibold mb-4">Legal</h4>
            <ul class="space-y-2 text-sm text-dark-300">
              <li><a href="#" class="hover:text-primary-400 transition">Privacy Policy</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Terms of Service</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Cookie Policy</a></li>
              <li><a href="#" class="hover:text-primary-400 transition">Accessibility</a></li>
            </ul>
          </div>
        </div>

        <div class="border-t border-dark-700 pt-8">
          <div class="flex flex-col md:flex-row items-center justify-between gap-4">
            <p class="text-dark-400 text-sm">&copy; 2025 ShopFlow. All rights reserved.</p>
            <div class="flex items-center gap-4">
              <a href="#" class="text-dark-300 hover:text-primary-400 transition">
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                </svg>
              </a>
              <a href="#" class="text-dark-300 hover:text-primary-400 transition">
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M23 3a10.9 10.9 0 01-3.14 1.53 4.48 4.48 0 00-7.86 3v1A10.66 10.66 0 013 4s-4 9 5 13a11.64 11.64 0 01-7 2s9 5 20 5a9.5 9.5 0 00-9-5.5c4.75 2.25 7-7 7-7"/>
                </svg>
              </a>
              <a href="#" class="text-dark-300 hover:text-primary-400 transition">
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <rect x="2" y="2" width="20" height="20" rx="5" ry="5" fill="none" stroke="currentColor" stroke-width="2"/>
                  <path d="M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10z" fill="none" stroke="currentColor" stroke-width="2"/>
                  <circle cx="17" cy="7" r="1" fill="currentColor"/>
                </svg>
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  `,
  styles: [],
})
export class LayoutComponent {}
