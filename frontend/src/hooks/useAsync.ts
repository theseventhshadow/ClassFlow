import { useState, useCallback } from 'react';
import { ApiError } from '@types';

interface UseAsyncState<T> {
  data: T | null;
  loading: boolean;
  error: ApiError | null;
}

/**
 * Hook personalizado para manejar peticiones asincrónicas
 * Simplifica la lógica de loading, error y data
 */
export function useAsync<T>(
  asyncFunction: () => Promise<T>,
  immediate = true
): UseAsyncState<T> & { execute: () => Promise<void> } {
  const [state, setState] = useState<UseAsyncState<T>>({
    data: null,
    loading: immediate,
    error: null,
  });

  const execute = useCallback(async () => {
    setState({ data: null, loading: true, error: null });
    try {
      const response = await asyncFunction();
      setState({ data: response, loading: false, error: null });
    } catch (error) {
      setState({
        data: null,
        loading: false,
        error: error instanceof Error ? { message: error.message, status: 0 } : (error as ApiError),
      });
    }
  }, [asyncFunction]);

  // Ejecutar automáticamente si immediate es true
  if (immediate && state.loading) {
    execute();
  }

  return { ...state, execute };
}
