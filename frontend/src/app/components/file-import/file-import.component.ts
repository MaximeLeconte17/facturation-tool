import { Component, input, output } from '@angular/core';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-file-import',
  templateUrl: './file-import.component.html',
  imports: [ButtonModule],
  styleUrl: './file-import.component.scss'
})
export class FileImportComponent {
  label = input('Importer un fichier');
  accept = input('.xlsx');
  fileSelected = output<File>();

  onFileChange(inputElement: HTMLInputElement): void {
    const file = inputElement.files?.[0];
    if (file) {
      this.fileSelected.emit(file);
    }
    inputElement.value = '';
  }
}