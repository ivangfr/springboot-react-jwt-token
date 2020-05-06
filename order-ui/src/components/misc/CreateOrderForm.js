import React from 'react'
import { Form, Button, Icon } from 'semantic-ui-react'

function CreateOrderForm({ orderDescription, handleInputChange, handleCreateOrder }) {
  return (
    <Form onSubmit={handleCreateOrder}>
      <Form.Group>
        <Form.Input
          id='orderDescription'
          placeholder='Description'
          value={orderDescription}
          onChange={handleInputChange}
        />
        <Button icon labelPosition='right'>
          Create<Icon name='add' />
        </Button>
      </Form.Group>
    </Form>
  )
}

export default CreateOrderForm