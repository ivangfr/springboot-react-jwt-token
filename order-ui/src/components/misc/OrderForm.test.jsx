import React from 'react'
import { screen, fireEvent } from '@testing-library/react'
import { render } from '../../test-utils'
import OrderForm from './OrderForm'

function makeProps(overrides = {}) {
  return {
    orderDescription: '',
    handleInputChange: vi.fn(),
    handleCreateOrder: vi.fn(),
    ...overrides,
  }
}

describe('OrderForm', () => {
  it('disables Create button when description is empty', () => {
    render(<OrderForm {...makeProps({ orderDescription: '' })} />)
    expect(screen.getByRole('button', { name: /create/i })).toBeDisabled()
  })

  it('disables Create button when description is only whitespace', () => {
    render(<OrderForm {...makeProps({ orderDescription: '   ' })} />)
    expect(screen.getByRole('button', { name: /create/i })).toBeDisabled()
  })

  it('enables Create button when description has content', () => {
    render(<OrderForm {...makeProps({ orderDescription: 'My order' })} />)
    expect(screen.getByRole('button', { name: /create/i })).not.toBeDisabled()
  })

  it('calls handleInputChange when the input value changes', () => {
    const handleInputChange = vi.fn()
    render(<OrderForm {...makeProps({ handleInputChange })} />)
    fireEvent.change(screen.getByPlaceholderText('Description *'), {
      target: { value: 'New order' },
    })
    expect(handleInputChange).toHaveBeenCalledTimes(1)
  })

  it('calls handleCreateOrder when the form is submitted', () => {
    const handleCreateOrder = vi.fn()
    render(<OrderForm {...makeProps({ orderDescription: 'Test', handleCreateOrder })} />)
    fireEvent.submit(screen.getByRole('button', { name: /create/i }).closest('form'))
    expect(handleCreateOrder).toHaveBeenCalledTimes(1)
  })
})
