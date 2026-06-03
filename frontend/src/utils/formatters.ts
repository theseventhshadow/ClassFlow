/**
 * Utilidades de formato
 */

/**
 * Formatea una fecha en formato legible
 */
export const formatDate = (date: string | Date, locale = 'es-ES'): string => {
  return new Date(date).toLocaleDateString(locale);
};

/**
 * Formatea un número como moneda
 */
export const formatCurrency = (amount: number, currency = 'USD'): string => {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency,
  }).format(amount);
};

/**
 * Trunca un string a una longitud específica
 */
export const truncateString = (str: string, length: number): string => {
  return str.length > length ? `${str.substring(0, length)}...` : str;
};

/**
 * Capitaliza la primera letra de un string
 */
export const capitalize = (str: string): string => {
  return str.charAt(0).toUpperCase() + str.slice(1);
};
