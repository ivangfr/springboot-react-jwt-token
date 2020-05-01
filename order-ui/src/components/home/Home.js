import React, { Component } from 'react'
import { Statistic, Icon, Grid, Container, Image, Segment } from 'semantic-ui-react'
import { orderApi } from '../misc/OrderApi'

class Home extends Component {
  state = {
    numberOfUsers: 0,
    numberOfOrders: 0,
    isLoadingNumberOfUsers: false,
    isLoadingNumberOfOrders: false,
  }

  componentDidMount() {
    this.getNumberOfUsers()
    this.getNumberOfOrders()
  }

  getNumberOfUsers = () => {
    this.setState({ isLoadingNumberOfUsers: true })
    orderApi.numberOfUsers()
      .then(response => {
        this.setState({ numberOfUsers: response.data })
      })
      .catch(error => {
        console.log(error)
      })
      .finally(() => {
        this.setState({ isLoadingNumberOfUsers: false })
      })
  }

  getNumberOfOrders = () => {
    this.setState({ getNumberOfOrders: true })
    orderApi.numberOfOrders()
      .then(response => {
        this.setState({ numberOfOrders: response.data })
      })
      .catch(error => {
        console.log(error)
      })
      .finally(() => {
        this.setState({ getNumberOfOrders: false })
      })
  }

  render() {
    const { isLoadingNumberOfUsers, numberOfUsers, isLoadingNumberOfOrders, numberOfOrders } = this.state
    return (
      <Container text>
        <Grid stackable columns={2}>
          <Grid.Row>
            <Grid.Column textAlign='center'>
              <Segment color='violet' loading={isLoadingNumberOfUsers}>
                <Statistic>
                  <Statistic.Value><Icon name='user' color='grey' />{numberOfUsers}</Statistic.Value>
                  <Statistic.Label>Users</Statistic.Label>
                </Statistic>
              </Segment>
            </Grid.Column>
            <Grid.Column textAlign='center'>
              <Segment color='violet' loading={isLoadingNumberOfOrders}>
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
}

export default Home