import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi  } from '@angular/common/http';
import { HttpErrorInterceptor } from './core/http-error.interceptor';
import { routes } from './app.routes';

// OpenAPI (généré)
import { ApiModule, Configuration } from './api';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
     provideHttpClient(withInterceptorsFromDi()),
    provideRouter(routes),
     // Injection du client OpenAPI avec basePath = environment.apiBaseUrl
    importProvidersFrom(
      ApiModule.forRoot(() => new Configuration({ basePath: environment.apiBaseUrl }))
    )
  ]
};
