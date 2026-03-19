import React from 'react'
import { screen, fireEvent, within } from '@testing-library/react'
import { render } from '../../test-utils'
import OrderTable from './OrderTable'

const mockOrders = [
  { id: 'o1', user: { username: 'alice' }, createdAt: '2024-01-01', description: 'First order' },
  { id: 'o2', user: { username: 'bob'   }, createdAt: '2024-01-02', description: 'Second order' },
]

function makeProps(overrides = {}) {
  return {
    orders: [],
    orderDescription: '',
    orderTextSearch: '',
    handleInputChange: vi.fn(),
    handleCreateOrder: vi.fn(),
    handleDeleteOrder: vi.fn(),
    handleSearchOrder: vi.fn(),
    ...overrides,
  }
}

// Find the delete ActionIcon button within the table row containing the given text
function getDeleteButtonInRow(rowText) {
  const row = screen.getByText(rowText).closest('tr')
  return within(row).getByRole('button')
}

describe('admin/OrderTable', () => {
  it('shows "No order" row when orders array is empty', () => {
    render(<OrderTable {...makeProps()} />)
    expect(screen.getByText('No order')).toBeInTheDocument()
  })

  it('renders a row for each order', () => {
    render(<OrderTable {...makeProps({ orders: mockOrders })} />)
    expect(screen.getByText('First order')).toBeInTheDocument()
    expect(screen.getByText('Second order')).toBeInTheDocument()
    expect(screen.getByText('alice')).toBeInTheDocument()
    expect(screen.getByText('bob')).toBeInTheDocument()
  })

  it('calls handleDeleteOrder with the correct order id when delete is clicked', () => {
    const handleDeleteOrder = vi.fn()
    render(<OrderTable {...makeProps({ orders: mockOrders, handleDeleteOrder })} />)
    fireEvent.click(getDeleteButtonInRow('First order'))
    expect(handleDeleteOrder).toHaveBeenCalledWith('o1')
  })

  it('calls handleSearchOrder when search form is submitted', () => {
    const handleSearchOrder = vi.fn((e) => e.preventDefault())
    render(<OrderTable {...makeProps({ handleSearchOrder })} />)
    fireEvent.submit(screen.getByPlaceholderText('Search by Id or Description').closest('form'))
    expect(handleSearchOrder).toHaveBeenCalledTimes(1)
  })

  it('renders OrderForm for creating new orders', () => {
    render(<OrderTable {...makeProps()} />)
    expect(screen.getByPlaceholderText('Description *')).toBeInTheDocument()
  })
})
