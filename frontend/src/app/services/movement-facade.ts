import { Injectable, signal } from '@angular/core';
import {
  MovementsApi,
  MovementCreateRequestDto,
  MovementResponseDto,
  ProblemDto
} from '../api';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, finalize, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { problemToMessage } from '@shared/utils/helper.utils';


@Injectable({
  providedIn: 'root'
})
export class MovementFacade {
  // état global réutilisable par Entry/Exit
  readonly isLoading = signal(false);
  readonly apiErrorMessage = signal<string | null>(null);
  readonly lastResponse = signal<MovementResponseDto | null>(null);

  constructor(private api: MovementsApi) {}

  createMovement(payload: MovementCreateRequestDto) {
    this.isLoading.set(true);
    this.apiErrorMessage.set(null);
    this.lastResponse.set(null);

    return this.api.createMovement(payload).pipe(
      tap((res) => {
        this.lastResponse.set(res);
        // console.log('[Movement] created:', JSON.stringify(res, null, 2));
      }),
      catchError((err: HttpErrorResponse) => {
        const p = (err?.error ?? null) as ProblemDto | any;
        this.apiErrorMessage.set(problemToMessage(p));
        return of(null);
      }),
      finalize(() => this.isLoading.set(false))
    );
  }
}
