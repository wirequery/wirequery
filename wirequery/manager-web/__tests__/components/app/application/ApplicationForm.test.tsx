import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { ApplicationForm } from '@components/app/application/ApplicationForm'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('ApplicationForm', () => {
  const application = {
    id: '1',
    name: 'Some name',
    description: 'Some description',
  }

  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <ApplicationForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Name')).not.toBeNull()
    expect(screen.queryByText('Description')).not.toBeNull()
  })

  it('renders form containing existing data if id is passed', async () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          application,
        },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <ApplicationForm id="1" onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    const expectedFormValues = {
      ...application,
      id: undefined,
      name: undefined,
    }
    delete expectedFormValues.id
    delete expectedFormValues.name
    await waitFor(() =>
      expect(screen.getByRole('form')).toHaveFormValues(expectedFormValues)
    )
  })

  it('calls a mutation if Save is clicked if there is an id', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: { application },
      })
    )
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
        <ApplicationForm id="1" onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })

  it('calls a mutation if Save is clicked if there is no id', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue({
        data: {},
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <ApplicationForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.change(screen.getByLabelText('Name', { exact: false }), {
      target: { value: 'SomeName' },
    })
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })

  it('calls no mutation if Save when validation fails', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue({
        data: {},
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <ApplicationForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    expect(mockClient.executeMutation).not.toBeCalled()
    expect(saveFn).not.toBeCalled()
  })
})