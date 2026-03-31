import { screen, fireEvent, within } from '@testing-library/react'
import { render } from '../../test-utils'
import UserTable from './UserTable'

const mockUsers = [
  { id: 1, username: 'admin',  name: 'Admin',     email: 'admin@example.com',  role: 'ADMIN' },
  { id: 2, username: 'bob',    name: 'Bob',        email: 'bob@example.com',    role: 'USER'  },
]

function makeProps(overrides = {}) {
  return {
    users: [],
    userUsernameSearch: '',
    handleInputChange: vi.fn(),
    handleDeleteUser: vi.fn(),
    handleSearchUser: vi.fn(),
    ...overrides,
  }
}

// Find the delete ActionIcon button within a table row that contains the given text
function getDeleteButtonInRow(rowText) {
  const row = screen.getByText(rowText).closest('tr')
  return within(row).getByRole('button')
}

describe('UserTable', () => {
  it('shows "No user" row when users array is empty', () => {
    render(<UserTable {...makeProps()} />)
    expect(screen.getByText('No user')).toBeInTheDocument()
  })

  it('renders a row for each user', () => {
    render(<UserTable {...makeProps({ users: mockUsers })} />)
    expect(screen.getByText('bob')).toBeInTheDocument()
    expect(screen.getByText('Bob')).toBeInTheDocument()
    expect(screen.getByText('bob@example.com')).toBeInTheDocument()
    expect(screen.getByText('USER')).toBeInTheDocument()
  })

  it('delete button is disabled for the admin user', () => {
    render(<UserTable {...makeProps({ users: mockUsers })} />)
    expect(getDeleteButtonInRow('admin')).toBeDisabled()
  })

  it('delete button is enabled for non-admin users', () => {
    render(<UserTable {...makeProps({ users: mockUsers })} />)
    expect(getDeleteButtonInRow('bob')).not.toBeDisabled()
  })

  it('calls handleDeleteUser with the correct username when delete is clicked', () => {
    const handleDeleteUser = vi.fn()
    render(<UserTable {...makeProps({ users: mockUsers, handleDeleteUser })} />)
    fireEvent.click(getDeleteButtonInRow('bob'))
    expect(handleDeleteUser).toHaveBeenCalledWith('bob')
  })

  it('calls handleSearchUser when search form is submitted', () => {
    const handleSearchUser = vi.fn((e) => e.preventDefault())
    render(<UserTable {...makeProps({ handleSearchUser })} />)
    fireEvent.submit(screen.getByPlaceholderText('Search by Username').closest('form'))
    expect(handleSearchUser).toHaveBeenCalledTimes(1)
  })
})
