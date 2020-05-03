import React from 'react'
import { Form, Button, Icon } from 'semantic-ui-react'

function CreateOrderForm({ orderDescription, handleChange, createOrder }) {
  return (
    <Form onSubmit={createOrder}>
      <Form.Group>
        <Form.Input
          id='orderDescription'
          placeholder='Description'
          value={orderDescription}
          onChange={handleChange}
        />
        <Button icon labelPosition='right'>
          Create<Icon name='add' />
        </Button>
      </Form.Group>
    </Form>
  )
}

export default CreateOrderForm