import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import {provideHttpClient} from '@angular/common/http';
import {providePrimeNG} from 'primeng/config';
import Aura from '@primeuix/themes/aura';
import { routes } from './app.routes';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { definePreset } from '@primeuix/themes';
import { ConfirmationService, MessageService } from 'primeng/api';


const MiniFacturePreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{sky.50}', 100: '{sky.100}', 200: '{sky.200}',
      300: '{sky.300}', 400: '{sky.400}', 500: '{sky.500}',
      600: '{sky.600}', 700: '{sky.700}', 800: '{sky.800}',
      900: '{sky.900}', 950: '{sky.950}'
    }
  }
});

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: MiniFacturePreset
      }
    }),
    ConfirmationService,
    MessageService,
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient()]
};
