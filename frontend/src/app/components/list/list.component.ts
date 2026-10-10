import { Component, input, output } from '@angular/core';
import { Client } from '../../client.model';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';


@Component({
  selector: 'app-list',
  imports: [TableModule, ButtonModule],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss',
  standalone: true
})
export class ListComponent<T extends object> {
  datas = input<T[]>([]);
  remove = output<number>();
  open = output<number>();
}
