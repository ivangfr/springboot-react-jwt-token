import { screen, fireEvent } from '@testing-library/react'
import { render } from '../../test-utils'
import AdminTab from './AdminTab'

function makeProps(overrides = {}) {
  return {
    isUsersLoading: false,
    users: [],
    userUsernameSearch: '',
    handleDeleteUser: vi.fn(),
    handleSearchUser: vi.fn(),
    isOrdersLoading: false,
    orders: [],
    orderDescription: '',
    orderTextSearch: '',
    handleCreateOrder: vi.fn(),
    handleDeleteOrder: vi.fn(),
    handleSearchOrder: vi.fn(),
    handleInputChange: vi.fn(),
    ...overrides,
  }
}

describe('AdminTab', () => {
  it('renders Users tab by default', () => {
    render(<AdminTab {...makeProps()} />)
    expect(screen.getByText('Users')).toBeInTheDocument()
    expect(screen.getByText('No user')).toBeInTheDocument()
  })

  it('switches to Orders panel when Orders tab is clicked', () => {
    render(<AdminTab {...makeProps()} />)
    fireEvent.click(screen.getByRole('tab', { name: /orders/i }))
    expect(screen.getByText('No order')).toBeInTheDocument()
  })

  it('renders user rows in the Users tab', () => {
    const users = [
      { id: 1, username: 'alice', name: 'Alice', email: 'alice@example.com', role: 'USER' },
    ]
    render(<AdminTab {...makeProps({ users })} />)
    expect(screen.getByText('alice')).toBeInTheDocument()
  })

  it('renders order rows in the Orders tab', () => {
    const orders = [
      { id: 'o1', user: { username: 'alice' }, createdAt: '2024-01-01', description: 'Test order' },
    ]
    render(<AdminTab {...makeProps({ orders })} />)
    fireEvent.click(screen.getByRole('tab', { name: /orders/i }))
    expect(screen.getByText('Test order')).toBeInTheDocument()
  })
})
