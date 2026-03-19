import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Group, Button, Anchor, AppShell, Text } from '@mantine/core'
import { useAuth } from '../context/AuthContext'

function Navbar() {
  const { getUser, userIsAuthenticated, userLogout } = useAuth()
  const navigate = useNavigate()

  const isAuthenticated = userIsAuthenticated()
  const user = getUser()
  const isAdmin = user && user.data.rol[0] === 'ADMIN'
  const isUser = user && user.data.rol[0] === 'USER'
  const userName = user ? user.data.name : ''

  const handleLogout = () => {
    userLogout()
    navigate('/')
  }

  return (
    <AppShell.Header p='sm'>
      <Group justify='space-between' h='100%'>
        <Group>
          <Text fw={700} size='lg' c='violet'>Order-UI</Text>
          <Anchor component={Link} to='/' c='dark'>Home</Anchor>
          {isAdmin && <Anchor component={Link} to='/adminpage' c='dark'>AdminPage</Anchor>}
          {isUser && <Anchor component={Link} to='/userpage' c='dark'>UserPage</Anchor>}
        </Group>
        <Group>
          {!isAuthenticated && <Anchor component={Link} to='/login' c='dark'>Login</Anchor>}
          {!isAuthenticated && <Anchor component={Link} to='/signup' c='dark'>Sign Up</Anchor>}
          {isAuthenticated && <Text size='sm'>Hi {userName}</Text>}
          {isAuthenticated && <Button variant='light' color='violet' size='sm' onClick={handleLogout}>Logout</Button>}
        </Group>
      </Group>
    </AppShell.Header>
  )
}

export default Navbar
