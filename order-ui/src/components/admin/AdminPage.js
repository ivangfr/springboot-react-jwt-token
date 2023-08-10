import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import { useAuth } from '../context/AuthContext'
import AdminTab from './AdminTab'
import { orderApi } from '../misc/OrderApi'
import { handleLogError } from '../misc/Helpers'

function AdminPage() {
  const Auth = useAuth()
  const user = Auth.getUser()

  const [users, setUsers] = useState([])
  const [orders, setOrders] = useState([])
  const [orderDescription, setOrderDescription] = useState('')
  const [orderTextSearch, setOrderTextSearch] = useState('')
  const [userUsernameSearch, setUserUsernameSearch] = useState('')
  const [isAdmin, setIsAdmin] = useState(true)
  const [isUsersLoading, setIsUsersLoading] = useState(false)
  const [isOrdersLoading, setIsOrdersLoading] = useState(false)

  useEffect(() => {
    setIsAdmin(user.data.rol[0] === 'ADMIN')
    handleGetUsers()
    handleGetOrders()
  }, [])

  const handleInputChange = (e, { name, value }) => {
    if (name === 'userUsernameSearch') {
      setUserUsernameSearch(value)
    } else if (name === 'orderDescription') {
      setOrderDescription(value)
    } else if (name === 'orderTextSearch') {
      setOrderTextSearch(value)
    }
  }

  const handleGetUsers = async () => {
    setIsUsersLoading(true)
    try {
      const response = await orderApi.getUsers(user)
      setUsers(response.data)
    } catch (error) {
      handleLogError(error)
    } finally {
      setIsUsersLoading(false)
    }
  }

  const handleDeleteUser = async (username) => {
    try {
      await orderApi.deleteUser(user, username)
      handleGetUsers()
    } catch (error) {
      handleLogError(error)
    }
  }

  const handleSearchUser = async () => {
    const username = userUsernameSearch
    try {
      const response = await orderApi.getUsers(user, username)
      const data = response.data
      const users = data instanceof Array ? data : [data]
      setUsers(users)
    } catch (error) {
      handleLogError(error)
      setUsers([])
    }
  }

  const handleGetOrders = async () => {
    setIsOrdersLoading(true)
    try {
      const response = await orderApi.getOrders(user)
      setOrders(response.data)
    } catch (error) {
      handleLogError(error)
    } finally {
      setIsOrdersLoading(false)
    }
  }

  const handleDeleteOrder = async (isbn) => {
    try {
      await orderApi.deleteOrder(user, isbn)
      handleGetOrders()
    } catch (error) {
      handleLogError(error)
    }
  }

  const handleCreateOrder = async () => {
    let description = orderDescription.trim()
    if (!description) {
      return
    }

    const order = { description }
    try {
      await orderApi.createOrder(user, order)
      handleGetOrders()
      setOrderDescription('')
    } catch (error) {
      handleLogError(error)
    }
  }

  const handleSearchOrder = async () => {
    const text = orderTextSearch
    try {
      const response = await orderApi.getOrders(user, text)
      setOrders(response.data)
    } catch (error) {
      handleLogError(error)
      setOrders([])
    }
  }

  if (!isAdmin) {
    return <Navigate to='/' />
  }

  return (
    <Container>
      <AdminTab
        isUsersLoading={isUsersLoading}
        users={users}
        userUsernameSearch={userUsernameSearch}
        handleDeleteUser={handleDeleteUser}
        handleSearchUser={handleSearchUser}
        isOrdersLoading={isOrdersLoading}
        orders={orders}
        orderDescription={orderDescription}
        orderTextSearch={orderTextSearch}
        handleCreateOrder={handleCreateOrder}
        handleDeleteOrder={handleDeleteOrder}
        handleSearchOrder={handleSearchOrder}
        handleInputChange={handleInputChange}
      />
    </Container>
  )
}

export default AdminPage