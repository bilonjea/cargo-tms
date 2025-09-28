import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-movement-time-picker',
  imports: [],
  templateUrl: './movement-time-picker.html',
  styleUrl: './movement-time-picker.scss',
   providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => MovementTimePicker),
    multi: true
  }]
})
export class MovementTimePicker implements ControlValueAccessor {
  @Input() required = false;
  @Input() min?: string; // '2025-09-27T00:00'
  @Input() max?: string;

  // valeur affichée dans l'input (locale, sans 'Z')
  localValue: string | null = null;

  private onChange: (val: string | null) => void = () => {};
  private onTouched: () => void = () => {};
  
  markTouched() {
    this.onTouched();
  }

  writeValue(isoUtc: string | null): void {
    this.localValue = isoUtc ? this.isoUtcToLocalInput(isoUtc) : null;
  }
  registerOnChange(fn: any): void { this.onChange = fn; }
  registerOnTouched(fn: any): void { this.onTouched = fn; }
  setDisabledState(isDisabled: boolean): void { /* à implémenter si besoin */ }

  onInputChange(v: string) {
    this.localValue = v || null;
    const iso = v ? this.localInputToIsoUtc(v) : null;
    this.onChange(iso);
  }

  // "YYYY-MM-DDTHH:mm" (local) -> ISO UTC "…Z"
  private localInputToIsoUtc(local: string): string {
    const d = new Date(local);           // interprété en local
    return new Date(
      Date.UTC(d.getFullYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), 0, 0)
    ).toISOString();
  }

  // ISO UTC "…Z" -> "YYYY-MM-DDTHH:mm" local
  private isoUtcToLocalInput(iso: string): string {
    const d = new Date(iso);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }
}
