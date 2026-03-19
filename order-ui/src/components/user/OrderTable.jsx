import React from 'react'
import { Grid, Group, Title, Box, LoadingOverlay, Table } from '@mantine/core'
import { IconDeviceLaptop } from '@tabler/icons-react'
import OrderForm from '../misc/OrderForm'

function OrderTable({ orders, isLoading, orderDescription, handleInputChange, handleCreateOrder }) {
  let orderList
  if (!orders || orders.length === 0) {
    orderList = (
      <Table.Tr key='no-order'>
        <Table.Td colSpan={3} ta='center'>No order</Table.Td>
      </Table.Tr>
    )
  } else {
    orderList = orders.map(order => (
      <Table.Tr key={order.id}>
        <Table.Td>{order.id}</Table.Td>
        <Table.Td>{order.createdAt}</Table.Td>
        <Table.Td>{order.description}</Table.Td>
      </Table.Tr>
    ))
  }

  return (
    <Box pos='relative'>
      <LoadingOverlay visible={isLoading} />
      <Grid mb='md' align='center'>
        <Grid.Col span={{ base: 12, sm: 3 }}>
          <Group>
            <IconDeviceLaptop size={28} />
            <Title order={2}>Orders</Title>
          </Group>
        </Grid.Col>
        <Grid.Col span={{ base: 12, sm: 9 }}>
          <OrderForm
            orderDescription={orderDescription}
            handleInputChange={handleInputChange}
            handleCreateOrder={handleCreateOrder}
          />
        </Grid.Col>
      </Grid>
      <Table striped highlightOnHover withTableBorder>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>ID</Table.Th>
            <Table.Th>Created At</Table.Th>
            <Table.Th>Description</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>{orderList}</Table.Tbody>
      </Table>
    </Box>
  )
}

export default OrderTable
