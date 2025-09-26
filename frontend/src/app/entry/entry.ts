import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MovementForm } from '../movement-form/movement-form';

@Component({
  selector: 'app-entry',
  imports: [MovementForm, CommonModule],
  templateUrl: './entry.html',
  styleUrl: './entry.scss'
})
export class Entry {
  onSubmitMovement(data: any) {
    console.log('Entrée soumise:', data);
    // Ajoutez ici la logique pour envoyer les données au serveur
  }
}
