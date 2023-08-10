import React, { useEffect, useState } from 'react'
import { Statistic, Icon, Grid, Container, Image, Segment, Dimmer, Loader } from 'semantic-ui-react'
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
        const numberOfUsers = responseUsers.data

        const responseOrders = await orderApi.numberOfOrders()
        const numberOfOrders = responseOrders.data

        setNumberOfUsers(numberOfUsers)
        setNumberOfOrders(numberOfOrders)
      } catch (error) {
        handleLogError(error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [])

  if (isLoading) {
    return (
      <Segment basic style={{ marginTop: window.innerHeight / 2 }}>
        <Dimmer active inverted>
          <Loader inverted size='huge'>Loading</Loader>
        </Dimmer>
      </Segment>
    )
  }

  return (
    <Container text>
      <Grid stackable columns={2}>
        <Grid.Row>
          <Grid.Column textAlign='center'>
            <Segment color='violet'>
              <Statistic>
                <Statistic.Value><Icon name='user' color='grey' />{numberOfUsers}</Statistic.Value>
                <Statistic.Label>Users</Statistic.Label>
              </Statistic>
            </Segment>
          </Grid.Column>
          <Grid.Column textAlign='center'>
            <Segment color='violet'>
              <Statistic>
                <Statistic.Value><Icon name='laptop' color='grey' />{numberOfOrders}</Statistic.Value>
                <Statistic.Label>Orders</Statistic.Label>
              </Statistic>
            </Segment>
          </Grid.Column>
        </Grid.Row>
      </Grid>

      <Image src='https://react.semantic-ui.com/images/wireframe/media-paragraph.png' style={{ marginTop: '2em' }} />
      <Image src='https://react.semantic-ui.com/images/wireframe/paragraph.png' style={{ marginTop: '2em' }} />
    </Container>
  )
}

export default Home