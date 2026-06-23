import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { Loading } from '../Loading';

describe('Loading', () => {
  it('renders default message', () => {
    render(<Loading />);
    expect(screen.getByText('Cargando...')).toBeDefined();
  });

  it('renders custom message', () => {
    render(<Loading message="Obteniendo datos..." />);
    expect(screen.getByText('Obteniendo datos...')).toBeDefined();
  });

  it('renders spinner element', () => {
    const { container } = render(<Loading />);
    expect(container.querySelector('.spinner')).toBeDefined();
  });
});
