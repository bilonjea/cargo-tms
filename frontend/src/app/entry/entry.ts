import { CommonModule } from '@angular/common';
import { Component, inject, ViewChild } from '@angular/core';
import { MovementForm } from '../movement-form/movement-form';
import { MovementCreateRequestDto} from '../api';
import { MovementFacade } from '@services/movement-facade';

@Component({
  selector: 'app-entry',
  imports: [MovementForm, CommonModule],
  templateUrl: './entry.html',
  styleUrl: './entry.scss'
})
export class Entry {
  @ViewChild(MovementForm) form!: MovementForm;
  facade = inject(MovementFacade);

  onSubmitMovement = (payload: MovementCreateRequestDto) => {
    //this.facade.createMovement(payload).subscribe();
    this.facade.createMovement(payload).subscribe(res => {
      if (res) {
        this.form.resetWithDefaults();
        //this.facade.lastResponse.set(null);
        setTimeout(() => this.facade.lastResponse.set(null), 3000);
      }
    });
  };
}
