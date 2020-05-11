import React from 'react'
import { Grid, Table, Header, Icon } from 'semantic-ui-react'
import OrderForm from '../misc/OrderForm'

function OrderTable({ orders, orderDescription, handleInputChange, handleCreateOrder }) {
  let orderList
  if (!orders || orders.length === 0) {
    orderList = (
      <Table.Row key='no-order'>
        <Table.Cell collapsing textAlign='center' colSpan='3'>No order</Table.Cell>
      </Table.Row>
    )
  } else {
    orderList = orders.map(order => {
      return (
        <Table.Row key={order.id}>
          <Table.Cell>{order.id}</Table.Cell>
          <Table.Cell>{order.createdAt}</Table.Cell>
          <Table.Cell>{order.description}</Table.Cell>
        </Table.Row>
      )
    })
  }

  return (
    <>
      <Grid stackable divided>
        <Grid.Row columns='2'>
          <Grid.Column width='3'>
            <Header as='h2'>
              <Icon name='laptop' />
              <Header.Content>Orders</Header.Content>
            </Header>
          </Grid.Column>
          <Grid.Column>
            <OrderForm
              orderDescription={orderDescription}
              handleInputChange={handleInputChange}
              handleCreateOrder={handleCreateOrder}
            />
          </Grid.Column>
        </Grid.Row>
      </Grid>

      <Table compact striped>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell width={5}>ID</Table.HeaderCell>
            <Table.HeaderCell width={5}>Created At</Table.HeaderCell>
            <Table.HeaderCell width={6}>Description</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {orderList}
        </Table.Body>
      </Table>
    </>
  )
}

export default OrderTable