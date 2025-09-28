import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
// TODO:import { MovementTimePicker } from '../movement-time-picker/movement-time-picker';
import { getRefTypeOptions, getCustomStatusOptions, getCustomDocTypeOptions, DECLARED_IN, KNOWN_WAREHOUSES } from './movement-form.utils';

import { MovementCreateRequestDto, WarehouseDto, MovementTypeDto } from '../api';

@Component({
  selector: 'app-movement-form',
  imports: [CommonModule, ReactiveFormsModule, /*TODO: MovementTimePicker*/],
  templateUrl: './movement-form.html',
  styleUrl: './movement-form.scss'
})
export class MovementForm {
  @Input() isEntry: boolean = true; // Indique si le formulaire est pour une entrée ou une sortie
  @Output() submitMovement = new EventEmitter<any>();
  
  customStatusOptions = getCustomStatusOptions();
  customDocTypeOptions = getCustomDocTypeOptions();
  refTypeOptions = getRefTypeOptions();

  declaredIn = DECLARED_IN;
  knownWarehouses = KNOWN_WAREHOUSES;

  errorMessage = signal<string | null>(null);
  movementForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.movementForm = this.fb.group({
      movementTime: [this.toLocalDatetimeInput(), Validators.required],
      warehouse: ['', Validators.required],
      customStatus: ['', Validators.required],
      referenceType: ['', Validators.required],
      reference: ['', [Validators.required, this.validateAWB.bind(this)]],
      description: ['', Validators.required],
      quantity: ['', Validators.required],
      weight: ['', Validators.required],
      totalQuantity: ['', Validators.required],
      totalWeight: ['', Validators.required],
      documentType: [''], // Pour les sorties uniquement
      documentReference: [''] // Pour les sorties uniquement
    });
  }
  
  validateAWB(control: AbstractControl): ValidationErrors | null {
    // Vérifier si le champ est vide
    if (!control.value) {
      return { required: true };
    }
    
    // Vérifier la validation spécifique pour AWB
    const referenceType = this.movementForm?.get('referenceType')?.value;
    if (referenceType === 'AWB' && !/^\d{11}$/.test(control.value)) {
      return { invalidAWB: true };
    }
    return null;
  }

  private toLocalDatetimeInput(d = new Date()): string {
    const p = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`;
  }


  private validateForm(): boolean {
    const v = this.movementForm.value;

    // Vérification du magasin
    const wh = this.knownWarehouses.find(w => w.code === v.warehouse);
    if (!wh) {
      this.errorMessage.set('Magasin obligatoire');
      console.warn('[MovementForm] Magasin obligatoire');
      this.movementForm.get('warehouse')?.setErrors({ required: true });
      return false;
    }

    // Vérification du document douanier pour une sortie
    if (!this.isEntry && (!v.documentType || !v.documentReference)) {
      this.errorMessage.set('Document douanier requis pour une sortie');
      console.warn('[MovementForm] Document douanier requis pour une sortie');
      this.movementForm.get('documentType')?.setErrors({ required: true });
      this.movementForm.get('documentReference')?.setErrors({ required: true });
      return false;
    }

    // Vérification du format AWB
    if (v.referenceType === 'AWB' && !/^\d{11}$/.test(v.reference)) {
      this.errorMessage.set('AWB doit contenir exactement 11 chiffres');
      console.warn('[MovementForm] AWB doit contenir exactement 11 chiffres');
      this.movementForm.get('reference')?.setErrors({ invalidAWB: true });
      return false;
    }

    return true;
  }

  private buildPayload(): MovementCreateRequestDto | null {
    const v = this.movementForm.value;
    const n = (x: any) => Number.isFinite(+x) ? +x : 0;
    const wh = this.knownWarehouses.find(w => w.code === v.warehouse);

    if (!wh) {
      return null;
    }

    const payload: MovementCreateRequestDto = {
      movementType: this.isEntry ? MovementTypeDto.In : MovementTypeDto.Out,
      movementTime: new Date(v.movementTime).toISOString(),
      createdBy: 'user', // TODO: user courant
      declaredIn: this.declaredIn,
      goods: {
        refType: v.referenceType,
        refCode: v.reference,
        description: v.description,
        quantity: n(v.quantity),
        weight: n(v.weight),
        totalQuantity: n(v.totalQuantity),
        totalWeight: n(v.totalWeight),
      },
      customsStatus: v.customStatus,
      ...(this.isEntry
        ? { fromWarehouse: { code: wh.code, label: wh.label } as WarehouseDto }
        : {
            toWarehouse: { code: wh.code, label: wh.label } as WarehouseDto,
            customsDocument: v.documentType && v.documentReference
              ? { type: v.documentType, ref: v.documentReference }
              : undefined
          })
    };

    return payload;
  }

  onSubmit() {
    if (this.movementForm.invalid) {
      this.movementForm.markAllAsTouched();
      return;
    }

    if (!this.validateForm()) {
      return;
    }

    this.errorMessage.set(null);

    const payload = this.buildPayload();
    if (!payload) {
      return;
    }
    // this.submitCreateMovement(payload);
    this.submitMovement.emit(payload);
  }

  resetWithDefaults() {
    this.movementForm.reset();
    this.movementForm.patchValue({ movementTime: this.toLocalDatetimeInput() });
  }
}
