import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-rating-stars',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex items-center gap-0.5">
      @for (star of stars; track $index) {
        <svg
          class="w-4 h-4"
          [ngClass]="star <= rating ? 'text-yellow-400' : 'text-dark-200'"
          fill="currentColor"
          viewBox="0 0 20 20"
        >
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
      }
      @if (showCount && reviewCount !== undefined) {
        <span class="ml-1 text-sm text-dark-500">({{ reviewCount }})</span>
      }
    </div>
  `,
  styles: []
})
export class RatingStarsComponent {
  @Input() rating = 0;
  @Input() reviewCount?: number;
  @Input() showCount = false;

  stars = [1, 2, 3, 4, 5];
}