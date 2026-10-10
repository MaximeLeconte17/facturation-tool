import { Component, input, output } from '@angular/core';
import { Client } from '../../client.model';
import { TableModule } from 'primeng/table';

@Component({
  selector: 'app-list',
  imports: [TableModule],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss',
  standalone: true
})
export class ListComponent<T extends object> {
  datas = input<T[]>([]);
  remove = output<number>();
}
