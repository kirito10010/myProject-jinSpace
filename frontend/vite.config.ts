import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5273,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8086',
        changeOrigin: true
      },
      '/music': {
        target: 'http://api.zhyunxi.com',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/music/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  }
})
