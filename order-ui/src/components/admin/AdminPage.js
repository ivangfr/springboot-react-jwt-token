import React, { Component } from 'react'
import { Redirect } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import AuthContext from '../context/AuthContext'
import { orderApi } from '../misc/OrderApi'
import AdminTab from './AdminTab'

class AdminPage extends Component {
  static contextType = AuthContext

  state = {
    users: [],
    orders: [],
    orderDescription: '',
    textTextSearch: '',
    userUsernameSearch: '',
    isAdmin: true,
    isUsersLoading: false,
    isOrdersLoading: false,
  }

  componentDidMount() {
    const Auth = this.context
    const user = Auth.getUser()
    const isAdmin = user.role === 'ADMIN'
    this.setState({ isAdmin })

    this.getUsers()
    this.getOrders()
  }

  handleChange = (e) => {
    const { id, value } = e.target
    this.setState({ [id]: value })
  }

  getUsers = () => {
    const Auth = this.context
    const user = Auth.getUser()

    this.setState({ isUsersLoading: true })
    orderApi.getUsers(user)
      .then(response => {
        this.setState({ users: response.data })
      })
      .catch(error => {
        console.log(error)
      })
      .finally(() => {
        this.setState({ isUsersLoading: false })
      })
  }

  deleteUser = (username) => {
    const Auth = this.context
    const user = Auth.getUser()

    orderApi.deleteUser(user, username)
      .then(() => {
        this.getUsers()
      })
      .catch(error => {
        console.log(error)
      })
  }

  searchUser = () => {
    const Auth = this.context
    const user = Auth.getUser()

    const username = this.state.userUsernameSearch
    orderApi.getUsers(user, username)
      .then(response => {
        if (response.status === 200) {
          const data = response.data;
          const users = data instanceof Array ? data : [data]
          this.setState({ users })
        } else {
          this.setState({ users: [] })
        }
      })
      .catch(error => {
        console.log(error)
        this.setState({ users: [] })
      })
  }

  getOrders = () => {
    const Auth = this.context
    const user = Auth.getUser()

    this.setState({ isOrdersLoading: true })
    orderApi.getOrders(user)
      .then(response => {
        this.setState({ orders: response.data })
      })
      .catch(error => {
        console.log(error)
      })
      .finally(() => {
        this.setState({ isOrdersLoading: false })
      })
  }

  deleteOrder = (isbn) => {
    const Auth = this.context
    const user = Auth.getUser()

    orderApi.deleteOrder(user, isbn)
      .then(() => {
        this.getOrders()
      })
      .catch(error => {
        console.log(error)
      })
  }

  createOrder = () => {
    const Auth = this.context
    const user = Auth.getUser()

    const { orderDescription } = this.state
    if (!orderDescription) {
      return
    }

    const order = { description: orderDescription }
    orderApi.createOrder(user, order)
      .then(() => {
        this.clearOrderForm()
        this.getOrders()
      })
      .catch(error => {
        console.log(error)
      })
  }

  searchOrder = () => {
    const Auth = this.context
    const user = Auth.getUser()

    const text = this.state.textTextSearch
    orderApi.getOrders(user, text)
      .then(response => {
        if (response.status === 200) {
          const data = response.data;
          const orders = data instanceof Array ? data : [data]
          this.setState({ orders })
        } else {
          this.setState({ orders: [] })
        }
      })
      .catch(error => {
        console.log(error)
        this.setState({ orders: [] })
      })
  }

  clearOrderForm = () => {
    this.setState({
      orderId: '',
      orderDescription: ''
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
            deleteUser={this.deleteUser}
            searchUser={this.searchUser}
            isOrdersLoading={isOrdersLoading}
            orders={orders}
            orderDescription={orderDescription}
            orderTextSearch={orderTextSearch}
            createOrder={this.createOrder}
            deleteOrder={this.deleteOrder}
            searchOrder={this.searchOrder}
            handleChange={this.handleChange}
          />
        </Container>
      )
    }
  }
}

export default AdminPage