import React from 'react';
import { mediaService } from '@services';

interface MediaImageProps extends React.ImgHTMLAttributes<HTMLImageElement> {
  fallbackSrc?: string;
  loading?: 'eager' | 'lazy';
  onLoadError?: () => void;
}

/**
 * Componente Image con fallback automático
 * Carga placeholder si hay error o no hay URL
 */
export const MediaImage: React.FC<MediaImageProps> = ({
  src,
  fallbackSrc = mediaService.getPlaceholderUrl('image'),
  onError,
  onLoadError,
  loading = 'lazy',
  alt = 'Imagen',
  ...props
}) => {
  const handleError = (e: React.SyntheticEvent<HTMLImageElement>) => {
    const img = e.currentTarget;
    if (img.src !== fallbackSrc) {
      img.src = fallbackSrc;
    }
    onError?.(e as any);
    onLoadError?.();
  };

  return (
    <img
      src={src || fallbackSrc}
      alt={alt}
      loading={loading}
      onError={handleError}
      {...props}
    />
  );
};

export default MediaImage;
