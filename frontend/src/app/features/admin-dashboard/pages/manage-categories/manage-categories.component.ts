import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

type Category = {
  id: number;
  nom: string;
  description?: string | null;
  parentId?: number | null;
  parentNom?: string | null;
  actif?: boolean | null;
};

@Component({
  selector: 'app-manage-categories',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-8 gap-4">
          <h1 class="text-2xl font-bold text-dark-900">Manage Categories</h1>
          <button
            class="px-4 py-2 rounded-md bg-dark-900 text-white hover:bg-dark-800 disabled:opacity-50"
            (click)="load()"
            [disabled]="loading()"
          >
            Refresh
          </button>
        </div>

        <div *ngIf="error()" class="mb-6 bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">
          {{ error() }}
        </div>

        <div class="bg-white rounded-lg shadow overflow-hidden">
          <div *ngIf="loading()" class="p-12 text-center text-dark-500">Loading categories…</div>

          <div *ngIf="!loading() && categories().length === 0" class="p-12 text-center">
            <p class="text-dark-500">No categories to display.</p>
          </div>

          <table *ngIf="!loading() && categories().length > 0" class="min-w-full divide-y divide-dark-100">
            <thead class="bg-dark-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">Name</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">Parent</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">Description</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">Status</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-dark-100">
              <tr *ngFor="let c of categoriesSorted()">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-dark-900">{{ c.nom }}</div>
                  <div class="text-xs text-dark-500">#{{ c.id }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-dark-700">
                  <span *ngIf="c.parentNom; else noParent">{{ c.parentNom }}</span>
                  <ng-template #noParent>—</ng-template>
                </td>
                <td class="px-6 py-4 text-sm text-dark-700">
                  <span *ngIf="c.description && c.description.length > 0; else noDesc">{{ c.description }}</span>
                  <ng-template #noDesc>—</ng-template>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  <span
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    [class.bg-green-100]="c.actif !== false"
                    [class.text-green-800]="c.actif !== false"
                    [class.bg-red-100]="c.actif === false"
                    [class.text-red-800]="c.actif === false"
                  >
                    {{ c.actif === false ? 'Inactive' : 'Active' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class ManageCategoriesComponent implements OnInit {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  categories = signal<Category[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  categoriesSorted = computed(() => {
    const list = [...this.categories()];
    list.sort((a, b) => a.nom.localeCompare(b.nom));
    return list;
  });

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<Category[]>(`${this.apiUrl}/categories`).subscribe({
      next: (data) => {
        this.categories.set(Array.isArray(data) ? data : []);
        this.loading.set(false);
      },
      error: (err) => {
        const msg =
          err?.error?.message ||
          err?.message ||
          'Failed to load categories. Please try again.';
        this.error.set(msg);
        this.categories.set([]);
        this.loading.set(false);
      }
    });
  }
}