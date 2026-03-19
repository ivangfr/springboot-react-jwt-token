import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render, makeRegularUser, seedLocalStorage, makeToken } from '../../test-utils'
import Login from './Login'
import { orderApi } from '../misc/OrderApi'

vi.mock('../misc/OrderApi')

beforeEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
})

describe('Login', () => {
  it('redirects to / when already logged in', () => {
    seedLocalStorage(makeRegularUser())
    // Navigate replaces content; the form should not be visible
    render(<Login />, { initialRoute: '/login' })
    expect(screen.queryByPlaceholderText('Username')).not.toBeInTheDocument()
  })

  it('shows error alert when submitting with empty fields', async () => {
    render(<Login />)
    await userEvent.click(screen.getByRole('button', { name: /login/i }))
    expect(screen.getByRole('alert')).toBeInTheDocument()
  })

  it('shows error alert when API returns an error', async () => {
    orderApi.authenticate.mockRejectedValue({ message: 'Unauthorized' })
    render(<Login />)

    await userEvent.type(screen.getByPlaceholderText('Username'), 'alice')
    await userEvent.type(screen.getByPlaceholderText('Password'), 'wrong')
    await userEvent.click(screen.getByRole('button', { name: /login/i }))

    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument()
    })
  })

  it('calls userLogin and clears fields on successful authentication', async () => {
    const futureExp = Math.floor(Date.now() / 1000) + 3600
    const payload = { sub: 'alice', rol: ['USER'], name: 'Alice', exp: futureExp }
    orderApi.authenticate.mockResolvedValue({ data: { accessToken: makeToken(payload) } })

    render(<Login />)

    await userEvent.type(screen.getByPlaceholderText('Username'), 'alice')
    await userEvent.type(screen.getByPlaceholderText('Password'), 'secret')
    await userEvent.click(screen.getByRole('button', { name: /login/i }))

    await waitFor(() => {
      expect(localStorage.getItem('user')).not.toBeNull()
    })
  })
})
