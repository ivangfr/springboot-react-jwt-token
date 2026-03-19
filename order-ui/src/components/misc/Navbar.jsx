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
    <AppShell.Header p='sm' bg='violet'>
      <Group justify='space-between' h='100%'>
        <Group>
          <Text fw={700} size='lg' c='white'>Order-UI</Text>
          <Anchor component={Link} to='/' c='white'>Home</Anchor>
          {isAdmin && <Anchor component={Link} to='/adminpage' c='white'>AdminPage</Anchor>}
          {isUser && <Anchor component={Link} to='/userpage' c='white'>UserPage</Anchor>}
        </Group>
        <Group>
          {!isAuthenticated && <Anchor component={Link} to='/login' c='white'>Login</Anchor>}
          {!isAuthenticated && <Anchor component={Link} to='/signup' c='white'>Sign Up</Anchor>}
          {isAuthenticated && <Text size='sm' c='white'>Hi {userName}</Text>}
          {isAuthenticated && <Button variant='white' color='violet' size='sm' onClick={handleLogout}>Logout</Button>}
        </Group>
      </Group>
    </AppShell.Header>
  )
}

export default Navbar
