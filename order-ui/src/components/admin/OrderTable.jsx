import { Grid, Group, TextInput, ActionIcon, Table } from '@mantine/core'
import { IconSearch, IconTrash } from '@tabler/icons-react'
import OrderForm from '../misc/OrderForm'

function OrderTable({ orders, orderDescription, orderTextSearch, handleInputChange, handleCreateOrder, handleDeleteOrder, handleSearchOrder }) {
  let orderList
  if (orders.length === 0) {
    orderList = (
      <Table.Tr key='no-order'>
        <Table.Td colSpan={5} ta='center'>No order</Table.Td>
      </Table.Tr>
    )
  } else {
    orderList = orders.map(order => (
      <Table.Tr key={order.id}>
        <Table.Td>
          <ActionIcon color='red' variant='light' size='sm' onClick={() => handleDeleteOrder(order.id)}>
            <IconTrash size={14} />
          </ActionIcon>
        </Table.Td>
        <Table.Td>{order.id}</Table.Td>
        <Table.Td>{order.user.username}</Table.Td>
        <Table.Td>{order.createdAt}</Table.Td>
        <Table.Td>{order.description}</Table.Td>
      </Table.Tr>
    ))
  }

  return (
    <>
      <Grid mb='md'>
        <Grid.Col span={{ base: 12, sm: 5 }}>
          <form onSubmit={handleSearchOrder}>
            <Group>
              <TextInput
                name='orderTextSearch'
                placeholder='Search by Id or Description'
                value={orderTextSearch}
                onChange={handleInputChange}
              />
              <ActionIcon type='submit' variant='light' color='violet'>
                <IconSearch size={16} />
              </ActionIcon>
            </Group>
          </form>
        </Grid.Col>
        <Grid.Col span={{ base: 12, sm: 7 }}>
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
            <Table.Th w={40} />
            <Table.Th>ID</Table.Th>
            <Table.Th>Username</Table.Th>
            <Table.Th>Created At</Table.Th>
            <Table.Th>Description</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>{orderList}</Table.Tbody>
      </Table>
    </>
  )
}

export default OrderTable
