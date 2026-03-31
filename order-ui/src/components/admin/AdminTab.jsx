import { Tabs, Box, LoadingOverlay } from '@mantine/core'
import { IconUsers, IconDeviceLaptop } from '@tabler/icons-react'
import UserTable from './UserTable'
import OrderTable from './OrderTable'

function AdminTab(props) {
  const { handleInputChange } = props
  const { isUsersLoading, users, userUsernameSearch, handleDeleteUser, handleSearchUser } = props
  const { isOrdersLoading, orders, orderDescription, orderTextSearch, handleCreateOrder, handleDeleteOrder, handleSearchOrder } = props

  return (
    <Tabs defaultValue='users' mt='md'>
      <Tabs.List>
        <Tabs.Tab value='users' leftSection={<IconUsers size={16} />}>Users</Tabs.Tab>
        <Tabs.Tab value='orders' leftSection={<IconDeviceLaptop size={16} />}>Orders</Tabs.Tab>
      </Tabs.List>

      <Tabs.Panel value='users' pt='md'>
        <Box pos='relative'>
          <LoadingOverlay visible={isUsersLoading} />
          <UserTable
            users={users}
            userUsernameSearch={userUsernameSearch}
            handleInputChange={handleInputChange}
            handleDeleteUser={handleDeleteUser}
            handleSearchUser={handleSearchUser}
          />
        </Box>
      </Tabs.Panel>

      <Tabs.Panel value='orders' pt='md'>
        <Box pos='relative'>
          <LoadingOverlay visible={isOrdersLoading} />
          <OrderTable
            orders={orders}
            isOrdersLoading={isOrdersLoading}
            orderDescription={orderDescription}
            orderTextSearch={orderTextSearch}
            handleInputChange={handleInputChange}
            handleCreateOrder={handleCreateOrder}
            handleDeleteOrder={handleDeleteOrder}
            handleSearchOrder={handleSearchOrder}
          />
        </Box>
      </Tabs.Panel>
    </Tabs>
  )
}

export default AdminTab
