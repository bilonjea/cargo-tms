import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MovementForm } from '../movement-form/movement-form';

@Component({
  selector: 'app-exit',
  imports: [MovementForm, CommonModule],
  templateUrl: './exit.html',
  styleUrl: './exit.scss'
})
export class Exit {
  onSubmitMovement(data: any) {
    console.log('Sortie soumise:', data);
    // Ajoutez ici la logique pour envoyer les données au serveur
  }
}
