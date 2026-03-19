import React from 'react'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render, makeRegularUser, seedLocalStorage, makeToken } from '../../test-utils'
import Signup from './Signup'
import { orderApi } from '../misc/OrderApi'

vi.mock('../misc/OrderApi')

beforeEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
})

async function fillForm({ username = 'alice', password = 'secret', name = 'Alice', email = 'alice@example.com' } = {}) {
  if (username) await userEvent.type(screen.getByPlaceholderText('Username'), username)
  if (password) await userEvent.type(screen.getByPlaceholderText('Password'), password)
  if (name)     await userEvent.type(screen.getByPlaceholderText('Name'), name)
  if (email)    await userEvent.type(screen.getByPlaceholderText('Email'), email)
}

describe('Signup', () => {
  it('redirects to / when already logged in', () => {
    seedLocalStorage(makeRegularUser())
    render(<Signup />, { initialRoute: '/signup' })
    expect(screen.queryByPlaceholderText('Username')).not.toBeInTheDocument()
  })

  it('shows error alert when any required field is missing', async () => {
    render(<Signup />)
    // Submit with only username filled — other fields empty
    await userEvent.type(screen.getByPlaceholderText('Username'), 'alice')
    await userEvent.click(screen.getByRole('button', { name: /sign up/i }))
    expect(screen.getByRole('alert')).toBeInTheDocument()
  })

  it('shows a generic error message on network failure (no error.response)', async () => {
    orderApi.signup.mockRejectedValue({ message: 'Network Error' })
    render(<Signup />)
    await fillForm()
    await userEvent.click(screen.getByRole('button', { name: /sign up/i }))
    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument()
      expect(screen.getByText(/unexpected error/i)).toBeInTheDocument()
    })
  })

  it('shows conflict message on 409 response', async () => {
    orderApi.signup.mockRejectedValue({
      response: { data: { status: 409, message: 'Username already in use' } },
    })
    render(<Signup />)
    await fillForm()
    await userEvent.click(screen.getByRole('button', { name: /sign up/i }))
    await waitFor(() => {
      expect(screen.getByText('Username already in use')).toBeInTheDocument()
    })
  })

  it('shows validation message on 400 response', async () => {
    orderApi.signup.mockRejectedValue({
      response: {
        data: {
          status: 400,
          errors: [{ defaultMessage: 'Email must be valid' }],
        },
      },
    })
    render(<Signup />)
    await fillForm()
    await userEvent.click(screen.getByRole('button', { name: /sign up/i }))
    await waitFor(() => {
      expect(screen.getByText('Email must be valid')).toBeInTheDocument()
    })
  })

  it('stores user in localStorage on successful signup', async () => {
    const futureExp = Math.floor(Date.now() / 1000) + 3600
    const payload = { sub: 'alice', rol: ['USER'], name: 'Alice', exp: futureExp }
    orderApi.signup.mockResolvedValue({ data: { accessToken: makeToken(payload) } })

    render(<Signup />)
    await fillForm()
    await userEvent.click(screen.getByRole('button', { name: /sign up/i }))

    await waitFor(() => {
      expect(localStorage.getItem('user')).not.toBeNull()
    })
  })
})
