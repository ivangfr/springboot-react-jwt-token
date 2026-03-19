import React from 'react'
import ReactDOM from 'react-dom/client'
import '@mantine/core/styles.css'
import './index.css'
import { MantineProvider } from '@mantine/core'
import App from './App'

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
  <MantineProvider>
    <App />
  </MantineProvider>
)
