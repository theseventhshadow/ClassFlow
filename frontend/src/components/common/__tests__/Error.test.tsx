import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import { Error } from '../Error';

describe('Error', () => {
  it('renders error message', () => {
    render(<Error message="Algo salió mal" />);
    expect(screen.getByText('Algo salió mal')).toBeDefined();
  });

  it('renders retry button when onRetry is provided', () => {
    const onRetry = vi.fn();
    render(<Error message="Error" onRetry={onRetry} />);
    expect(screen.getByText('Reintentar')).toBeDefined();
  });

  it('calls onRetry when retry button is clicked', async () => {
    const onRetry = vi.fn();
    render(<Error message="Error" onRetry={onRetry} />);
    await userEvent.click(screen.getByText('Reintentar'));
    expect(onRetry).toHaveBeenCalledTimes(1);
  });

  it('does not render retry button when onRetry is not provided', () => {
    render(<Error message="Error" />);
    expect(screen.queryByText('Reintentar')).toBeNull();
  });
});
