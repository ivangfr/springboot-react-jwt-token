import React from 'react'
import { screen, fireEvent } from '@testing-library/react'
import { render, makeAdminUser, makeRegularUser, seedLocalStorage } from '../../test-utils'
import { AppShell } from '@mantine/core'
import Navbar from './Navbar'

// AppShell.Header requires an AppShell ancestor — wrap every render
function renderNavbar() {
  return render(
    <AppShell header={{ height: 60 }}>
      <Navbar />
    </AppShell>
  )
}

beforeEach(() => {
  localStorage.clear()
})

describe('Navbar — unauthenticated', () => {
  it('shows Login and Sign Up links', () => {
    renderNavbar()
    expect(screen.getByText('Login')).toBeInTheDocument()
    expect(screen.getByText('Sign Up')).toBeInTheDocument()
  })

  it('does not show Logout button', () => {
    renderNavbar()
    expect(screen.queryByText('Logout')).not.toBeInTheDocument()
  })

  it('does not show AdminPage or UserPage links', () => {
    renderNavbar()
    expect(screen.queryByText('AdminPage')).not.toBeInTheDocument()
    expect(screen.queryByText('UserPage')).not.toBeInTheDocument()
  })
})

describe('Navbar — admin user', () => {
  beforeEach(() => seedLocalStorage(makeAdminUser()))

  it('shows AdminPage link', () => {
    renderNavbar()
    expect(screen.getByText('AdminPage')).toBeInTheDocument()
  })

  it('does not show UserPage link', () => {
    renderNavbar()
    expect(screen.queryByText('UserPage')).not.toBeInTheDocument()
  })

  it('shows greeting with user name and Logout button', () => {
    renderNavbar()
    expect(screen.getByText(/Admin User/)).toBeInTheDocument()
    expect(screen.getByText('Logout')).toBeInTheDocument()
  })

  it('does not show Login or Sign Up links', () => {
    renderNavbar()
    expect(screen.queryByText('Login')).not.toBeInTheDocument()
    expect(screen.queryByText('Sign Up')).not.toBeInTheDocument()
  })
})

describe('Navbar — regular user', () => {
  beforeEach(() => seedLocalStorage(makeRegularUser()))

  it('shows UserPage link', () => {
    renderNavbar()
    expect(screen.getByText('UserPage')).toBeInTheDocument()
  })

  it('does not show AdminPage link', () => {
    renderNavbar()
    expect(screen.queryByText('AdminPage')).not.toBeInTheDocument()
  })
})

describe('Navbar — logout', () => {
  it('removes user from localStorage when Logout is clicked', () => {
    seedLocalStorage(makeRegularUser())
    renderNavbar()
    fireEvent.click(screen.getByText('Logout'))
    expect(localStorage.getItem('user')).toBeNull()
  })
})
