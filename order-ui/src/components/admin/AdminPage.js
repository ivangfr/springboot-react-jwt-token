import React, { Component } from 'react'
import { Redirect } from 'react-router-dom'
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

  handleInputChange = (e, {name, value}) => {
    this.setState({ [name]: value })
  }

  handleGetUsers = () => {
    const Auth = this.context
    const user = Auth.getUser()

    this.setState({ isUsersLoading: true })
    orderApi.getUsers(user)
      .then(response => {
        this.setState({ users: response.data })
      })
      .catch(error => {
        handleLogError(error)
      })
      .finally(() => {
        this.setState({ isUsersLoading: false })
      })
  }

  handleDeleteUser = (username) => {
    const Auth = this.context
    const user = Auth.getUser()

    orderApi.deleteUser(user, username)
      .then(() => {
        this.handleGetUsers()
      })
      .catch(error => {
        handleLogError(error)
      })
  }

  handleSearchUser = () => {
    const Auth = this.context
    const user = Auth.getUser()

    const username = this.state.userUsernameSearch
    orderApi.getUsers(user, username)
      .then(response => {
        const data = response.data
        const users = data instanceof Array ? data : [data]
        this.setState({ users })
      })
      .catch(error => {
        handleLogError(error)
        this.setState({ users: [] })
      })
  }

  handleGetOrders = () => {
    const Auth = this.context
    const user = Auth.getUser()

    this.setState({ isOrdersLoading: true })
    orderApi.getOrders(user)
      .then(response => {
        this.setState({ orders: response.data })
      })
      .catch(error => {
        handleLogError(error)
      })
      .finally(() => {
        this.setState({ isOrdersLoading: false })
      })
  }

  handleDeleteOrder = (isbn) => {
    const Auth = this.context
    const user = Auth.getUser()

    orderApi.deleteOrder(user, isbn)
      .then(() => {
        this.handleGetOrders()
      })
      .catch(error => {
        handleLogError(error)
      })
  }

  handleCreateOrder = () => {
    const Auth = this.context
    const user = Auth.getUser()

    let { orderDescription } = this.state
    orderDescription = orderDescription.trim()
    if (!orderDescription) {
      return
    }

    const order = { description: orderDescription }
    orderApi.createOrder(user, order)
      .then(() => {
        this.handleGetOrders()
        this.setState({ orderDescription: '' })
      })
      .catch(error => {
        handleLogError(error)
      })
  }

  handleSearchOrder = () => {
    const Auth = this.context
    const user = Auth.getUser()

    const text = this.state.orderTextSearch
    orderApi.getOrders(user, text)
      .then(response => {
        const orders = response.data
        this.setState({ orders })
      })
      .catch(error => {
        handleLogError(error)
        this.setState({ orders: [] })
      })
  }

  render() {
    if (!this.state.isAdmin) {
      return <Redirect to='/' />
    } else {
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
}

export default AdminPage