import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    // sockjs-client assume un ambiente Node (usa `global`), inesistente nel browser
    global: 'globalThis',
  },
})
