import React from 'react'
import { Tab } from 'semantic-ui-react'
import UserTable from './UserTable'
import OrderTable from './OrderTable'

function AdminTab(props) {
  const { handleChange } = props
  const { isUsersLoading, users, userUsernameSearch, deleteUser, searchUser } = props
  const { isOrdersLoading, orders, orderDescription, orderTextSearch, createOrder, deleteOrder, searchOrder } = props

  const panes = [
    {
      menuItem: { key: 'users', content: 'Users' },
      render: () => (
        <Tab.Pane loading={isUsersLoading}>
          <UserTable
            users={users}
            userUsernameSearch={userUsernameSearch}
            handleChange={handleChange}
            deleteUser={deleteUser}
            searchUser={searchUser}
          />
        </Tab.Pane>
      )
    },
    {
      menuItem: { key: 'orders', content: 'Orders' },
      render: () => (
        <Tab.Pane loading={isOrdersLoading}>
          <OrderTable
            orders={orders}
            orderDescription={orderDescription}
            orderTextSearch={orderTextSearch}
            handleChange={handleChange}
            createOrder={createOrder}
            deleteOrder={deleteOrder}
            searchOrder={searchOrder}
          />
        </Tab.Pane>
      )
    }
  ]

  return (
    <Tab menu={{ attached: 'top' }} panes={panes} />
  )
}

export default AdminTab