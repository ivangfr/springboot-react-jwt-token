import React, { Fragment } from 'react'
import { Grid, Form, Icon, Button, Input, Table } from 'semantic-ui-react'

function OrderTable({ orders, orderDescription, orderTextSearch, handleChange, createOrder, deleteOrder, searchOrder }) {
  let orderList
  if (orders.length === 0) {
    orderList = (
      <Table.Row key='no-order'>
        <Table.Cell collapsing textAlign='center' colSpan='4'>No order</Table.Cell>
      </Table.Row>
    )
  } else {
    orderList = orders.map(order => {
      return (
        <Table.Row key={order.id}>
          <Table.Cell collapsing>
            <Button
              circular
              color='red'
              size='small'
              icon='trash'
              onClick={() => deleteOrder(order.id)}
            />
          </Table.Cell>
          <Table.Cell>{order.id}</Table.Cell>
          <Table.Cell>{order.description}</Table.Cell>
        </Table.Row>
      )
    })
  }

  return (
    <Fragment>
      <Grid stackable divided>
        <Grid.Row columns='2'>
          <Grid.Column width='5'>
            <Form onSubmit={searchOrder}>
              <Input
                action={{ icon: 'search' }}
                id='orderTextSearch'
                placeholder='Search by Id or Description'
                value={orderTextSearch}
                onChange={handleChange}
              />
            </Form>
          </Grid.Column>
          <Grid.Column>
            <Form onSubmit={createOrder}>
              <Form.Group>
                <Form.Input
                  id='orderDescription'
                  placeholder='Description'
                  value={orderDescription}
                  onChange={handleChange}
                />
                <Button icon>
                  <Icon name='add' />
                </Button>
              </Form.Group>
            </Form>
          </Grid.Column>
        </Grid.Row>
      </Grid>
      <Table compact striped selectable>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell />
            <Table.HeaderCell>ID</Table.HeaderCell>
            <Table.HeaderCell>Description</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {orderList}
        </Table.Body>
      </Table>
    </Fragment>
  )
}

export default OrderTable