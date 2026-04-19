import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">Manage Users</h1>
        <div class="bg-white rounded-lg shadow p-12 text-center">
          <p class="text-dark-500">No users to display.</p>
        </div>
      </div>
    </div>
  `
})
export class ManageUsersComponent implements OnInit {
  ngOnInit(): void {}
}