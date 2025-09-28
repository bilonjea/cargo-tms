/**
 * Transforme un ProblemDto (RFC 7807) en message lisible multi-lignes
 */
export function problemToMessage(p: any): string {
  // entête : detail > title(+status) > fallback
  const header =
    (p?.detail as string) ??
    (p ? `${p.title ?? 'Validation Error'}${p.status ? ` (${p.status})` : ''}` : 'Une erreur est survenue');

  // erreurs agrégées: supporte p.errors ou p.additionalProperties.errors
  const list =
    Array.isArray(p?.errors) ? p.errors :
    Array.isArray(p?.additionalProperties?.errors) ? p.additionalProperties.errors :
    null;

  if (list?.length) {
    const lines = list.map((e: any) => `${e.field ?? '—'} - ${e.message ?? ''}`);
    return [header, ...lines].join('\n');   // multi-lignes
  }

  return header;
}
