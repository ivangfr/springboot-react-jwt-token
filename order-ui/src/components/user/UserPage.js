import React, { Component } from 'react'
import { Redirect } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import OrderTable from './OrderTable'
import AuthContext from '../context/AuthContext'
import { orderApi } from '../misc/OrderApi'
import { handleLogError } from '../misc/Helpers'

class UserPage extends Component {
  static contextType = AuthContext

  state = {
    userMe: null,
    isUser: true,
    isLoading: false,
    orderDescription: ''
  }

  componentDidMount() {
    const Auth = this.context
    const user = Auth.getUser()
    const isUser = user.data.rol[0] === 'USER'
    this.setState({ isUser })

    this.handleGetUserMe()
  }

  handleInputChange = (e, {name, value}) => {
    this.setState({ [name]: value })
  }

  handleGetUserMe = () => {
    const Auth = this.context
    const user = Auth.getUser()

    this.setState({ isLoading: true })
    orderApi.getUserMe(user)
      .then(response => {
        this.setState({ userMe: response.data })
      })
      .catch(error => {
        handleLogError(error)
      })
      .finally(() => {
        this.setState({ isLoading: false })
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
        this.handleGetUserMe()
        this.setState({ orderDescription: '' })
      })
      .catch(error => {
        handleLogError(error)
      })
  }

  render() {
    if (!this.state.isUser) {
      return <Redirect to='/' />
    } else {
      const { userMe, isLoading, orderDescription } = this.state
      return (
        <Container>
          <OrderTable
            orders={userMe && userMe.orders}
            isLoading={isLoading}
            orderDescription={orderDescription}
            handleCreateOrder={this.handleCreateOrder}
            handleInputChange={this.handleInputChange}
          />
        </Container>
      )
    }
  }
}

export default UserPage