import { CommonModule } from '@angular/common';
import { Component, OnInit  } from '@angular/core';

@Component({
  selector: 'app-history',
  imports: [],
  templateUrl: './history.html',
  styleUrl: './history.scss'
})
export class History implements OnInit {
movements: any[] = [];

  ngOnInit() {
    // Récupérez ici les mouvements depuis le serveur
    this.movements = [
      { timestamp: '23-12-2017 22:32:48', type: 'SORTIE', from: 'NV1', to: 'SL2', reference: 'AWB 07712345678', quantity: 12, weight: 203.23, customStatus: 'X' },
      { timestamp: '23-12-2017 22:18:53', type: 'ENTREE', from: 'Dupoint', to: 'NV1', reference: 'AWB 07712345679', quantity: 2, weight: 42.12, customStatus: 'C' },
      // Ajoutez d'autres mouvements ici
    ];
  }
}
