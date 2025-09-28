import { RefTypeDto, WarehouseDto, CustomsDocumentDto  } from '../api';
import { enumToOptions } from '@shared/utils/enum.utils';

export enum CustomStatusDto {
  A = 'A',
  D = 'D',
  X = 'X'
}

export enum CustomDocTypeDto {
  AX = 'AX',
  BA = 'BA',
  CE = 'CE',
  AF = 'AF',
  KK = 'KK',
  LB = 'LB',
  MK = 'MK',
  IN = 'IN',
  XT = 'XT',
  BK = 'BK',
  EX = 'EX'
}


const CUSTOM_STATUS_LABELS: Record<CustomStatusDto, string> = {
  A: 'Approbation',
  D: 'Bloqué',
  X: 'Contrôle douane'
};


export function getCustomStatusLabelsOptions() {
  return Object.values(CustomStatusDto).map(value => ({
    value,
    label: CUSTOM_STATUS_LABELS[value] ?? value
  }));
}

/* TODO: REMOVE
export function getCustomStatusOptions(): { value: CustomStatusDto, label: string }[] {
  return Object.values(CustomStatusDto).map(value => ({
    value: value,
    label: value
  }));
}
*/
export const getCustomStatusOptions = () => enumToOptions(CustomStatusDto);
export const getRefTypeOptions = () => enumToOptions(RefTypeDto);
export const getCustomDocTypeOptions = () => enumToOptions(CustomDocTypeDto);


// entrepôt déclaré "chez nous"
export const DECLARED_IN: WarehouseDto = {
  code: 'RAPIDCARGO',
  label: 'RapidCargo CDG',
};

// entrepôts connus pour les déplacements IN/OUT
export const KNOWN_WAREHOUSES: WarehouseDto[] = [
  { code: 'CDG', label: 'Paris Charles-de-Gaulle' },
  { code: 'BRU', label: 'Bruxelles Zaventem' },
  { code: 'LHR', label: 'London Heathrow' },
  { code: 'FRA', label: 'Frankfurt Main' },
  { code: 'NOT', label: 'Warehouse NOT IN DB' }
];


