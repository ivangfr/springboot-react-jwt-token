import { screen, waitFor } from '@testing-library/react'
import { render, makeAdminUser, makeToken, seedLocalStorage } from '../../test-utils'
import AdminPage from './AdminPage'
import { orderApi } from '../misc/OrderApi'

vi.mock('../misc/OrderApi')

beforeEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
})

describe('AdminPage', () => {
  it('redirects to / when the stored user is not ADMIN', () => {
    // Seed a USER role — AdminPage checks rol[0] === 'ADMIN'
    const userData = { sub: 'bob', rol: ['USER'], name: 'Bob', exp: Math.floor(Date.now() / 1000) + 3600 }
    const user = { data: userData, accessToken: makeToken(userData) }
    seedLocalStorage(user)

    orderApi.getUsers.mockResolvedValue({ data: [] })
    orderApi.getOrders.mockResolvedValue({ data: [] })

    render(<AdminPage />, { initialRoute: '/adminpage' })
    // AdminPage renders <Navigate to='/' /> immediately — Users tab should not appear
    expect(screen.queryByRole('tab', { name: /users/i })).not.toBeInTheDocument()
  })

  it('loads and displays the admin tabs when user is ADMIN', async () => {
    seedLocalStorage(makeAdminUser())
    orderApi.getUsers.mockResolvedValue({ data: [] })
    orderApi.getOrders.mockResolvedValue({ data: [] })

    render(<AdminPage />)

    await waitFor(() => {
      expect(screen.getByRole('tab', { name: /users/i })).toBeInTheDocument()
      expect(screen.getByRole('tab', { name: /orders/i })).toBeInTheDocument()
    })
  })

  it('fetches users and orders on mount', async () => {
    seedLocalStorage(makeAdminUser())
    orderApi.getUsers.mockResolvedValue({ data: [
      { id: 1, username: 'alice', name: 'Alice', email: 'alice@example.com', role: 'USER' },
    ]})
    orderApi.getOrders.mockResolvedValue({ data: [] })

    render(<AdminPage />)

    await waitFor(() => {
      expect(orderApi.getUsers).toHaveBeenCalledTimes(1)
      expect(orderApi.getOrders).toHaveBeenCalledTimes(1)
      expect(screen.getByText('alice')).toBeInTheDocument()
    })
  })

  it('shows loading overlays while fetching and hides them after', async () => {
    seedLocalStorage(makeAdminUser())
    let resolveUsers, resolveOrders
    orderApi.getUsers.mockReturnValue(new Promise(r => { resolveUsers = r }))
    orderApi.getOrders.mockReturnValue(new Promise(r => { resolveOrders = r }))

    const { container } = render(<AdminPage />)

    expect(container.querySelector('.mantine-LoadingOverlay-root')).toBeInTheDocument()

    resolveUsers({ data: [] })
    resolveOrders({ data: [] })
    await waitFor(() => expect(container.querySelector('.mantine-LoadingOverlay-root')).not.toBeInTheDocument())
  })
})
