/**
 * Utilidades comunes
 */

/**
 * Retrasa la ejecución de una función (debounce)
 */
export const debounce = <T extends unknown[]>(
  func: (...args: T) => void,
  delay: number
): ((...args: T) => void) => {
  let timeoutId: ReturnType<typeof setTimeout>;
  return (...args: T) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delay);
  };
};

/**
 * Limita la frecuencia de ejecución de una función (throttle)
 */
export const throttle = <T extends unknown[]>(
  func: (...args: T) => void,
  limit: number
): ((...args: T) => void) => {
  let inThrottle: boolean;
  return (...args: T) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => {
        inThrottle = false;
      }, limit);
    }
  };
};

/**
 * Crea un delay promesa
 */
export const delay = (ms: number): Promise<void> => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

/**
 * Genera un ID único
 */
export const generateId = (): string => {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

/**
 * Copia un objeto profundamente
 */
export const deepClone = <T>(obj: T): T => {
  return JSON.parse(JSON.stringify(obj)) as T;
};
