import { Group, TextInput, Button } from '@mantine/core'
import { IconPlus } from '@tabler/icons-react'

function OrderForm({ orderDescription, handleInputChange, handleCreateOrder }) {
  const createBtnDisabled = orderDescription.trim() === ''
  return (
    <form onSubmit={handleCreateOrder}>
      <Group>
        <TextInput
          name='orderDescription'
          placeholder='Description *'
          value={orderDescription}
          onChange={handleInputChange}
        />
        <Button
          type='submit'
          leftSection={<IconPlus size={16} />}
          disabled={createBtnDisabled}
          color='violet'
        >
          Create
        </Button>
      </Group>
    </form>
  )
}

export default OrderForm
