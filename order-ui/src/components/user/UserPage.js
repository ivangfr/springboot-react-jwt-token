import React, { Component } from 'react'
import { Redirect } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import OrderTable from './OrderTable'
import AuthContext from '../context/AuthContext'
import { orderApi } from '../misc/OrderApi'

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

    this.getUserMe()
  }

  handleChange = (e) => {
    const { id, value } = e.target
    this.setState({ [id]: value })
  }

  getUserMe = () => {
    const Auth = this.context
    const user = Auth.getUser()

    this.setState({ isLoading: true })
    orderApi.getUserMe(user)
      .then(response => {
        this.setState({ userMe: response.data })
      })
      .catch(error => {
        console.log(error)
      })
      .finally(() => {
        this.setState({ isLoading: false })
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
        this.getUserMe()
        this.setState({ orderDescription: '' })
      })
      .catch(error => {
        console.log(error)
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
            createOrder={this.createOrder}
            handleChange={this.handleChange}
          />
        </Container>
      )
    }
  }
}

export default UserPage