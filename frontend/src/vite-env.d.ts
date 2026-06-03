/// <reference types="vite/client" />

import React from 'react';

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string;
  readonly VITE_APP_NAME?: string;
  readonly VITE_APP_VERSION?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

declare global {
  namespace React {
    interface CSSProperties {
      [key: string]: unknown;
    }
  }
}

export {};
