export interface Client {
  id?: number;
  nom: string;
  email: string;
  adresse?: string;
}

export interface ImportResult {
  imported: number;
  ignored: number;
  errors: string[];
}