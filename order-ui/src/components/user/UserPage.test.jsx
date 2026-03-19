import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render, makeRegularUser, seedLocalStorage, makeToken } from '../../test-utils'
import UserPage from './UserPage'
import { orderApi } from '../misc/OrderApi'

vi.mock('../misc/OrderApi')

beforeEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
})

const userMeResponse = {
  username: 'bob',
  name: 'Bob',
  orders: [
    { id: 'o1', createdAt: '2024-01-01', description: 'Buy coffee' },
  ],
}

describe('UserPage', () => {
  it('redirects to / when the stored user is not USER role', () => {
    const userData = { sub: 'admin', rol: ['ADMIN'], name: 'Admin', exp: Math.floor(Date.now() / 1000) + 3600 }
    const user = { data: userData, accessToken: makeToken(userData) }
    seedLocalStorage(user)
    orderApi.getUserMe.mockResolvedValue({ data: userMeResponse })

    render(<UserPage />, { initialRoute: '/userpage' })
    expect(screen.queryByText('Orders')).not.toBeInTheDocument()
  })

  it('fetches and displays orders on mount', async () => {
    seedLocalStorage(makeRegularUser())
    orderApi.getUserMe.mockResolvedValue({ data: userMeResponse })

    render(<UserPage />)

    await waitFor(() => {
      expect(orderApi.getUserMe).toHaveBeenCalledTimes(1)
      expect(screen.getByText('Buy coffee')).toBeInTheDocument()
    })
  })

  it('calls createOrder and re-fetches on form submit', async () => {
    seedLocalStorage(makeRegularUser())
    orderApi.getUserMe.mockResolvedValue({ data: userMeResponse })
    orderApi.createOrder.mockResolvedValue({})

    render(<UserPage />)
    await waitFor(() => expect(screen.getByText('Buy coffee')).toBeInTheDocument())

    await userEvent.type(screen.getByPlaceholderText('Description *'), 'New item')
    await userEvent.click(screen.getByRole('button', { name: /create/i }))

    await waitFor(() => {
      expect(orderApi.createOrder).toHaveBeenCalledTimes(1)
      expect(orderApi.getUserMe).toHaveBeenCalledTimes(2)
    })
  })
})
