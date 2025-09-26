import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-movement-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movement-form.html',
  styleUrl: './movement-form.scss'
})
export class MovementForm {
  @Input() isEntry: boolean = true; // Indique si le formulaire est pour une entrée ou une sortie
  @Output() submitMovement = new EventEmitter<any>();

  movementForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.movementForm = this.fb.group({
      warehouse: ['', Validators.required],
      customStatus: ['', Validators.required],
      referenceType: ['', Validators.required],
      reference: ['', [Validators.required, this.validateAWB]],
      description: ['', Validators.required],
      quantity: ['', Validators.required],
      weight: ['', Validators.required],
      totalQuantity: ['', Validators.required],
      totalWeight: ['', Validators.required],
      documentType: [''], // Pour les sorties uniquement
      documentReference: [''] // Pour les sorties uniquement
    });
  }

  // Validation personnalisée pour AWB
  validateAWB(control: any) {
    if (control.value && control.value.length !== 11) {
      return { invalidAWB: true };
    }
    return null;
  }

  onSubmit() {
    if (this.movementForm.valid) {
      this.submitMovement.emit(this.movementForm.value);
    }
  }

  onCancel() {
    this.movementForm.reset();
  }
}
