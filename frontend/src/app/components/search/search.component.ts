import { Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';

@Component({
  selector: 'app-search',
  imports: [FormsModule, InputTextModule, IconFieldModule, InputIconModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  placeholder = input('Rechercher...');
  searched = output<string>();

  value = signal('');

  onChange(value: string): void {
    this.value.set(value);
    this.searched.emit(value);
  }
}