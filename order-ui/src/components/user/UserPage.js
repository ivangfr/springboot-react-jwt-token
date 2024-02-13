import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { Container } from 'semantic-ui-react'
import OrderTable from './OrderTable'
import { useAuth } from '../context/AuthContext'
import { orderApi } from '../misc/OrderApi'
import { handleLogError } from '../misc/Helpers'

function UserPage() {
  const Auth = useAuth()
  const user = Auth.getUser()
  const isUser = user.data.rol[0] === 'USER'

  const [userMe, setUserMe] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [orderDescription, setOrderDescription] = useState('')

  useEffect(() => {
    async function fetchData() {
      setIsLoading(true)

      try {
        const response = await orderApi.getUserMe(user)
        setUserMe(response.data)
      } catch (error) {
        handleLogError(error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [])

  const handleInputChange = (e, { name, value }) => {
    if (name === 'orderDescription') {
      setOrderDescription(value)
    }
  }

  const handleCreateOrder = async () => {
    let trimmedDescription = orderDescription.trim()
    if (!trimmedDescription) {
      return
    }

    const order = { description: trimmedDescription }
    try {
      await orderApi.createOrder(user, order)
      await fetchUserMeData()
      setOrderDescription('')
    } catch (error) {
      handleLogError(error)
    }
  }

  const fetchUserMeData = async () => {
    setIsLoading(true)

    try {
      const response = await orderApi.getUserMe(user)
      setUserMe(response.data)
    } catch (error) {
      handleLogError(error)
    } finally {
      setIsLoading(false)
    }
  }

  if (!isUser) {
    return <Navigate to='/' />
  }

  return (
    <Container>
      <OrderTable
        orders={userMe && userMe.orders}
        isLoading={isLoading}
        orderDescription={orderDescription}
        handleCreateOrder={handleCreateOrder}
        handleInputChange={handleInputChange}
      />
    </Container>
  )
}

export default UserPage
