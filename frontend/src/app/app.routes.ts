import { Routes } from '@angular/router';
import { History } from './history/history';
import { Entry } from './entry/entry';
import { Exit } from './exit/exit';

export const routes: Routes = [
    { path: 'mouvements', component: History },
    { path: 'mouvements/entry', component: Entry },
    { path: 'mouvements/exit', component: Exit },
    { path: '', pathMatch: 'full', redirectTo: 'mouvements' },
];
