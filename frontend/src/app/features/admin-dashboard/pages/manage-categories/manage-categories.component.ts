import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-manage-categories',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">Manage Categories</h1>
        <div class="bg-white rounded-lg shadow p-12 text-center">
          <p class="text-dark-500">No categories to display.</p>
        </div>
      </div>
    </div>
  `
})
export class ManageCategoriesComponent implements OnInit {
  ngOnInit(): void {}
}