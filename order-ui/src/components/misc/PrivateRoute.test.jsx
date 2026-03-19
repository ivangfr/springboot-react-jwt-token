import React from 'react'
import { screen } from '@testing-library/react'
import { render, makeRegularUser, makeExpiredUser, seedLocalStorage } from '../../test-utils'
import PrivateRoute from './PrivateRoute'

beforeEach(() => {
  localStorage.clear()
})

describe('PrivateRoute', () => {
  it('redirects to /login when not authenticated', () => {
    render(
      <PrivateRoute>
        <div>protected content</div>
      </PrivateRoute>,
      { initialRoute: '/protected' }
    )
    // MemoryRouter doesn't render a real URL bar, but Navigate will swap
    // the rendered content. The protected text must not appear.
    expect(screen.queryByText('protected content')).not.toBeInTheDocument()
  })

  it('renders children when authenticated with a valid token', () => {
    seedLocalStorage(makeRegularUser())
    render(
      <PrivateRoute>
        <div>protected content</div>
      </PrivateRoute>
    )
    expect(screen.getByText('protected content')).toBeInTheDocument()
  })

  it('redirects when the stored token is already expired', () => {
    // makeExpiredUser has exp in the past — userIsAuthenticated() returns false
    seedLocalStorage(makeExpiredUser())
    render(
      <PrivateRoute>
        <div>protected content</div>
      </PrivateRoute>
    )
    expect(screen.queryByText('protected content')).not.toBeInTheDocument()
  })
})
