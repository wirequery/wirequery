import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { SessionForm } from '@components/app/session/SessionForm'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('SessionForm', () => {
  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <SessionForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Template')).not.toBeNull()
    expect(screen.queryByText('Ends at')).not.toBeNull()
  })

  it('calls a mutation if Save is clicked', async () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          templates: [
            {
              id: '1',
              name: 'Some template',
              fields: [],
            },
          ],
        },
      })
    )
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue({
        data: {},
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <SessionForm templateId={'1'} onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })
})
