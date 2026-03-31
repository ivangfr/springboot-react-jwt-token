import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from '@mantine/core'
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
  const [isUsersLoading, setIsUsersLoading] = useState(true)
  const [isOrdersLoading, setIsOrdersLoading] = useState(true)

  useEffect(() => {
    setIsAdmin(user.data.rol[0] === 'ADMIN')
    handleGetUsers()
    handleGetOrders()
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleInputChange = (e) => {
    const { name, value } = e.target
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
    setIsUsersLoading(true)
    try {
      await orderApi.deleteUser(user, username)
      handleGetUsers()
    } catch (error) {
      handleLogError(error)
      setIsUsersLoading(false)
    }
  }

  const handleSearchUser = async (e) => {
    e.preventDefault()
    const username = userUsernameSearch
    setIsUsersLoading(true)
    try {
      const response = await orderApi.getUsers(user, username)
      const data = response.data
      const users = data instanceof Array ? data : [data]
      setUsers(users)
    } catch (error) {
      handleLogError(error)
      setUsers([])
    } finally {
      setIsUsersLoading(false)
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

  const handleDeleteOrder = async (orderId) => {
    setIsOrdersLoading(true)
    try {
      await orderApi.deleteOrder(user, orderId)
      handleGetOrders()
    } catch (error) {
      handleLogError(error)
      setIsOrdersLoading(false)
    }
  }

  const handleCreateOrder = async (e) => {
    e.preventDefault()
    const description = orderDescription.trim()
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

  const handleSearchOrder = async (e) => {
    e.preventDefault()
    const text = orderTextSearch
    setIsOrdersLoading(true)
    try {
      const response = await orderApi.getOrders(user, text)
      setOrders(response.data)
    } catch (error) {
      handleLogError(error)
      setOrders([])
    } finally {
      setIsOrdersLoading(false)
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
