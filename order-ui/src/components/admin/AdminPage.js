import React, { Component } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import AuthContext from '../context/AuthContext'
import { orderApi } from '../misc/OrderApi'
import AdminTab from './AdminTab'
import { handleLogError } from '../misc/Helpers'

class AdminPage extends Component {
  static contextType = AuthContext

  state = {
    users: [],
    orders: [],
    orderDescription: '',
    orderTextSearch: '',
    userUsernameSearch: '',
    isAdmin: true,
    isUsersLoading: false,
    isOrdersLoading: false,
  }

  componentDidMount() {
    const Auth = this.context
    const user = Auth.getUser()
    const isAdmin = user.data.rol[0] === 'ADMIN'
    this.setState({ isAdmin })

    this.handleGetUsers()
    this.handleGetOrders()
  }

  handleInputChange = (e, { name, value }) => {
    this.setState({ [name]: value })
  }

  handleGetUsers = async () => {
    const user = this.context.getUser()

    this.setState({ isUsersLoading: true })
    try {
      const response = await orderApi.getUsers(user)
      this.setState({ users: response.data })
    } catch (error) {
      handleLogError(error)
    } finally {
      this.setState({ isUsersLoading: false })
    }
  }

  handleDeleteUser = async (username) => {
    const user = this.context.getUser()

    try {
      await orderApi.deleteUser(user, username)
      await this.handleGetUsers()
    } catch (error) {
      handleLogError(error)
    }
  }

  handleSearchUser = async () => {
    const user = this.context.getUser()

    const username = this.state.userUsernameSearch
    try {
      const response = await orderApi.getUsers(user, username)
      const data = response.data
      const users = data instanceof Array ? data : [data]
      this.setState({ users })
    } catch (error) {
      handleLogError(error)
      this.setState({ users: [] })
    }
  }

  handleGetOrders = async () => {
    const user = this.context.getUser()

    this.setState({ isOrdersLoading: true })
    try {
      const response = await orderApi.getOrders(user)
      this.setState({ orders: response.data })
    } catch (error) {
      handleLogError(error)
    } finally {
      this.setState({ isOrdersLoading: false })
    }
  }

  handleDeleteOrder = async (isbn) => {
    const user = this.context.getUser()

    try {
      await orderApi.deleteOrder(user, isbn)
      await this.handleGetOrders()
    } catch (error) {
      handleLogError(error)
    }
  }

  handleCreateOrder = async () => {
    const user = this.context.getUser()

    let { orderDescription } = this.state
    orderDescription = orderDescription.trim()
    if (!orderDescription) {
      return
    }

    const order = { description: orderDescription }
    try {
      await orderApi.createOrder(user, order)
      await this.handleGetOrders()
      this.setState({ orderDescription: '' })
    } catch (error) {
      handleLogError(error)
    }
  }

  handleSearchOrder = async () => {
    const user = this.context.getUser()

    const text = this.state.orderTextSearch
    try {
      const response = await orderApi.getOrders(user, text)
      const orders = response.data
      this.setState({ orders })
    } catch (error) {
      handleLogError(error)
      this.setState({ orders: [] })
    }
  }

  render() {
    if (!this.state.isAdmin) {
      return <Navigate to='/' />
    }

    const { isUsersLoading, users, userUsernameSearch, isOrdersLoading, orders, orderDescription, orderTextSearch } = this.state
    return (
      <Container>
        <AdminTab
          isUsersLoading={isUsersLoading}
          users={users}
          userUsernameSearch={userUsernameSearch}
          handleDeleteUser={this.handleDeleteUser}
          handleSearchUser={this.handleSearchUser}
          isOrdersLoading={isOrdersLoading}
          orders={orders}
          orderDescription={orderDescription}
          orderTextSearch={orderTextSearch}
          handleCreateOrder={this.handleCreateOrder}
          handleDeleteOrder={this.handleDeleteOrder}
          handleSearchOrder={this.handleSearchOrder}
          handleInputChange={this.handleInputChange}
        />
      </Container>
    )
  }
}

export default AdminPage