import { TemplateForm } from '@components/app/template/TemplateForm'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('TemplateForm', () => {
  const template = {
    id: '1',
    name: 'Some name',
    description: 'Some description',
    fields: [],
    nameTemplate: 'Some nameTemplate',
    descriptionTemplate: 'Some descriptionTemplate',
    allowUserInitiation: true,
  }

  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <TemplateForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Name')).not.toBeNull()
    expect(screen.queryByText('Description')).not.toBeNull()
    expect(screen.queryByText('Name Template')).not.toBeNull()
    expect(screen.queryByText('Description Template')).not.toBeNull()
  })

  it('renders form containing existing data if id is passed', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          template,
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
        <TemplateForm id="1" onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    const expectedFormValues = {
      ...template,
      id: undefined,
    }
    delete expectedFormValues.id
    delete expectedFormValues.fields
    expect(screen.getByRole('form')).toHaveFormValues(expectedFormValues)
  })

  it('calls a mutation if Save is clicked if there is an id', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: { template },
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
        <TemplateForm id="1" onSave={saveFn} onCancel={jest.fn()} />
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
        <TemplateForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.change(screen.getAllByLabelText('Name', { exact: false })[0], {
      target: { value: 'Name' },
    })
    fireEvent.change(
      screen.getAllByLabelText('Description', { exact: false })[0],
      {
        target: { value: 'Description' },
      }
    )
    fireEvent.change(screen.getByLabelText('Name Template', { exact: false }), {
      target: { value: 'Name' },
    })
    fireEvent.change(
      screen.getByLabelText('Description Template', { exact: false }),
      {
        target: { value: 'Description' },
      }
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })
})