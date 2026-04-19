import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-my-reviews',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">My Reviews</h1>
        <div class="bg-white rounded-lg shadow p-12 text-center">
          <p class="text-dark-500">You haven't written any reviews yet.</p>
          <a routerLink="/products" class="text-primary-600 hover:text-primary-700 mt-2 inline-block">Browse products</a>
        </div>
      </div>
    </div>
  `
})
export class MyReviewsComponent implements OnInit {
  ngOnInit(): void {}
}