export * from './dev.service';
import { DevApi } from './dev.service';
export * from './movements.service';
import { MovementsApi } from './movements.service';
export * from './references.service';
import { ReferencesApi } from './references.service';
export const APIS = [DevApi, MovementsApi, ReferencesApi];
