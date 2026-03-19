import { screen, waitFor } from '@testing-library/react'
import { render } from '../../test-utils'
import Home from './Home'
import { orderApi } from '../misc/OrderApi'

vi.mock('../misc/OrderApi')

beforeEach(() => {
  vi.clearAllMocks()
  localStorage.clear()
})

describe('Home', () => {
  it('renders user and order counts after data loads', async () => {
    orderApi.numberOfUsers.mockResolvedValue({ data: 5 })
    orderApi.numberOfOrders.mockResolvedValue({ data: 12 })

    render(<Home />)

    await waitFor(() => {
      expect(screen.getByText('5')).toBeInTheDocument()
      expect(screen.getByText('12')).toBeInTheDocument()
    })

    expect(screen.getByText('Users')).toBeInTheDocument()
    expect(screen.getByText('Orders')).toBeInTheDocument()
  })

  it('shows 0 counts when API calls fail', async () => {
    orderApi.numberOfUsers.mockRejectedValue(new Error('Network error'))
    orderApi.numberOfOrders.mockRejectedValue(new Error('Network error'))

    render(<Home />)

    await waitFor(() => {
      // Both counts remain at default 0
      const zeros = screen.getAllByText('0')
      expect(zeros.length).toBeGreaterThanOrEqual(2)
    })
  })
})
