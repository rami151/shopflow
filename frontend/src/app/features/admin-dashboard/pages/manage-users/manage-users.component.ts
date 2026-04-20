import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

type AdminUser = {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: 'ADMIN' | 'SELLER' | 'CUSTOMER';
  actif: boolean;
  dateCreation: string;
};

type UpdateUserAdminRequest = {
  nom?: string;
  prenom?: string;
  role?: AdminUser['role'];
  actif?: boolean;
};

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-8 gap-4">
          <h1 class="text-2xl font-bold text-dark-900">Manage Users</h1>
          <button
            class="px-4 py-2 rounded-md bg-dark-900 text-white hover:bg-dark-800 disabled:opacity-50"
            (click)="load()"
            [disabled]="loading()"
          >
            Refresh
          </button>
        </div>

        <div class="mb-6 grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            class="w-full px-4 py-2 rounded-lg border border-dark-200 focus:outline-none focus:ring-2 focus:ring-dark-900"
            placeholder="Search by name or email…"
            [(ngModel)]="query"
          />
          <select
            class="w-full px-4 py-2 rounded-lg border border-dark-200 focus:outline-none focus:ring-2 focus:ring-dark-900"
            [(ngModel)]="roleFilter"
          >
            <option value="">All roles</option>
            <option value="ADMIN">ADMIN</option>
            <option value="SELLER">SELLER</option>
            <option value="CUSTOMER">CUSTOMER</option>
          </select>
          <select
            class="w-full px-4 py-2 rounded-lg border border-dark-200 focus:outline-none focus:ring-2 focus:ring-dark-900"
            [(ngModel)]="statusFilter"
          >
            <option value="">All statuses</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
        </div>

        <div *ngIf="error()" class="mb-6 bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">
          {{ error() }}
        </div>

        <div class="bg-white rounded-lg shadow overflow-hidden">
          <div *ngIf="loading()" class="p-12 text-center text-dark-500">Loading users…</div>

          <div *ngIf="!loading() && filteredUsers().length === 0" class="p-12 text-center">
            <p class="text-dark-500">No users to display.</p>
          </div>

          <table *ngIf="!loading() && filteredUsers().length > 0" class="min-w-full divide-y divide-dark-100">
            <thead class="bg-dark-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">User</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">Role</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-dark-500 uppercase tracking-wider">Active</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-dark-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-dark-100">
              <tr *ngFor="let u of filteredUsers()">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-dark-900">{{ u.prenom }} {{ u.nom }}</div>
                  <div class="text-xs text-dark-500">{{ u.email }}</div>
                </td>

                <td class="px-6 py-4 whitespace-nowrap">
                  <select
                    class="px-3 py-2 rounded-md border border-dark-200 focus:outline-none focus:ring-2 focus:ring-dark-900"
                    [(ngModel)]="edit[u.id].role"
                  >
                    <option value="ADMIN">ADMIN</option>
                    <option value="SELLER">SELLER</option>
                    <option value="CUSTOMER">CUSTOMER</option>
                  </select>
                </td>

                <td class="px-6 py-4 whitespace-nowrap">
                  <label class="inline-flex items-center gap-2 text-sm text-dark-700">
                    <input type="checkbox" [(ngModel)]="edit[u.id].actif" />
                    <span>{{ edit[u.id].actif ? 'Active' : 'Inactive' }}</span>
                  </label>
                </td>

                <td class="px-6 py-4 whitespace-nowrap text-right">
                  <button
                    class="px-3 py-2 rounded-md bg-dark-900 text-white hover:bg-dark-800 disabled:opacity-50"
                    (click)="save(u)"
                    [disabled]="savingIds().has(u.id)"
                  >
                    Save
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class ManageUsersComponent implements OnInit {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  users = signal<AdminUser[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  savingIds = signal<Set<number>>(new Set());

  query = '';
  roleFilter: '' | AdminUser['role'] = '';
  statusFilter: '' | 'active' | 'inactive' = '';

  edit: Record<number, { role: AdminUser['role']; actif: boolean }> = {};

  filteredUsers = computed(() => {
    const q = this.query.trim().toLowerCase();
    const role = this.roleFilter;
    const status = this.statusFilter;

    return this.users().filter((u) => {
      const matchesQuery =
        q.length === 0 ||
        `${u.prenom} ${u.nom}`.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q);

      const matchesRole = !role || u.role === role;
      const matchesStatus =
        !status || (status === 'active' ? u.actif === true : u.actif === false);

      return matchesQuery && matchesRole && matchesStatus;
    });
  });

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<AdminUser[]>(`${this.apiUrl}/dashboard/admin/users`).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : [];
        this.users.set(list);
        this.edit = Object.fromEntries(
          list.map((u) => [u.id, { role: u.role, actif: u.actif }])
        );
        this.loading.set(false);
      },
      error: (err) => {
        const msg =
          err?.error?.message ||
          err?.message ||
          'Failed to load users. Please try again.';
        this.error.set(msg);
        this.users.set([]);
        this.edit = {};
        this.loading.set(false);
      }
    });
  }

  save(user: AdminUser): void {
    const current = this.edit[user.id];
    if (!current) return;

    const payload: UpdateUserAdminRequest = {
      role: current.role,
      actif: current.actif
    };

    const nextSaving = new Set(this.savingIds());
    nextSaving.add(user.id);
    this.savingIds.set(nextSaving);

    this.http
      .put<AdminUser>(`${this.apiUrl}/dashboard/admin/users/${user.id}`, payload)
      .subscribe({
        next: (updated) => {
          this.users.set(
            this.users().map((u) => (u.id === updated.id ? updated : u))
          );
          this.edit[updated.id] = { role: updated.role, actif: updated.actif };

          const s = new Set(this.savingIds());
          s.delete(user.id);
          this.savingIds.set(s);
        },
        error: (err) => {
          const msg =
            err?.error?.message ||
            err?.message ||
            'Failed to save user changes.';
          this.error.set(msg);

          const s = new Set(this.savingIds());
          s.delete(user.id);
          this.savingIds.set(s);
        }
      });
  }
}