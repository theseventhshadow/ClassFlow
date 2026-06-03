import { useState, useCallback } from 'react';

/**
 * Hook personalizado para manejar formularios
 * Simplifica el estado y validación de campos
 */
export function useForm<T extends Record<string, unknown>>(initialValues: T) {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState<Partial<T>>({});
  const [touched, setTouched] = useState<Partial<Record<keyof T, boolean>>>({});

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
      const { name, value, type } = e.target;
      const finalValue = type === 'checkbox' ? (e.target as HTMLInputElement).checked : value;

      setValues((prev) => ({
        ...prev,
        [name]: finalValue,
      }));
    },
    []
  );

  const handleBlur = useCallback((e: React.FocusEvent<HTMLInputElement>) => {
    const { name } = e.target;
    setTouched((prev) => ({
      ...prev,
      [name]: true,
    }));
  }, []);

  const handleReset = useCallback(() => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
  }, [initialValues]);

  const setFieldValue = useCallback((name: keyof T, value: unknown) => {
    setValues((prev) => ({
      ...prev,
      [name]: value,
    }));
  }, []);

  const setFieldError = useCallback((name: keyof T, error: string) => {
    setErrors((prev) => ({
      ...prev,
      [name]: error,
    }));
  }, []);

  return {
    values,
    errors,
    touched,
    handleChange,
    handleBlur,
    handleReset,
    setFieldValue,
    setFieldError,
  };
}
