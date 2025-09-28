import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { MovementsApi, MovementListItemDto, WarehouseDto  } from '../api';

@Component({
  selector: 'app-history',
  imports: [CommonModule],
  templateUrl: './history.html',
  styleUrl: './history.scss'
})
export class History implements OnInit {
  movements = signal<MovementListItemDto[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(private movementService: MovementsApi) {}

  ngOnInit(): void {
    this.fetchMovements();
  }

  fetchMovements(): void {
    this.loading.set(true);
    this.error.set(null);

    this.movementService
      .listMovements(50 /* limit */)
      .subscribe({
        next: (data) => {
          this.movements.set(data ?? []);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('[History] listMovements failed', err);
          this.error.set('Impossible de charger les mouvements');
          this.loading.set(false);
        },
      });
  }
// 👉 Helpers pour afficher "De" et "Vers"
  private formatWarehouse(w?: WarehouseDto | null): string {
    return w ? `${w.code} — ${w.label}` : '—';
  }

  fromOf(m: MovementListItemDto): string {
    return m.movementType === 'IN'
      ? this.formatWarehouse(m.fromWarehouse ?? null)
      : this.formatWarehouse(m.declaredIn);
  }

  toOf(m: MovementListItemDto): string {
    return m.movementType === 'IN'
      ? this.formatWarehouse(m.declaredIn)
      : this.formatWarehouse(m.toWarehouse ?? null);
  }
}