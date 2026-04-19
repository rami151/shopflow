import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <footer class="bg-dark-900 text-white mt-auto">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <div class="flex items-center gap-2 mb-4">
              <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-700 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold">ShopFlow</span>
            </div>
            <p class="text-dark-400 text-sm">Your trusted online shopping destination for quality products.</p>
          </div>

          <div>
            <h3 class="text-sm font-semibold mb-4">Shop</h3>
            <ul class="space-y-2">
              <li><a routerLink="/products" class="text-dark-400 hover:text-white text-sm transition">All Products</a></li>
              <li><a routerLink="/categories" class="text-dark-400 hover:text-white text-sm transition">Categories</a></li>
              <li><a href="#" class="text-dark-400 hover:text-white text-sm transition">Featured</a></li>
            </ul>
          </div>

          <div>
            <h3 class="text-sm font-semibold mb-4">Account</h3>
            <ul class="space-y-2">
              <li><a routerLink="/dashboard" class="text-dark-400 hover:text-white text-sm transition">My Dashboard</a></li>
              <li><a routerLink="/dashboard/orders" class="text-dark-400 hover:text-white text-sm transition">My Orders</a></li>
              <li><a href="#" class="text-dark-400 hover:text-white text-sm transition">Settings</a></li>
            </ul>
          </div>

          <div>
            <h3 class="text-sm font-semibold mb-4">Contact</h3>
            <ul class="space-y-2">
              <li class="text-dark-400 text-sm">support&#64;shopflow.com</li>
              <li class="text-dark-400 text-sm">+216 00 000 000</li>
              <li class="text-dark-400 text-sm">Tunis, Tunisia</li>
            </ul>
          </div>
        </div>

        <div class="border-t border-dark-800 mt-8 pt-8 flex flex-col md:flex-row items-center justify-between gap-4">
          <p class="text-dark-400 text-sm">&copy; 2026 ShopFlow. All rights reserved.</p>
          <div class="flex items-center gap-4">
            <a href="#" class="text-dark-400 hover:text-white transition">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"/></svg>
            </a>
            <a href="#" class="text-dark-400 hover:text-white transition">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2.163c3.204 0 3.584.012 4.85.057 1.17.055 1.805.249 2.227.415.56.22.924.901 1.263 1.55.338.5.562.897.812 1.445.249-.548.449-1.098.812-1.447.339-.65.703-1.331 1.263-1.55.422-.166 1.057-.36 2.227-.415 1.266-.045 1.646-.057 4.85-.057zm0 2.214c-3.204 0-3.587.012-4.849.057-1.17.055-1.805.249-2.227.415-.56.22-.924.901-1.263 1.55-.338.5-.562.897-.812 1.445-.249-.548-.449-1.098-.812-1.447-.339-.65-.703-1.331-1.263-1.55-.422-.166-1.057-.36-2.227-.415-1.262-.045-1.646-.057-4.849-.057zm-6.804 4.392c.907 0 1.627-.293 2.155-.879.528-.586.792-1.361.792-2.326 0-.948-.264-1.717-.792-2.306-.528-.589-1.248-.883-2.155-.883-.93 0-1.67.294-2.218.883-.548.589-.822 1.358-.822 2.306 0 .965.274 1.74.822 2.326.548.586 1.288.879 2.218.879zm6.804 0c.907 0 1.627-.293 2.155-.879.528-.586.792-1.361.792-2.326 0-.948-.264-1.717-.792-2.306-.528-.589-1.248-.883-2.155-.883-.93 0-1.67.294-2.218.883-.548.589-.822 1.358-.822 2.306 0 .965.274 1.74.822 2.326.548.586 1.288.879 2.218.879zm-10.69 4.573c1.506 0 2.729-.49 3.667-1.468.939-.979 1.408-2.173 1.408-3.583 0-1.49-.469-2.699-1.408-3.629-.938-.93-2.161-1.395-3.667-1.395-1.564 0-2.835.465-3.814 1.395-.979.93-1.468 2.139-1.468 3.629 0 1.41.489 2.604 1.468 3.583.979.978 2.25 1.468 3.814 1.468z"/></svg>
            </a>
            <a href="#" class="text-dark-400 hover:text-white transition">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M22.46 6c-.85.38-1.78.64-2.75.76 1-.6 1.76-1.55 2.12-2.68-.93.55-1.96.95-3.06 1.17-.88-.94-2.13-1.53-3.51-1.53-2.66 0-4.81 2.16-4.81 4.81 0 .38.04.75.13 1.1-4-.2-7.58-2.11-9.96-5.02-.95.63-2.45 1.83-3.84 2.37-.37.06-.77.09-1.18.09-.33 0-.65-.03-.96-.09.35 1.08 1.27 1.98 2.4 2.22-.32.08-.66.12-1.01.12-.25 0-.49-.02-.73-.06.49 1.53 1.91 2.65 3.59 2.98-1.4 1.1-3.17 1.75-5.08 1.75-.33 0-.66-.02-.98-.06 1.82 1.17 3.97 1.85 6.29 1.85 7.54 0 11.67-6.25 11.67-11.67 0-.18-.01-.35-.01-.53.8-.58 1.49-1.3 2.04-2.13z"/></svg>
            </a>
          </div>
        </div>
      </div>
    </footer>
  `,
  styles: []
})
export class FooterComponent {}