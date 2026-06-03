/**
 * Utilidades de validación
 */

/**
 * Valida un email
 */
export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Valida una contraseña
 * Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número
 */
export const validatePassword = (password: string): boolean => {
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
  return passwordRegex.test(password);
};

/**
 * Valida un teléfono
 */
export const validatePhone = (phone: string): boolean => {
  const phoneRegex = /^(\+\d{1,3}[- ]?)?\d{6,14}$/;
  return phoneRegex.test(phone);
};

/**
 * Valida un URL
 */
export const validateUrl = (url: string): boolean => {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

/**
 * Verifica si un valor está vacío
 */
export const isEmpty = (value: unknown): boolean => {
  if (typeof value === 'string') return value.trim().length === 0;
  if (Array.isArray(value)) return value.length === 0;
  if (value === null || value === undefined) return true;
  return false;
};
