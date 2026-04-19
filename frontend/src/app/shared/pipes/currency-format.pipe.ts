import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currencyFormat',
  standalone: true
})
export class CurrencyFormatPipe implements PipeTransform {
  transform(value: number | null | undefined, currency: string = 'TND'): string {
    if (value === null || value === undefined) {
      return '';
    }
    return `${value.toFixed(2)} ${currency}`;
  }
}