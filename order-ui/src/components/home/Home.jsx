import { useEffect, useState } from 'react'
import { SimpleGrid, Paper, Text, Container, Box, LoadingOverlay } from '@mantine/core'
import { IconUsers, IconDeviceLaptop } from '@tabler/icons-react'
import { orderApi } from '../misc/OrderApi'
import { handleLogError } from '../misc/Helpers'

function Home() {
  const [numberOfUsers, setNumberOfUsers] = useState(0)
  const [numberOfOrders, setNumberOfOrders] = useState(0)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    async function fetchData() {
      try {
        const responseUsers = await orderApi.numberOfUsers()
        const responseOrders = await orderApi.numberOfOrders()
        setNumberOfUsers(responseUsers.data)
        setNumberOfOrders(responseOrders.data)
      } catch (error) {
        handleLogError(error)
      } finally {
        setIsLoading(false)
      }
    }
    fetchData()
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <Container size='sm' mt='xl'>
      <Box pos='relative' mih={120}>
        <LoadingOverlay visible={isLoading} />
        <SimpleGrid cols={{ base: 1, sm: 2 }}>
          <Paper withBorder p='xl' radius='md' ta='center'>
            <IconUsers size={32} color='gray' />
            <Text size='3rem' fw={700}>{numberOfUsers}</Text>
            <Text c='dimmed'>Users</Text>
          </Paper>
          <Paper withBorder p='xl' radius='md' ta='center'>
            <IconDeviceLaptop size={32} color='gray' />
            <Text size='3rem' fw={700}>{numberOfOrders}</Text>
            <Text c='dimmed'>Orders</Text>
          </Paper>
        </SimpleGrid>
      </Box>
    </Container>
  )
}

export default Home
