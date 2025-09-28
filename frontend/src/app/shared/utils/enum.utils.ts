export function enumToOptions<T extends object>(
    e: T
): { value: T[keyof T], label: string }[] {
  return Object.values(e).map(value => ({ 
    value, label: String(value) 
 }));
}

