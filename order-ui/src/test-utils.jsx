import { render } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { MantineProvider } from '@mantine/core'
import { AuthProvider } from './components/context/AuthContext'

// Build a minimal but structurally valid JWT with the given payload.
// The signature segment is a placeholder — OrderApi/AuthContext only decode
// the payload (middle) segment, so the signature is never verified in tests.
export function makeToken(payload) {
  const header  = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
  const body    = btoa(JSON.stringify(payload))
    .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
  return `${header}.${body}.fakesig`
}

// Pre-built user fixtures. exp is always relative to the current time so
// tests don't break when run in the future.
const futureExp  = () => Math.floor(Date.now() / 1000) + 3600
const expiredExp = () => Math.floor(Date.now() / 1000) - 3600

export function makeAdminUser() {
  const data = { sub: 'admin', rol: ['ADMIN'], name: 'Admin User', exp: futureExp() }
  return { data, accessToken: makeToken(data) }
}

export function makeRegularUser() {
  const data = { sub: 'bob', rol: ['USER'], name: 'Bob', exp: futureExp() }
  return { data, accessToken: makeToken(data) }
}

export function makeExpiredUser() {
  const data = { sub: 'bob', rol: ['USER'], name: 'Bob', exp: expiredExp() }
  return { data, accessToken: makeToken(data) }
}

// Seed localStorage before a component that reads user synchronously at render.
export function seedLocalStorage(user) {
  localStorage.setItem('user', JSON.stringify(user))
}

// renderWithProviders — wraps the component under test with all required
// providers. Pass initialRoute to control the MemoryRouter starting location.
function renderWithProviders(ui, { initialRoute = '/' } = {}) {
  function Wrapper({ children }) {
    return (
      <MantineProvider>
        <MemoryRouter initialEntries={[initialRoute]}>
          <AuthProvider>
            {children}
          </AuthProvider>
        </MemoryRouter>
      </MantineProvider>
    )
  }
  return render(ui, { wrapper: Wrapper })
}

export * from '@testing-library/react'
export { renderWithProviders as render }
