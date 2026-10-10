export interface FormField {
  name: string;
  label: string;
  type?: 'text' | 'email' | 'number';
  required?: boolean;
}